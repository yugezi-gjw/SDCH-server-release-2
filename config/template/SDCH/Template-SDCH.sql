USE [Qin]
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'SDCH' AND templateId = 'Registration')﻿
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('SDCH', 'Registration', '患者注册', 'config\template\SDCH\Registration.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'SDCH' AND templateId = 'PhysicianPatientBanner')
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('SDCH', 'PhysicianPatientBanner', '医生组PatientBanner', 'config\template\SDCH\PhysicianPatientBanner.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'SDCH' AND templateId = 'PhysicistPatientBanner')
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('SDCH', 'PhysicistPatientBanner', '物理师组PatientBanner', 'config\template\SDCH\PhysicistPatientBanner.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'SDCH' and templateId = 'PlaceImmobilizationAndCTOrder')
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('SDCH', 'PlaceImmobilizationAndCTOrder', '制模定位申请单', 'config\template\SDCH\PlaceImmobilizationAndCTOrder.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'SDCH' and templateId = 'DoCTRecord')
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('SDCH', 'DoCTRecord', 'CT制模记录单', 'config\template\SDCH\DoCTRecord.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'SDCH' and templateId = 'PlacePlanningOrder')
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('SDCH', 'PlacePlanningOrder', '放射计划申请单', 'config\template\SDCH\PlacePlanningOrder.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'SDCH' and templateId = 'PlanningOrder')
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('SDCH', 'PlanningOrder', '计划申请单', 'config\template\SDCH\PlanningOrder.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'SDCH' and templateId = 'PrintCTAndImmobilizationOrder')
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('SDCH', 'PrintCTAndImmobilizationOrder', 'CT制模申请单（打印）', 'config\template\SDCH\PrintCTAndImmobilizationOrder.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'SDCH' and templateId = 'PrintCTAndImmobilizationOrderEnhancement')
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('SDCH', 'PrintCTAndImmobilizationOrderEnhancement', '大口径CT模拟定位申请表', 'config\template\SDCH\PrintCTAndImmobilizationOrderEnhancement.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'SDCH' and templateId = 'RadiotherapyConsent')
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('SDCH', 'RadiotherapyConsent', '放射治疗知情同意书', 'config\template\SDCH\RadiotherapyConsent.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'SDCH' and templateId = 'RadiotherapyConsent2')
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('SDCH', 'RadiotherapyConsent2', '放射治疗知情同意书2', 'config\template\SDCH\RadiotherapyConsent2.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'SDCH' and templateId = 'SelfPaidConsent')
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('SDCH', 'SelfPaidConsent', '自费项目知情同意书', 'config\template\SDCH\SelfPaidConsent.json');
IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE category = 'SDCH' and templateId = 'AuthorizedLetter')
  INSERT INTO dbo.DynamicFormTemplate(category, templateId, templateName, templatePath) VALUES ('SDCH', 'AuthorizedLetter', '授权委托书', 'config\template\SDCH\AuthorizedLetter.json');
