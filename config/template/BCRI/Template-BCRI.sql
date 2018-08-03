USE [Qin]
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'Registration')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'Registration', '患者注册', 'config\template\BCRI\Registration.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'PhysicianPatientBanner')
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'PhysicianPatientBanner', '医生组PatientBanner', 'config\template\BCRI\PhysicianPatientBanner.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'PhysicistPatientBanner')
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'PhysicistPatientBanner', '物理师组PatientBanner', 'config\template\BCRI\PhysicistPatientBanner.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'PlaceImmobilizationAndCTOrder')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'PlaceImmobilizationAndCTOrder', '制模定位申请单', 'config\template\BCRI\PlaceImmobilizationAndCTOrder.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'PlaceImmobilizationAndCTOrderDefaultValue')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'PlaceImmobilizationAndCTOrderDefaultValue', '制模定位申请单（默认值）', 'config\template\BCRI\PlaceImmobilizationAndCTOrderDefaultValue.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'DoImmoRecord')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'DoImmoRecord', '制模记录单', 'config\template\BCRI\DoImmoRecord.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'DoCTRecord')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'DoCTRecord', 'CT记录单', 'config\template\BCRI\DoCTRecord.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'PlacePlanningOrder')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'PlacePlanningOrder', '放射计划申请单', 'config\template\BCRI\PlacePlanningOrder.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'DoRepositioning')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'DoRepositioning', '复位记录单', 'config\template\BCRI\DoRepositioning.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'DoTreatment')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'DoTreatment', '放射治疗记录单', 'config\template\BCRI\DoTreatment.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'PrintCTAndImmobilizationOrder')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'PrintCTAndImmobilizationOrder', '制模定位申请单（打印）', 'config\template\BCRI\PrintCTAndImmobilizationOrder.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'CTContrastAgentConsent')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'CTContrastAgentConsent', 'ct造影剂使用知情同意书', 'config\template\BCRI\CTContrastAgentConsent.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'RadiotherapyMRIConsent')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'RadiotherapyMRIConsent', 'MRI知情同意书', 'config\template\BCRI\RadiotherapyMRIConsent.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'RadiotherapyMRIEnhancementConsent')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'RadiotherapyMRIEnhancementConsent', 'MRI增强知情同意书', 'config\template\BCRI\RadiotherapyMRIEnhancementConsent.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'RadiotherapyMRITreatmentFlowOrder')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'RadiotherapyMRITreatmentFlowOrder', '只包含CT流程的流程引导', 'config\template\BCRI\RadiotherapyMRITreatmentFlowOrder.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'RadiotherapyTreatmentFlowOrder')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'RadiotherapyTreatmentFlowOrder', '包含CT和MRI流程的流程引导', 'config\template\BCRI\RadiotherapyTreatmentFlowOrder.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'RadiotherapyMagneticResonanceChecklist')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'RadiotherapyMagneticResonanceChecklist', '放疗磁共振定位检查前确认表', 'config\template\BCRI\RadiotherapyMagneticResonanceChecklist.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'PlaceMRIOrder')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'PlaceMRIOrder', 'MRI定位申请单', 'config\template\BCRI\PlaceMRIOrder.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'PrintTreatmentRecords')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'PrintTreatmentRecords', '放射治疗记录单（打印）', 'config\template\BCRI\PrintTreatmentRecords.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'PlanningOrder')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'PlanningOrder', '计划申请单', 'config\template\BCRI\PlanningOrder.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'TreatmentApproval')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'TreatmentApproval', '复位审核单', 'config\template\BCRI\TreatmentApproval.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'PlanningApproval')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'PlanningApproval', '计划确认单', 'config\template\BCRI\PlanningApproval.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'BCRI' AND templateId = 'DoMRIRecord')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('BCRI', 'DoMRIRecord', 'MRI记录单', 'config\template\BCRI\DoMRIRecord.json');
