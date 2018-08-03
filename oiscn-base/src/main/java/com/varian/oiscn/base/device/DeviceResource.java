package com.varian.oiscn.base.device;

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.DeviceAntiCorruptionServiceImp;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.base.util.DeviceUtil;
import com.varian.oiscn.base.util.DevicesReader;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.config.DeviceTimeSettingConfiguration;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.carepath.CarePathConfigItem;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.device.DeviceSettingView;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathList;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.resource.AbstractResource;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gbt1220 on 5/18/2017.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class DeviceResource extends AbstractResource {

    private CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp;

    public DeviceResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
        carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
    }

    @Path("/devices/search")
    @GET
    public Response searchDevices(@Auth UserContext userContext,
                                  @QueryParam("code") String activityCode,
                                  @QueryParam("patientSer") Long patientSer,
                                  @QueryParam("instanceId") String instanceId) {
        List<String> deviceIds = new ArrayList<>();
        String realActivityCode = ActivityCodesReader.getSourceActivityCodeByRelativeCode(activityCode).getName();
        if(StringUtils.isEmpty(realActivityCode)){
            realActivityCode = activityCode;
        }
        if (patientSer != null) {
            if(SystemConfigPool.queryTreatmentActivityCode().equals(activityCode)){
                final List<String> tmpList = new ArrayList<>();
                final String retActCode = realActivityCode;
                List<CarePathInstance> carePathInstanceList = carePathAntiCorruptionServiceImp.queryAllCarePathByPatientID(String.valueOf(patientSer));
                PatientEncounterCarePath ecp = PatientEncounterHelper.getEncounterCarePathByPatientSer(String.valueOf(patientSer));
                if(ecp != null) {
                    EncounterCarePathList encounterCarePathList = ecp.getPlannedCarePath();
                    List<EncounterCarePath>  list = encounterCarePathList.getEncounterCarePathList();
                    carePathInstanceList.forEach(carePathInstance -> {
                        for(EncounterCarePath encounterCarePath : list){
                            if(encounterCarePath.getCpInstanceId().equals(new Long(carePathInstance.getId()))){
                                carePathInstance.getActivityInstances().forEach(activityInstance -> {
                                    if (retActCode.equals(activityInstance.getActivityCode())) {
                                        List<String> deviceIdList = activityInstance.getDeviceIDs();
                                        if (deviceIdList != null && !deviceIdList.isEmpty()) {
                                            deviceIdList.forEach(deviceId -> {
                                                if (!tmpList.contains(deviceId)) {
                                                    tmpList.add(deviceId);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                    deviceIds.addAll(tmpList);
                }
            }else{
                deviceIds = getByPatientSerAndActivityCode(realActivityCode, patientSer,instanceId);
            }
        } else {
            List<String> supportCarePathTemplateNameList = new ArrayList<>();
            String defaultTemplateName = configuration.getDefaultCarePathTemplateName();
            supportCarePathTemplateNameList.add(defaultTemplateName);
            List<CarePathConfigItem> carePathConfigItems = this.configuration.getCarePathConfig().getCarePath();
            if(carePathConfigItems != null){
                carePathConfigItems.forEach(carePathConfigItem -> supportCarePathTemplateNameList.add(carePathConfigItem.getTemplateId()));
            }
            if (!supportCarePathTemplateNameList.isEmpty()) {
                for(String templateId : supportCarePathTemplateNameList){
                    deviceIds.addAll(DeviceUtil.getDevicesByActivityCode(templateId, realActivityCode));
                }
//                deviceIds 去重
                Set<String> set = new HashSet<>();
                List<String> newList = new ArrayList<>();
                for(String deviceId : deviceIds){
                    if(set.add(deviceId)){
                        newList.add(deviceId);
                    }
                }
                set.clear();
                deviceIds = newList;
            }
        }
        if (deviceIds == null || deviceIds.isEmpty()) {
            return Response.status(Response.Status.OK).entity(new ArrayList<>()).build();
        } else {
            DeviceAntiCorruptionServiceImp deviceAntiCorruptionServiceImp = new DeviceAntiCorruptionServiceImp();
            List<DeviceDto> result = new ArrayList<>();
            DeviceDto deviceDto;
            DeviceDto deviceCfg;
            for (String deviceId : deviceIds) {
                deviceDto = deviceAntiCorruptionServiceImp.queryDeviceByID(deviceId);
                deviceCfg = DevicesReader.getDeviceTimeConfigureByCode(deviceDto.getCode());
                deviceDto.setTimeSlotList(deviceCfg.getTimeSlotList());
                deviceDto.setInterval(deviceCfg.getInterval());
                result.add(deviceDto);
            }
            return Response.status(Response.Status.OK).entity(result).build();
        }
    }

    private List<String> getByPatientSerAndActivityCode(String activityCode, Long patientSer,String instanceId) {
        List<String> deviceIds = new ArrayList<>();
        CarePathInstance carePathInstance;
        if(StringUtils.isNotEmpty(instanceId)) {
            carePathInstance = carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(patientSer.toString(), instanceId, ActivityTypeEnum.APPOINTMENT);
        }else{
            carePathInstance = carePathAntiCorruptionServiceImp.queryLastCarePathByPatientIDAndActivityCode(patientSer.toString(),activityCode);
        }
        if (carePathInstance == null) {
            return deviceIds;
        }
        //carepath instance里取回的device有重复的现象，所以需要去重
        Set<String> deviceIdSet = new HashSet<>();
        carePathInstance.getActivityInstances().stream().forEach(activityInstance -> {
            if (StringUtils.equalsIgnoreCase(activityInstance.getActivityCode(), activityCode) &&
                    activityInstance.getDeviceIDs() != null) {
                deviceIdSet.addAll(activityInstance.getDeviceIDs());
            }
        });
        deviceIds.addAll(deviceIdSet);
        deviceIdSet.clear();
        return deviceIds;
    }

    /**
     * 该方法原用于为所有不同类型的预约显示在患者详情页上提供默认时间段，现不再使用
     * @param userContext
     * @return
     */
    @Deprecated
    @Path("/devices/defaultTimeSetting")
    @GET
    public Response getDeviceDefaultTimeSetting(@Auth UserContext userContext){
        DeviceTimeSettingConfiguration deviceTimeSettingConfig = configuration.getDeviceTimeSettingConfiguration();
        if(deviceTimeSettingConfig != null){
            return Response.status(Response.Status.OK).entity(deviceTimeSettingConfig).build();
        }
        return Response.noContent().build();
    }

    /**
     * 根据设备code，获取该设备在devices.yaml中的配置信息
     * @param userContext
     * @return
     */
    @Path("/devices/setting/{deviceCode}")
    @GET
    public Response getDeviceSettingByCode(@Auth UserContext userContext,@PathParam("deviceCode") String code){
        DeviceDto deviceDto = DevicesReader.getDeviceTimeConfigureByCode(code);
        if(deviceDto == null){
            deviceDto = new DeviceDto();
        }
        DeviceSettingView deviceSettingView = new DeviceSettingView();
        deviceSettingView.setCode(deviceDto.getId());
        deviceSettingView.setInterval(deviceDto.getInterval());
        deviceSettingView.setTimeSlotList(deviceDto.getTimeSlotList());
        return Response.ok(deviceSettingView).build();
    }
}
