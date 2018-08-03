package com.varian.oiscn.appointment.service;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.DeviceAntiCorruptionServiceImp;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.base.util.DevicesReader;
import com.varian.oiscn.cache.AppointmentCache;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.pagination.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RefreshAppointmentCacheService implements Runnable{
    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp = new AppointmentAntiCorruptionServiceImp();
    private DeviceAntiCorruptionServiceImp deviceAntiCorruptionServiceImp = new DeviceAntiCorruptionServiceImp();
    private static List<String> deviceIdList = Collections.synchronizedList(new ArrayList<>());
    //用于记录当前轮有哪些appointmentId还存在
    private static AtomicInteger currentMarkValue = new AtomicInteger(0);
    @Override
    public void run() {
        init();
        //获取这一轮的标记值
        Integer markValue = currentMarkValue.incrementAndGet();

        Calendar calendar = Calendar.getInstance();
        int subFirst = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
        calendar.add(Calendar.DAY_OF_MONTH,-subFirst);
        Date startDate = calendar.getTime();
        calendar = Calendar.getInstance();
        int addEnd = 7 - calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_MONTH,addEnd);
        Date endDate = calendar.getTime();

        String startDateStr =  DateUtil.formatDate(startDate,DateUtil.DATE_FORMAT);
        String endDateStr =  DateUtil.formatDate(endDate,DateUtil.DATE_FORMAT);

        List<String> keyList = AppointmentCache.allKeys();
        if(!keyList.isEmpty()){
            //获取最小和最大的dateStr
            String minDate = startDateStr;
            String maxDate = endDateStr;
            for(String key :keyList){
                String dateStr = StringUtils.split(key,":")[1];
                if(minDate.compareTo(dateStr) > 0){
                    minDate = dateStr;
                }
                if(maxDate.compareTo(dateStr) <0){
                    maxDate = dateStr;
                }
            }
            if(endDateStr.compareTo(maxDate) <0){
                endDateStr = maxDate;
            }
            if(startDateStr.compareTo(minDate) > 0){
//                清除本周之前的所有缓存
//                计算本周之前的所有日期
                try {
                    Date tmpStartDate = DateUtil.parse(startDateStr);
                    tmpStartDate =  DateUtil.addDay(tmpStartDate,-1);
                    List<String> dateStrList = DateUtil.splitDateRange(minDate,DateUtil.formatDate(tmpStartDate,DateUtil.DATE_FORMAT));
//                    清除缓存
                    for(String deviceId:deviceIdList){
                        for(String date:dateStrList) {
                            AppointmentCache.remove(deviceId,date);
                        }
                    }

                } catch (ParseException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }

//      存储最后一天的前一天
        String tmpEndDateStr = endDateStr;
        try {
            endDate = DateUtil.parse(endDateStr);
            endDate = DateUtil.addDay(endDate,-1);
            tmpEndDateStr = DateUtil.formatDate(endDate,DateUtil.DATE_FORMAT);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for(String deviceId : deviceIdList) {
            Pagination<AppointmentDto> pagination = appointmentAntiCorruptionServiceImp.syncAppointmentListByDeviceIdAndDateRangeAndPagination(Arrays.asList(deviceId), startDateStr, tmpEndDateStr,
                    Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED), AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)), Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
            if (pagination != null && pagination.getLstObject() != null) {
                pagination.getLstObject().forEach(appointmentDto -> {
                    AppointmentCache.put(appointmentDto);
                    AppointmentCache.markStillExisting(appointmentDto.getAppointmentId(), markValue);
                });
            }
//      处理最后一天跨零点
            Pagination<AppointmentDto> pagination2 = appointmentAntiCorruptionServiceImp.syncAppointmentListByDeviceIdAndDateRangeAndPagination(Arrays.asList(deviceId), endDateStr, null,
                    Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED), AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)), Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
            if (pagination2 != null && pagination2.getLstObject() != null) {
                pagination2.getLstObject().forEach(appointmentDto -> {
                    AppointmentCache.put(appointmentDto);
                    AppointmentCache.markStillExisting(appointmentDto.getAppointmentId(), markValue);
                });
            }
        }
        //这一轮没有进行标记的appointmentId应该被remove掉
        AppointmentCache.removeAllNonExistingAppointments(markValue);
    }

    private void init(){
        if(deviceIdList.isEmpty()){
            List<DeviceDto> deviceList =  DevicesReader.getAllDeviceDto();
            deviceList.forEach(deviceDto -> {
                DeviceDto dto = deviceAntiCorruptionServiceImp.queryDeviceByCode(deviceDto.getId());
                if(dto != null){
                    deviceIdList.add(dto.getId());
                }
            });
        }
    }
}
