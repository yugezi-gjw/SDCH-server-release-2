package com.varian.oiscn.carepath.service;

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.base.common.CarePathInstanceHelper;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.config.LinkCPInDynamicFormConfiguration;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.linkcp.LinkCPInDynamicFormItem;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathServiceImpl;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.encounter.dynamicform.DynamicFormInstanceServiceImp;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
public class LinkCPInDynamicFormConfigService {

    private static List<LinkCPInDynamicFormItem> linkCPInDynamicFormItems;

    public static void init(String configFile) {
        if (isEmpty(configFile)) {
            log.warn("No link carePath configuration file found!!!");
            return;
        }
        LinkCPInDynamicFormConfiguration config = loadConfigFile(configFile);
        if (config == null) {
            log.warn("Link carePath configuration error!!!");
            return;
        }
        linkCPInDynamicFormItems = config.getLinkCPInDynamicFormItems();
    }

    private static LinkCPInDynamicFormConfiguration loadConfigFile(String configFile) {
        LinkCPInDynamicFormConfiguration config = null;
        File file = new File(configFile);
        if (file != null && file.exists() && file.isFile()) {
            try {
                config = new Yaml().loadAs(new FileInputStream(file), LinkCPInDynamicFormConfiguration.class);
            } catch (FileNotFoundException e) {
                log.warn("No link carePath configuration file !!! {}", e.getMessage());
            }
        }
        return config;
    }

    public static LinkCPInDynamicFormItem getItem(String activityCode) {
        if (linkCPInDynamicFormItems != null) {
            Optional<LinkCPInDynamicFormItem> optional = linkCPInDynamicFormItems.stream().filter(item -> StringUtils.equals(activityCode, item.getActivityCode())).findAny();
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    public static void linkOptionalCP(UserContext userContext, String activityCode, Long patientSer) {
        LinkCPInDynamicFormItem item = LinkCPInDynamicFormConfigService.getItem(activityCode);
        if (item != null) {
            DynamicFormInstanceServiceImp dynamicFormInstanceServiceImp = new DynamicFormInstanceServiceImp(userContext);
            String tagValue = dynamicFormInstanceServiceImp.queryFieldValueByPatientSerListAndFieldName(patientSer.toString(), item.getDynamicFormTag());
            if (Boolean.parseBoolean(tagValue)) {
                CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
                String instanceId = carePathAntiCorruptionServiceImp.linkCarePath(patientSer.toString(), SystemConfigPool.queryDefaultDepartment(), item.getCpTemplateId());
                if (StringUtils.isNotEmpty(instanceId)) {
                    OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp = new OrderAntiCorruptionServiceImp();
                    EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
                    //TODO: 将第一个节点的dueDate+2小时, 比如希望预约MRI在预约制模后面
                    CarePathInstance carePathInstance = carePathAntiCorruptionServiceImp.queryCarePathInstanceByInstanceId(instanceId);
                    CarePathInstanceHelper helper = new CarePathInstanceHelper(carePathInstance);
                    ActivityInstance firstActivity = helper.getFirstActivity();
                    if (isNotEmpty(firstActivity.getInstanceID()) && ActivityTypeEnum.TASK.equals(firstActivity.getActivityType())) {
                        OrderDto orderDto = orderAntiCorruptionServiceImp.queryOrderById(firstActivity.getInstanceID());
                        if (orderDto != null && orderDto.getLastModifiedDT() != null) {
                            orderAntiCorruptionServiceImp.updateOrder(new OrderDto() {{
                                setOrderId(firstActivity.getInstanceID());
                                setDueDate(DateUtil.addMillSecond(orderDto.getLastModifiedDT(), 5 * 60 * 1000));
                            }});
                        }
                    }
                    EncounterCarePathServiceImpl encounterCarePathService = new EncounterCarePathServiceImpl(userContext);
                    EncounterCarePath encounterCarePath = new EncounterCarePath() {{
                        setEncounterId(new Long(encounterServiceImp.queryByPatientSer(patientSer).getId()));
                        setCpInstanceId(new Long(instanceId));
                        setCategory(EncounterCarePathCategoryEnum.OPTIONAL);
                    }};
                    boolean ok = encounterCarePathService.addEncounterCarePath(encounterCarePath);
                    if (ok) {
                        PatientEncounterHelper.syncEncounterCarePathByPatientSer(patientSer.toString());
                    }
                } else {
                    log.error("Link new care path[{}] for patientSer {} fail", item.getCpTemplateId(), patientSer);
                }
            }
        }
    }
}
