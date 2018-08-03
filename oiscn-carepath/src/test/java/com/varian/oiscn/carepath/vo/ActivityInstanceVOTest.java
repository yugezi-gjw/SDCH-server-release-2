package com.varian.oiscn.carepath.vo;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ActivityInstanceVOTest {

    @Test
    public void test(){
        ActivityInstanceVO activityInstanceVO = new ActivityInstanceVO();
        Date date = new Date();
        String patientId = "12121";
        activityInstanceVO.setPatientSer(patientId);
        Assert.assertEquals(patientId, activityInstanceVO.getPatientSer());
        String activityId = "activityId";
        activityInstanceVO.setActivityId(activityId);
        Assert.assertEquals(activityId, activityInstanceVO.getActivityId());
        String instanceId = "instanceId";
        activityInstanceVO.setInstanceId(instanceId);
        Assert.assertEquals(instanceId, activityInstanceVO.getInstanceId());
        String activityType = "activityType";
        activityInstanceVO.setActivityType(activityType);
        Assert.assertEquals(activityType, activityInstanceVO.getActivityType());
        String activityCode = "activityCode";
        activityInstanceVO.setActivityCode(activityCode);
        Assert.assertEquals(activityCode, activityInstanceVO.getActivityCode());
        boolean isActiveInWorkflow = false;
        activityInstanceVO.setActiveInWorkflow(isActiveInWorkflow);
        Assert.assertEquals(isActiveInWorkflow, activityInstanceVO.isActiveInWorkflow());
        String activityGroupId = "activityGroupId";
        activityInstanceVO.setActivityGroupId(activityGroupId);
        Assert.assertEquals(activityGroupId, activityInstanceVO.getActivityGroupId());
        String ariaId = "ariaId";
        activityInstanceVO.setAriaId(ariaId);
        Assert.assertEquals(ariaId, activityInstanceVO.getAriaId());
        String hisId = "hisId";
        activityInstanceVO.setHisId(hisId);
        Assert.assertEquals(hisId, activityInstanceVO.getHisId());
        String nationalId = "nationalId";
        activityInstanceVO.setNationalId(nationalId);
        Assert.assertEquals(nationalId, activityInstanceVO.getNationalId());
        String chineseName = "chineseName";
        activityInstanceVO.setChineseName(chineseName);
        Assert.assertEquals(chineseName, activityInstanceVO.getChineseName());
        String englishName = "englishName";
        activityInstanceVO.setEnglishName(englishName);
        Assert.assertEquals(englishName, activityInstanceVO.getEnglishName());
        String gender = "gender";
        activityInstanceVO.setGender(gender);
        Assert.assertEquals(gender, activityInstanceVO.getGender());
        Date birthday = date;
        activityInstanceVO.setBirthday(birthday);
        Assert.assertEquals(birthday, activityInstanceVO.getBirthday());
        String telephone = "telephone";
        activityInstanceVO.setTelephone(telephone);
        Assert.assertEquals(telephone, activityInstanceVO.getTelephone());
        String contactPerson = "contactPerson";
        activityInstanceVO.setContactPerson(contactPerson);
        Assert.assertEquals(contactPerson, activityInstanceVO.getContactPerson());
        String contactPhone = "contactPhone";
        activityInstanceVO.setContactPhone(contactPhone);
        Assert.assertEquals(contactPhone, activityInstanceVO.getContactPhone());
        String physicianGroupId = "physicianGroupId";
        activityInstanceVO.setPhysicianGroupId(physicianGroupId);
        Assert.assertEquals(physicianGroupId, activityInstanceVO.getPhysicianGroupId());
        String physicianGroupName = "physicianGroupName";
        activityInstanceVO.setPhysicianGroupName(physicianGroupName);
        Assert.assertEquals(physicianGroupName, activityInstanceVO.getPhysicianGroupName());
        String warningText = "warningText";
        activityInstanceVO.setWarningText(warningText);
        Assert.assertEquals(warningText, activityInstanceVO.getWarningText());
        boolean isUrgent = false;
        activityInstanceVO.setUrgent(isUrgent);
        Assert.assertEquals(isUrgent, activityInstanceVO.isUrgent());
        String physicianId = "physicianId";
        activityInstanceVO.setPhysicianId(physicianId);
        Assert.assertEquals(physicianId, activityInstanceVO.getPhysicianId());
        String physicianName = "physicianName";
        activityInstanceVO.setPhysicianName(physicianName);
        Assert.assertEquals(physicianName, activityInstanceVO.getPhysicianName());
        String physicianBId = "physicianBId";
        activityInstanceVO.setPhysicianBId(physicianBId);
        Assert.assertEquals(physicianBId, activityInstanceVO.getPhysicianBId());
        String physicianBName = "physicianBName";
        activityInstanceVO.setPhysicianBName(physicianBName);
        Assert.assertEquals(physicianBName, activityInstanceVO.getPhysicianBName());
        String physicianCId = "physicianCId";
        activityInstanceVO.setPhysicianCId(physicianCId);
        Assert.assertEquals(physicianCId, activityInstanceVO.getPhysicianCId());
        String physicianCName = "physicianCName";
        activityInstanceVO.setPhysicianCName(physicianCName);
        Assert.assertEquals(physicianCName, activityInstanceVO.getPhysicianCName());
        String physicianPhone = "physicianPhone";
        activityInstanceVO.setPhysicianPhone(physicianPhone);
        Assert.assertEquals(physicianPhone, activityInstanceVO.getPhysicianPhone());
        String progressState = "progressState";
        activityInstanceVO.setProgressState(progressState);
        Assert.assertEquals(progressState, activityInstanceVO.getProgressState());
        String nextAction = "nextAction";
        activityInstanceVO.setNextAction(nextAction);
        Assert.assertEquals(nextAction, activityInstanceVO.getNextAction());
        Date preActivityCompletedTime = date;
        activityInstanceVO.setPreActivityCompletedTime(preActivityCompletedTime);
        Assert.assertEquals(preActivityCompletedTime, activityInstanceVO.getPreActivityCompletedTime());
        String preActivityName = "preActivityName";
        activityInstanceVO.setPreActivityName(preActivityName);
        Assert.assertEquals(preActivityName, activityInstanceVO.getPreActivityName());
        String scheduleTime = "scheduleTime";
        activityInstanceVO.setScheduleTime(scheduleTime);
        Assert.assertEquals(scheduleTime, activityInstanceVO.getScheduleTime());
        Date startTime = date;
        activityInstanceVO.setStartTime(startTime);
        Assert.assertEquals(startTime, activityInstanceVO.getStartTime());
        boolean confirmedPayment = false;
        activityInstanceVO.setConfirmedPayment(confirmedPayment);
        Assert.assertEquals(confirmedPayment, activityInstanceVO.isConfirmedPayment());
        activityInstanceVO.setConfirmedPayment(confirmedPayment);
        Assert.assertEquals(confirmedPayment, activityInstanceVO.isConfirmedPayment());
        String workspaceType = "workspaceType";
        activityInstanceVO.setWorkspaceType(workspaceType);
        Assert.assertEquals(workspaceType, activityInstanceVO.getWorkspaceType());
        String moduleId = "moduleId";
        activityInstanceVO.setModuleId(moduleId);
        Assert.assertEquals(moduleId, activityInstanceVO.getModuleId());
        String physicianComment = "physicianComment";
        activityInstanceVO.setPhysicianComment(physicianComment);
        Assert.assertEquals(physicianComment, activityInstanceVO.getPhysicianComment());
        String patientSource = "patientSource";
        activityInstanceVO.setConfirmedPayment(confirmedPayment);
        Assert.assertEquals(confirmedPayment, activityInstanceVO.isConfirmedPayment());
        String insuranceType = "insuranceType";
        activityInstanceVO.setInsuranceType(insuranceType);
        Assert.assertEquals(insuranceType, activityInstanceVO.getInsuranceType());
        String age = "age";
        activityInstanceVO.setAge(age);
        Assert.assertEquals(age, activityInstanceVO.getAge());
    }
}