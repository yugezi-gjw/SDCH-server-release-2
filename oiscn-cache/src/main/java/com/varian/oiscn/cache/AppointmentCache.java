package com.varian.oiscn.cache;

import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class AppointmentCache {

    @SuppressWarnings("unchecked")
    protected static CacheInterface<String, List<AppointmentDto>> cache = CacheFactory.getCache(CacheFactory.APPOINTMENT);

    private static Map<String, String> appointmentIdAndRealKeyMap = new ConcurrentHashMap<>();
    private static Map<String, Integer> appointmentIdAndIfDeletedMap = new ConcurrentHashMap<>();

    /**
     * 使用 DeviceId, Date.<br>
     *
     * @param deviceId
     * @param date
     * @return
     */
    public static List<AppointmentDto> get(String deviceId, String date) {
        return cache.get(toRealKey(deviceId, date));
    }

    /**
     * 根据 DeviceId, Date更新缓存.<br>
     *
     * @param deviceId
     * @param date
     * @param dto
     */
    public static void put(String deviceId, String date, AppointmentDto dto) {
        List<AppointmentDto> list = cache.get(toRealKey(deviceId, date));
        if (list == null) {
            list = Collections.synchronizedList(new ArrayList<>());
            cache.put(toRealKey(deviceId,date),list);
        }

        list.add(dto);
    }

    /**
     * 根据 DeviceId, Date更新缓存.<br>
     *
     * @param dto
     */
    public static void put(AppointmentDto dto) {
        //如果当前appointmentId已经存在，则直接从缓存中remove掉
        if(appointmentIdAndRealKeyMap.containsKey(dto.getAppointmentId())){
            //这里一定要从Map中取一次原来的realKey，因为新的dto里面可能有变化，比如日期改了，新的日期缓存中本身就不存在这一条数据
            remove(dto.getAppointmentId());
        }
        String deviceId = null;
        String date = null;
        for (ParticipantDto participantDto : dto.getParticipants()) {
            if (participantDto.getType() == ParticipantTypeEnum.DEVICE) {
                deviceId = participantDto.getParticipantId();
                date = formatDate(dto.getStartTime());
                break;
            }
        }
        if (isEmpty(deviceId) || isEmpty(date)) {
            return;
        }

        put(deviceId, date, dto);
        appointmentIdAndRealKeyMap.put(dto.getAppointmentId(), toRealKey(deviceId, date));
    }

    /**
     * remove cache
     * @param dto
     */
    public static void remove(AppointmentDto dto){
        if(!appointmentIdAndRealKeyMap.containsKey(dto.getAppointmentId())){
            return;
        }
        String deviceId = null;
        String date = null;
        for (ParticipantDto participantDto : dto.getParticipants()) {
            if (participantDto.getType() == ParticipantTypeEnum.DEVICE) {
                deviceId = participantDto.getParticipantId();
                date = formatDate(dto.getStartTime());
                break;
            }
        }
        if (isEmpty(deviceId) || isEmpty(date)) {
            return;
        }
        List<AppointmentDto> list = cache.get(toRealKey(deviceId, date));
        if (list != null) {
            int index = containsAppointment(list, dto.getAppointmentId());
            if (index > -1) {
                list.remove(index);
            }
        }
        appointmentIdAndRealKeyMap.remove(dto.getAppointmentId());
    }

    private static void remove(String appointmentId){
        List<AppointmentDto> list = cache.get(appointmentIdAndRealKeyMap.get(appointmentId));
        if (list != null) {
            int index = containsAppointment(list, appointmentId);
            if (index > -1) {
                list.remove(index);
            }
        }
        appointmentIdAndRealKeyMap.remove(appointmentId);
    }

    public static void markStillExisting(String appointmentId, Integer mark){
        appointmentIdAndIfDeletedMap.put(appointmentId, mark);
    }

    public static void removeAllNonExistingAppointments(Integer currentMarkValue){
        for(Iterator<Map.Entry<String, Integer>> iterator = appointmentIdAndIfDeletedMap.entrySet().iterator(); iterator.hasNext();){
            Map.Entry<String, Integer> entry = iterator.next();
            if(entry.getValue() != currentMarkValue){
                remove(entry.getKey());
                iterator.remove();
            }
        }
    }

    /**
     * remove cache
     * @param deviceId
     * @param date
     */
    public static void remove(String deviceId,String date){
        cache.remove(toRealKey(deviceId,date));
    }

    /**
     * 获取所有的Key
     * @return
     */
    public static List<String> allKeys(){
        return cache.keys();
    }

    private static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private static int containsAppointment(List<AppointmentDto> list, String targetId) {
        for (int i = 0; i < list.size(); i++) {
            if (StringUtils.equals(list.get(i).getAppointmentId(), targetId)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 变换为实际使用的Key.<br>
     *
     * @param deviceId
     * @param date
     * @return
     */
    protected static String toRealKey(String deviceId, String date) {
        return deviceId + ":" + date;
    }
}
