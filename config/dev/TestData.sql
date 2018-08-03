USE [Qin]
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlaceImmobilizationAndCTOrder', '制模CT定位申请单', 'config\template\PlaceImmobilizationAndCTOrder.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('DoImmoRecord', '制模记录单', 'config\template\DoImmoRecord.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('DoCTRecord', 'CT记录单', 'config\template\DoCTRecord.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlacePlanningOrder', '放射计划申请单', 'config\template\PlacePlanningOrder.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('DoRepositioning', '复位记录单', 'config\template\DoRepositioning.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('DoTreatment', '放射治疗记录单', 'config\template\DoFirstTreatment.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PrintCTAndImmobilizationOrder', '制模定位申请打印', 'config\template\PrintCTAndImmobilizationOrder.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('CTContrastAgentConsent', 'ct造影剂使用知情同意书', 'config\template\CTContrastAgentConsent.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('RadiotherapyMRIConsent', 'MRI知情同意书', 'config\template\RadiotherapyMRIConsent.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('RadiotherapyMRIEnhancementConsent', 'MRI增强知情同意书', 'config\template\RadiotherapyMRIEnhancementConsent.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('RadiotherapyMRITreatmentFlowOrder', '只包含CT流程的流程引导', 'config\template\RadiotherapyMRITreatmentFlowOrder.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('RadiotherapyTreatmentFlowOrder', '包含CT和MRI流程的流程引导', 'config\template\RadiotherapyTreatmentFlowOrder.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('RadiotherapyMagneticResonanceChecklist', '放疗磁共振定位检查前确认表', 'config\template\RadiotherapyMagneticResonanceChecklist.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlaceMRIOrder', 'MRI定位申请单', 'config\template\PlaceMRIOrder.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PrintTreatmentRecords', '治疗记录打印表单', 'config\template\PrintTreatmentRecords.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlanningOrder', '计划申请单', 'config\template\PlanningOrder.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('TreatmentApproval', '复位审核单', 'config\template\TreatmentApproval.json');

/****** 2018-02-07 Add ShanDong 计划申请单 template ******/
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlacePlanningOrder_SD', '山东计划申请单', 'config\template\PlacePlanningOrder_SD.json');
/****** 2018-02-07 Add ShanDong 模板 ******/
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PrintCTAndImmobilizationOrder_SD', '山东CT制模申请单（打印）', 'config\template\PrintCTAndImmobilizationOrder_SD.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PrintCTAndImmobilizationOrder_zengqiang_SD', '山东CT（增强）制模申请单（打印）', 'config\template\PrintCTAndImmobilizationOrder_zengqiang_SD.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('RadiotherapyConsent_SD', '山东放射治疗知情同意书', 'config\template\RadiotherapyConsent_SD.json');
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlaceImmobilizationAndCTOrder_SD', '山东制模CT定位申请单', 'config\template\PlaceImmobilizationAndCTOrder_SD.json');

/****** 2018-03-09 Add 计划确认单 template ******/
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlanningApproval', '计划确认单', 'config\template\PlanningApproval.json');


/***** 2018-03-26 activityCode需要动态表单数据AssignDeviceSDCH */
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','lunao_saoMiaoBuWeiGroup',1);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','toujing_saoMiaoBuWeiGroup',2);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','hemian_saoMiaoBuWeiGroup',3);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','xiongbu_saoMiaoBuWeiGroup',4);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','shangfubu_saoMiaoBuWeiGroup',5);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','xiafubu_saoMiaoBuWeiGroup',6);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','penqiang_saoMiaoBuWeiGroup',7);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','biyan_saoMiaoBuWeiGroup',8);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','jizhu_saoMiaoBuWeiGroup',9);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','c_saoMiaoBuWeiGroup',10);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','t_saoMiaoBuWeiGroup',12);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','l_saoMiaoBuWeiGroup',13);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','s_saoMiaoBuWeiGroup',14);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','siZhi_saoMiaoBuWeiGroup',14);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','shangZhi_saoMiaoBuWeiGroup',15);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','xiaZhi_saoMiaoBuWeiGroup',16);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','daMianMoGuDing_tiWeiGuDingFangShiGroup',17);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup',18);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','tiMoGuDing_tiWeiGuDingFangShiGroup',19);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','fuYaDaiGuDing_tiWeiGuDingFangShiGroup',20);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup',21);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','fuPengGuDingQi_tiWeiGuDingFangShiGroup',22);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup',23);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','jianYiZhiLiaoSheBei',24);
insert into SystemConfig(name,value,orderBy) values('AssignDeviceSDCH','jianYiJiHuaXiTong',25);

insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','lunao_saoMiaoBuWeiGroup',1);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','toujing_saoMiaoBuWeiGroup',2);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','hemian_saoMiaoBuWeiGroup',3);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','xiongbu_saoMiaoBuWeiGroup',4);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','shangfubu_saoMiaoBuWeiGroup',5);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','xiafubu_saoMiaoBuWeiGroup',6);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','penqiang_saoMiaoBuWeiGroup',7);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','biyan_saoMiaoBuWeiGroup',8);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','jizhu_saoMiaoBuWeiGroup',9);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','c_saoMiaoBuWeiGroup',10);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','t_saoMiaoBuWeiGroup',12);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','l_saoMiaoBuWeiGroup',13);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','s_saoMiaoBuWeiGroup',14);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','siZhi_saoMiaoBuWeiGroup',14);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','shangZhi_saoMiaoBuWeiGroup',15);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','xiaZhi_saoMiaoBuWeiGroup',16);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','daMianMoGuDing_tiWeiGuDingFangShiGroup',17);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup',18);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','tiMoGuDing_tiWeiGuDingFangShiGroup',19);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','fuYaDaiGuDing_tiWeiGuDingFangShiGroup',20);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup',21);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','fuPengGuDingQi_tiWeiGuDingFangShiGroup',22);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup',23);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','jianYiZhiLiaoSheBei',24);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','jianYiJiHuaXiTong',25);
insert into SystemConfig(name,value,orderBy) values('AssignTPSSDCH','yiXuanZhiLiaoSheBei',26);

/***** 2018-03-26 动态表单CheckBox对应的中文*/
insert into SystemConfig(name,value,orderBy,category) values('meigong_canKaoDianWeiZhiGroup','眉弓',1,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('xiae_canKaoDianWeiZhiGroup','下颚',2,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('xiongbu_canKaoDianWeiZhiGroup','胸部',3,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('penqiang_canKaoDianWeiZhiGroup','盆腔',4,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('qita_canKaoDianWeiZhiGroup','其他',5,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('lunao_saoMiaoBuWeiGroup','颅脑',6,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('toujing_saoMiaoBuWeiGroup','头颈',7,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('hemian_saoMiaoBuWeiGroup','颌面',8,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('xiongbu_saoMiaoBuWeiGroup','胸部',9,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('shangfubu_saoMiaoBuWeiGroup','上腹部',10,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('xiafubu_saoMiaoBuWeiGroup','下腹部',11,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('penqiang_saoMiaoBuWeiGroup','盆腔',12,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('biyan_saoMiaoBuWeiGroup','鼻咽',13,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('jizhu_saoMiaoBuWeiGroup','脊柱',14,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('c_saoMiaoBuWeiGroup','C;',15,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('t_saoMiaoBuWeiGroup','T;',16,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('l_saoMiaoBuWeiGroup','L;',17,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('s_saoMiaoBuWeiGroup','S;',18,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('siZhi_saoMiaoBuWeiGroup','四肢',19,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('shangZhi_saoMiaoBuWeiGroup','上肢（左、右）',20,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('xiaZhi_saoMiaoBuWeiGroup','下肢（左、右）',21,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('changGuiCTSIM_dingWeiFangShiGroup','常规CTSIM',22,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('changGuiZengQiangCTSIM_dingWeiFangShiGroup','常规增强CTSIM',23,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('menKongCTSIM_dingWeiFangShiGroup','门控CTSIM',24,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('4DCTSIM_dingWeiFangShiGroup','4D CTSIM',25,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('putongdingwei_dingWeiFangShiGroup','普通定位',26,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('daMianMoGuDing_tiWeiGuDingFangShiGroup','大面膜固定',27,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('xiaoMianMoGuDing_tiWeiGuDingFangShiGroup','小面膜固定',28,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('tiMoGuDing_tiWeiGuDingFangShiGroup','体膜固定',29,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('fuYaDaiGuDing_tiWeiGuDingFangShiGroup','负压袋固定',30,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup','乳腺托架固定',31,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('fuPengGuDingQi_tiWeiGuDingFangShiGroup','腹盆固定器',32,'DynamicFormFieldValue');
insert into SystemConfig(name,value,orderBy,category) values('SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup','SBRT体架固定器',33,'DynamicFormFieldValue');

IF NOT EXISTS (SELECT * FROM Qin.dbo.DynamicFormTemplate WHERE templateId = 'Registration')
  INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('Registration', '患者注册', 'config\template\Registration.json');

/***** 2018-04-11 需要启动MRI流程的ActivityCode*/
insert into SystemConfig(name,value,orderBy) values('CreateMRICarePathActivityCode','PlaceImmobilizationAndCTOrder',1);
/***** 2018-04-11 MRI CarePath 模板ID*/
insert into SystemConfig(name,value,orderBy) values('MRICarePathTemplateId','MRICarePath',1);
/***** 2018-04-11 制模定位申请表单中的MRI选项的名字*/
insert into SystemConfig(name,value,orderBy) values('DynamicFormMRICheckboxName','MRICheckbox',1);

/***** 2018-04-11 Do MRI template*/
INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('DoMRIRecord', 'MRI记录单', 'config\template\DoMRIRecord.json');

/***** 2018-05-03 Group prefix*/
insert into SystemConfig(name,value,orderBy) values('GroupOncologistPrefix','Oncologist',1);
insert into SystemConfig(name,value,orderBy) values('GroupNursePrefix','Nurse',5);
insert into SystemConfig(name,value,orderBy) values('GroupPhysicistPrefix','Physicist',10);
insert into SystemConfig(name,value,orderBy) values('GroupTechnicianPrefix','Technician',15);
/***** 2018-05-03 Group role in Aria*/
insert into SystemConfig(name,value,orderBy) values('GroupRoleOncologist','Oncologist',1);
insert into SystemConfig(name,value,orderBy) values('GroupRoleNurse','Nurse',5);
insert into SystemConfig(name,value,orderBy) values('GroupRolePhysicist','Physicist',10);
insert into SystemConfig(name,value,orderBy) values('GroupRoleTherapist','Therapist',15);