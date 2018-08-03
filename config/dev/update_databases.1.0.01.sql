/***** INITIAL DATABASE Base *****/
USE [master]
IF DB_ID('Qin') IS NULL
  CREATE DATABASE [Qin]
  CONTAINMENT = NONE
    COLLATE Chinese_PRC_90_CI_AS
GO

USE [master]
IF (SELECT loginname FROM sys.syslogins WHERE loginname = 'VarianCN') IS NULL
  CREATE LOGIN [VarianCN] WITH PASSWORD = N'V@rianCN01', DEFAULT_DATABASE = [Qin], DEFAULT_LANGUAGE = [Simplified Chinese], CHECK_EXPIRATION = OFF, CHECK_POLICY = OFF
GO

USE [Qin]
GO
IF USER_ID('VarianCN') IS NULL
  CREATE USER [VarianCN] FOR LOGIN [VarianCN]
GO

USE [Qin]
GO
ALTER USER [VarianCN] WITH DEFAULT_SCHEMA=[dbo]
GO
USE [Qin]
GO
ALTER ROLE [db_owner] ADD MEMBER [VarianCN]
GO

IF OBJECT_ID('dbo.Patient', 'U') IS NULL
CREATE TABLE Patient(
  id                  INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
  vip                 NVARCHAR(1),
  hisId               NVARCHAR(64),
  vId                 NVARCHAR(64),
  photo               IMAGE,
  pinyin              NVARCHAR(32),
  address             NVARCHAR(512),
  contactName         NVARCHAR(128),
  contactAddress      NVARCHAR(512),
  contactHomePhone    NVARCHAR(64),
  contactWorkPhone    NVARCHAR(64),
  contactMobilePhone  NVARCHAR(64),
  contactRelationship NVARCHAR(32),
  birthDate           DATETIME,
  homePhone           NVARCHAR(64),
  workPhone           NVARCHAR(64),
  mobilePhone         NVARCHAR(64),
  nationalId          NVARCHAR(25),
  chineseName         NVARCHAR(128),
  citizenship         NVARCHAR(32),
  englishName         NVARCHAR(128),
  ethnicGroup         NVARCHAR(32),
  radiationId         NVARCHAR(32),
  maritalStatus       NVARCHAR(32),
  patientStatus       NVARCHAR(1),
  patientHistory      NVARCHAR(512),
  createdUser         NVARCHAR(64),
  createdDate         DATETIME,
  lastUpdatedUser     NVARCHAR(64),
  lastUpdatedDate     DATETIME,
  patientInfo         NTEXT,
);

IF OBJECT_ID('dbo.Encounter', 'U') IS NULL
CREATE TABLE Encounter(
  id                        INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
  patientId                 INT,
  patientSer                NVARCHAR(32),
  age                       INT,
  alert                     NVARCHAR(512),
  allergyInfo               NVARCHAR(512),
  bedNo                     NVARCHAR(32),
  status                    NVARCHAR(12),
  urgent                    NVARCHAR(12),
  ecogDesc                  NVARCHAR(512),
  ecogScore                 NVARCHAR(1),
  diagnoseCode              NVARCHAR(32),
  diagnoseDesc              NVARCHAR(512),
  diagnoseSystem            NVARCHAR(128),
  diagnosePatientId         INT,
  diagnoseRecurrence        NVARCHAR(128),
  diagnoseBodypartCode      NVARCHAR(128),
  diagnoseBodypartDesc      NVARCHAR(128),
  diagnoseDate              DATETIME,
  diagnosisNote             NVARCHAR(128),
  stagingSchemeName         NVARCHAR(128),
  stagingBasisCode          NVARCHAR(128),
  stagingStage              NVARCHAR(12),
  stagingTcode              NVARCHAR(12),
  stagingNcode              NVARCHAR(12),
  stagingMcode              NVARCHAR(12),
  stagingDate               DATETIME,
  positiveSign              NVARCHAR(64),
  inPatientArea             NVARCHAR(64),
  insuranceType             NVARCHAR(32),
  patientSource             NVARCHAR(32),
  organizationId            NVARCHAR(32),
  physicianComment          NVARCHAR(512),
  patientSourceEnum         NVARCHAR(32),
  primaryPhysicianId        NVARCHAR(32),
  primaryPhysicianGroupId   NVARCHAR(32),
  primaryPhysicianGroupName NVARCHAR(32),
  physicianBId              NVARCHAR(32),
  physicianBName            NVARCHAR(32),
  physicianCId              NVARCHAR(32),
  physicianCName            NVARCHAR(32),
  createdUser               NVARCHAR(64),
  createdDate               DATETIME,
  lastUpdatedUser           NVARCHAR(64),
  lastUpdatedDate           DATETIME,
  encounterInfo             NTEXT
);

IF OBJECT_ID('EncounterCarePath', 'U') IS NULL
CREATE TABLE EncounterCarePath(
  encounterId  INT NOT NULL,
  cpInstanceId INT
);

IF OBJECT_ID('dbo.Diagnosis', 'U') IS NULL
CREATE TABLE Diagnosis(
  id              INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
  code            NVARCHAR(512),
  description     NVARCHAR(1024),
  value           NVARCHAR(512),
  system          NVARCHAR(512),
  createdUser     NVARCHAR(64),
  createdDate     DATETIME,
  lastUpdatedUser NVARCHAR(64),
  lastUpdatedDate DATETIME,
  diagnosisInfo   NTEXT
);

IF OBJECT_ID('dbo.DynamicFormTemplate', 'U') IS NULL
CREATE TABLE DynamicFormTemplate(
  id              INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
  templateId      NVARCHAR(200),
  templateName    NVARCHAR(256),
  templatePath    NVARCHAR(256),
  createdUser     NVARCHAR(64),
  createdDate     DATETIME,
  lastUpdatedUser NVARCHAR(64),
  lastUpdatedDate DATETIME
);

IF OBJECT_ID('dbo.DynamicFormInstance', 'U') IS NULL
CREATE TABLE DynamicFormInstance(
  id                      INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
  hisId                   NVARCHAR(64),
  encounterId             INT,
  createduser             NVARCHAR(64),
  createdDate             DATETIME,
  lastUpdatedUser         NVARCHAR(64),
  lastUpdatedDate         DATETIME,
  dynamicFormInstanceInfo NTEXT
);

IF OBJECT_ID('dbo.DynamicFormItem', 'U') IS NULL
  CREATE TABLE DynamicFormItem(
    hisId       NVARCHAR(64),
    encounterId INT,
    itemKey     NVARCHAR(64),
    itemValue   NTEXT,
  );

IF OBJECT_ID('dbo.ISOCenter', 'U') IS NULL
CREATE TABLE ISOCenter(
  id              INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
  hisId           NVARCHAR(32),
  encounterId     INT,
  createdUser     NVARCHAR(64),
  createdDate     DATETIME,
  lastUpdatedUser NVARCHAR(64),
  lastUpdatedDate DATETIME,
  isoCenterInfo   NTEXT
);

IF OBJECT_ID('dbo.ISOCenterItem', 'U') IS NULL
CREATE TABLE ISOCenterItem(
  isoCenterId INT,
  planId      NVARCHAR(32),
  isoName     NVARCHAR(64),
  vrt         NUMERIC(8, 3),
  lng         NUMERIC(8, 3),
  lat         NUMERIC(8, 3)
);

IF OBJECT_ID('dbo.ConfirmPayment', 'U') IS NULL
CREATE TABLE ConfirmPayment (
  id              INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
  hisId           NVARCHAR(64),
  encounterId     INT,
  createdUser     NVARCHAR(64),
  createdDate     DATETIME,
  lastUpdatedUser NVARCHAR(64),
  lastUpdatedDate DATETIME
);

IF OBJECT_ID('dbo.ConfirmStatus', 'U') IS NULL
CREATE TABLE ConfirmStatus (
  confirmPaymentId INT,
  activityCode     NVARCHAR(64),
  activityContent  NVARCHAR(128),
  status           INT
);

IF OBJECT_ID('dbo.TreatmentConfirmStatus', 'U') IS NULL
CREATE TABLE TreatmentConfirmStatus (
  confirmPaymentId    INT,
  activityCode        NVARCHAR(64),
  activityContent     NVARCHAR(128),
  totalPaymentCount   INT,
  confirmPaymentCount INT
);

IF OBJECT_ID('dbo.TreatmentWorkload', 'U') IS NULL
CREATE TABLE TreatmentWorkload (
  id              INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
  hisId           NVARCHAR(64),
  encounterId     INT,
  treatmentDate   DATETIME,
  createdUser     NVARCHAR(64),
  createdDate     DATETIME,
  lastUpdatedUser NVARCHAR(64),
  lastUpdatedDate DATETIME
);

IF OBJECT_ID('dbo.TreatmentWorkloadPlan', 'U') IS NULL
CREATE TABLE TreatmentWorkloadPlan (
  workloadId         INT,
  planId             NVARCHAR(64),
  deliveredFractions INT,
  comment            NVARCHAR(512)
);

IF OBJECT_ID('dbo.TreatmentWorkloadSignature', 'U') IS NULL
CREATE TABLE TreatmentWorkloadSignature (
  workloadId   INT,
  userName     NVARCHAR(64),
  resourceName NVARCHAR(128),
  resourceSer  INT,
  signDate     DATETIME,
  signType     NVARCHAR(12)
);

IF OBJECT_ID('dbo.TreatmentWorkloadWorker', 'U') IS NULL
CREATE TABLE TreatmentWorkloadWorker (
  workloadId INT,
  workerName NVARCHAR(128),
  orderNum   INT
);

IF OBJECT_ID('dbo.SystemConfig', 'U') IS NULL
CREATE TABLE SystemConfig (
  id      INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
  name    NVARCHAR(64),
  value   NVARCHAR(256),
  orderBy INTEGER
);

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'insuranceType')
BEGIN
  INSERT INTO SystemConfig(name, value, orderBy) VALUES ('insuranceType', '北京医保', 1);
  INSERT INTO SystemConfig(name, value, orderBy) VALUES ('insuranceType', '北京新农合', 2);
  INSERT INTO SystemConfig(name, value, orderBy) VALUES ('insuranceType', '自费', 3);
END


IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'activityRefreshTime')
INSERT INTO SystemConfig(name, value, orderBy) VALUES ('activityRefreshTime', '20', 1);

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'defaultDepartment')
INSERT INTO SystemConfig(name, value, orderBy) VALUES ('defaultDepartment', '1', 1);

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'appointmentStoredToLocal')
INSERT INTO SystemConfig(name, value, orderBy) VALUES ('appointmentStoredToLocal', 'false', 1);

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'TreatmentActivityCode')
INSERT INTO SystemConfig(name, value, orderBy) VALUES ('TreatmentActivityCode', 'DoTreatment', 1);

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'CallingBatchCount')
INSERT INTO SystemConfig(name, value, orderBy) VALUES ('CallingBatchCount', '3', 1);

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'CountPerCalling')
INSERT INTO SystemConfig(name, value, orderBy) VALUES ('CountPerCalling', '3', 1);

IF OBJECT_ID('dbo.TreatmentAppointment', 'U') IS NULL
CREATE TABLE TreatmentAppointment(
  id              INT IDENTITY (1, 1) PRIMARY KEY,
  uid             CHAR(36)     NOT NULL,
  appointmentId   NVARCHAR(32),
  hisId           NVARCHAR(64) NOT NULL,
  encounterId     INT          NOT NULL,
  patientId       NVARCHAR(32) NOT NULL,
  deviceId        NVARCHAR(32) NOT NULL,
  startTime       DATETIME     NOT NULL,
  endTime         DATETIME     NOT NULL,
  activityCode    NVARCHAR(64) NOT NULL,
  status          NVARCHAR(32) NOT NULL,
  createdUser     NVARCHAR(64),
  createdDate     DATETIME,
  lastUpdatedUser NVARCHAR(64),
  lastUpdatedDate DATETIME
);

IF OBJECT_ID('dbo.VID', 'U') IS NULL
CREATE TABLE VID(
  vid INT
);

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'VIDPrefix')
INSERT INTO SystemConfig(name, value, OrderBy) VALUES ('VIDPrefix', 'V', 1);

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'VID')
INSERT INTO SystemConfig(name, value, OrderBy) VALUES ('VID', '1', 1);

IF OBJECT_ID('dbo.TaskLocking', 'U') IS NULL
CREATE TABLE TaskLocking(
  taskId       NVARCHAR(36) NOT NULL,
  activityType NVARCHAR(32) NOT NULL,
  lockUserName NVARCHAR(32) NOT NULL,
  resourceSer  INT          NOT NULL,
  resourceName NVARCHAR(64) NOT NULL,
  lockTime     DATETIME     NOT NULL
);

IF OBJECT_ID('dbo.QueuingManagement', 'U') IS NULL
CREATE  TABLE QueuingManagement(
  id            INT IDENTITY (1, 1) PRIMARY KEY,
  appointmentId NVARCHAR(36) NOT NULL,
  activityCode  NVARCHAR(32) NOT NULL,
  hisId         NVARCHAR(32) NOT NULL,
  encounterId   INT          NOT NULL,
  patientId     INT          NOT NULL,
  deviceId      NVARCHAR(32) NOT NULL,
  checkInStatus NVARCHAR(12) NOT NULL,
  checkInIdx    INT          NOT NULL,
  startTime     DATETIME     NOT NULL,
  checkInTime   DATETIME     NOT NULL
);

/****** UPDATE for MR1  ******/
USE [Qin]

IF COL_LENGTH('Diagnosis', 'pinyin') IS NULL
BEGIN
    ALTER TABLE Diagnosis ADD pinyin NVARCHAR(128);
END

/****** User Profile Table  ******/
IF OBJECT_ID('dbo.UserProfile', 'U') IS NULL
CREATE TABLE UserProfile(
  id int IDENTITY(1,1) NOT NULL,
  userId NVARCHAR(64) NOT NULL,
  userRole tinyint NOT NULL, -- 1:Login User, 2:Patient, 3, 99:Others
  propertyName NVARCHAR(20) NOT NULL, -- property Name, no more than 20 chars
  propertyValue NVARCHAR(50) NULL,
  crtUser NVARCHAR(64) NOT NULL,
  crtDT DATETIME DEFAULT (GETDATE()) NOT NULL,
  updUser NVARCHAR(64) NOT NULL,
  updDT DATETIME DEFAULT (GETDATE()) NOT NULL
);

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'DiagnosisSearchTopN')
INSERT INTO SystemConfig(name, value, orderBy) VALUES ('DiagnosisSearchTopN','50',1);

IF COL_LENGTH('Encounter', 'insuranceTypeCode') IS NULL
BEGIN
  ALTER TABLE Encounter ADD insuranceTypeCode NVARCHAR(32);
END

IF OBJECT_ID('dbo.BodyPart', 'U') IS NULL
CREATE TABLE  BodyPart (
  code varchar(32),
  description varchar(512),
  pinyin varchar(512)
);

IF COL_LENGTH('TreatmentWorkloadPlan', 'selected') IS NULL
BEGIN
  ALTER TABLE TreatmentWorkloadPlan ADD selected bit;
END
IF COL_LENGTH('DynamicFormItem', 'id') IS NULL
BEGIN
  ALTER TABLE DynamicFormItem ADD id INT IDENTITY(1,1) PRIMARY KEY NOT NULL;
END

IF OBJECT_ID('dbo.DynamicFormRecord', 'U') IS NULL
CREATE TABLE DynamicFormRecord(
  id INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
  hisId NVARCHAR(64),
  encounterId INT,
  carePathInstanceId NVARCHAR(32),
  templateId NVARCHAR(200),
  createdUser NVARCHAR(64),
  createdDate DATETIME,
  dynamicFormRecordInfo NTEXT
);

IF OBJECT_ID('dbo.EncounterCarePath', 'U') IS NULL
CREATE TABLE EncounterCarePath(
  encounterId INT NOT NULL,
  cpInstanceId INT
);

/****** Group Default View  ******/
IF COL_LENGTH('SystemConfig', 'category') IS NULL
BEGIN
  ALTER TABLE SystemConfig ADD category NVARCHAR(50);
END

/****** Group Default View: initial data  ******/
EXEC ('
  IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = ''Nursing'' AND category in (''default_view''))
  INSERT INTO SystemConfig (category, name, value, orderBy) VALUES (''default_view'', ''Nursing'', ''schedule-home'', 1);
  IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = ''Nursing'' AND category in (''default_tab''))
  INSERT INTO SystemConfig (category, name, value, orderBy) VALUES (''default_tab'', ''Nursing'', ''schedule'', 1);
')

/**** setup photo ***/
IF OBJECT_ID('dbo.SetupPhoto', 'U') IS NULL
CREATE TABLE SetupPhoto(
  id INT IDENTITY(1,1) PRIMARY KEY,
  deviceId NVARCHAR(64),
  hisId NVARCHAR(64),
  createdUser NVARCHAR(64),
  createdDT DATETIME
);

IF OBJECT_ID('dbo.SetupPhotoDetail', 'U') IS NULL
CREATE TABLE SetupPhotoDetail(
  id INT IDENTITY(1,1) PRIMARY KEY,
  setupPhotoId INT,
  photoId NVARCHAR(200),
  photo VARBINARY(MAX)
);

IF OBJECT_ID('dbo.SetupPhotoArchive', 'U') IS NULL
CREATE TABLE SetupPhotoArchive(
  id INT IDENTITY(1,1) PRIMARY KEY,
  dynamicFormRecordId INT,
  photoId NVARCHAR(200),
  photo VARBINARY(MAX),
  createdUser NVARCHAR(64),
  createdDT DATETIME
);
/**** setup photo ***/

/** PatientV2 */
IF OBJECT_ID('dbo.PatientV2', 'U') IS NULL
CREATE TABLE PatientV2(
  id                  INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
  hisId               NVARCHAR(64),
  ariaId              NVARCHAR(64),
  photo               IMAGE,
  fullName         	  NVARCHAR(128),
  pinyinName          NVARCHAR(32),
  nationalId          NVARCHAR(25),
  contactPerson       NVARCHAR(128),
  contactPhone    	  NVARCHAR(64),
  gender			        NVARCHAR(8),
  address             NVARCHAR(512),
  birthDate           DATETIME,
  telephone           NVARCHAR(64),
  citizenship         NVARCHAR(32),
  ethnicGroup         NVARCHAR(32),
  maritalStatus       NVARCHAR(32),
  patientStatus       NVARCHAR(1),
  medicalHistory      NVARCHAR(512),
  createdUser         NVARCHAR(64),
  createdDate         DATETIME,
  lastUpdatedUser     NVARCHAR(64),
  lastUpdatedDate     DATETIME,
  patientInfo         NTEXT
);

IF OBJECT_ID('dbo.PatientIdentifier', 'U') IS NULL
CREATE TABLE PatientIdentifier(
  domain              NVARCHAR(64),
  value               NVARCHAR(64),
  type                NVARCHAR(8), -- HC-hisId PI-ariaId NNCHN=nationalId
  status         	    NVARCHAR(8),
  used          		  NVARCHAR(8)
);

/****** 2018-01-29 Recurring Appointment Time Limit: 50 by default ******/
IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'RecurringAppointmentTimeLimit')
  INSERT INTO SystemConfig (name, value, orderBy) VALUES ('RecurringAppointmentTimeLimit', '50', 1)

/****** 2018-01-31 Default Patient Banner for Physicist ******/
EXEC ('
  IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = ''Physicist'')
  INSERT INTO SystemConfig (name, value, orderBy, category) VALUES (''Physicist'', ''PhysicistPatientBanner'', 1, ''default_banner'');

  IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE templateId = ''PhysicianPatientBanner'')
  INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES (''PhysicianPatientBanner'', ''医生组PatientBanner'', ''config\template\PhysicianPatientBanner.json'');

  IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE templateId = ''PhysicistPatientBanner'')
  INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES (''PhysicistPatientBanner'', ''物理师组PatientBanner'', ''config\template\PhysicistPatientBanner.json'');
')

/****** 2018-02-01 Recurring Appointment: The max count per time slot ******/
IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'CountPerSlot')
  INSERT INTO SystemConfig (name, value, orderBy) VALUES ('CountPerSlot', 3, 1)

/****** 2018-02-06 set Refresh cache from fhir interval and set PatientID1,PatientID2 mapping ******/
IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'RefreshCacheFromFHIRInterval')
  INSERT INTO SystemConfig (name, value, orderBy) VALUES ('RefreshCacheFromFHIRInterval', 5, 1);

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'PatientIdInAria')
  INSERT INTO SystemConfig (name, value, orderBy) VALUES ('PatientIdInAria', 'ariaId', 1);

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'PatientId2InAria')
  INSERT INTO SystemConfig (name, value, orderBy) VALUES ('PatientId2InAria', 'hisId', 1);

/****** 2018-02-26 Physicist Grouping: The flag of view all patients for physicist ******/
INSERT INTO SystemConfig (name, value, orderBy) VALUES ('ViewAllPatientsForPhysicist', 'true', 1);
/***** 2018-02-27 Target Volume table *****/

IF OBJECT_ID('dbo.TargetVolumeItem', 'U') IS NULL
CREATE TABLE TargetVolumeItem(
	targetVolumeId INT NOT NULL,
	fieldId NVARCHAR(64),
	fieldValue NVARCHAR(64),
	rNum INT,
	seq INT
);
IF OBJECT_ID('dbo.TargetVolume', 'U') IS  NULL
CREATE TABLE TargetVolume(
	  id INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
	  hisId NVARCHAR(64),
	  encounterId INT NOT NULL,
	  name NVARCHAR(64),
	  memo NVARCHAR(512)
);

/***** 2018-03-02 Plan Target Volume table *****/
IF OBJECT_ID('dbo.PlanTargetVolume', 'U') IS NULL
CREATE TABLE PlanTargetVolume(
	  hisId NVARCHAR(64),
	  encounterId INT NOT NULL,
	  planId NVARCHAR(64),
	  targetVolumeName NVARCHAR(64)
);

/***** 2018-03-02 Add configuration for the number of audit log entries per batch *****/
IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'auditLogEntryNumPerBatch')
  INSERT INTO SystemConfig (name, value, orderBy) VALUES ('auditLogEntryNumPerBatch', '100', 1);

/****** 2018-03-06  Add new DBHistory Table  ******/
IF OBJECT_ID('dbo.DBHistory', 'U') IS NULL
CREATE TABLE [dbo].[DBHistory](
  [DBHistorySer] INT  IDENTITY(1,1) PRIMARY KEY NOT NULL,
  [EventType] NVARCHAR(50) NOT NULL,
  [StartingRelease] NVARCHAR(50) NOT NULL,
  [EndingRelease] NVARCHAR(50) NOT NULL,
  [Description] NVARCHAR(50),
  [UpgrVersion] NVARCHAR(50),
  [HstryUserName] NVARCHAR(50),
  [HstryDateTime] DATETIME NOT NULL
);

INSERT INTO DBHistory VALUES((select case When (select top 1 [DBHistorySer]  from dbo.DBHistory)  IS NULL THEN 'New Installation'else 'Upgrade' End),(select case When (select top 1 [EndingRelease]  from dbo.DBHistory order by [HstryDateTime] desc)  IS NULL THEN '@EndingRelease'else (select top 1 [EndingRelease]  from dbo.DBHistory order by [HstryDateTime] desc) End),('@EndingRelease'),('ARIA Qin 1.0 MR1 '+'@EndingRelease'),(''),('@HstryUserName'), getdate())

/***** 2018-03-07 Add column carePathInstanceId in table ConfirmStatus *****/
IF COL_LENGTH('ConfirmStatus', 'carePathInstanceId') IS NULL
BEGIN
  ALTER TABLE ConfirmStatus ADD carePathInstanceId INT;
END

/***** 2018-03-13 Add column patientSer in table  *****/
IF COL_LENGTH('Patient', 'patientSer') IS NULL
  BEGIN
    ALTER TABLE Patient ADD patientSer BIGINT;
  END

IF COL_LENGTH('TreatmentAppointment', 'patientId') IS NOT NULL
BEGIN
  EXEC SP_RENAME 'TreatmentAppointment.patientId','patientSer','COLUMN';
  ALTER TABLE TreatmentAppointment ALTER column  patientSer BIGINT;
END

IF COL_LENGTH('TreatmentAppointment', 'patientSer') IS NULL
BEGIN
  ALTER TABLE TreatmentAppointment ADD patientSer BIGINT;
END
  ALTER TABLE TreatmentAppointment ALTER COLUMN hisId nvarchar(64) NULL;

IF COL_LENGTH('QueuingManagement', 'patientId') IS NOT NULL
BEGIN
ALTER TABLE QueuingManagement ALTER column  patientId BIGINT;
EXEC SP_RENAME 'QueuingManagement.patientId','patientSer','COLUMN';
END
IF COL_LENGTH('QueuingManagement', 'patientSer') IS NULL
BEGIN
  ALTER TABLE QueuingManagement ADD patientSer BIGINT;
END
  ALTER TABLE QueuingManagement ALTER COLUMN hisId NVARCHAR(32) NULL;

IF COL_LENGTH('TreatmentWorkload', 'patientSer') IS NULL
BEGIN
 ALTER TABLE TreatmentWorkload ADD patientSer BIGINT;
END

IF COL_LENGTH('PlanTargetVolume', 'patientSer') IS NULL
BEGIN
 ALTER TABLE PlanTargetVolume ADD patientSer BIGINT;
END

IF COL_LENGTH('ConfirmPayment', 'patientSer') IS NULL
BEGIN
 ALTER TABLE ConfirmPayment ADD patientSer BIGINT;
END

IF COL_LENGTH('ISOCenter', 'patientSer') IS NULL
BEGIN
 ALTER TABLE ISOCenter ADD patientSer BIGINT;
END

IF COL_LENGTH('SetupPhoto', 'patientSer') IS NULL
BEGIN
 ALTER TABLE SetupPhoto ADD patientSer BIGINT;
END

IF COL_LENGTH('TargetVolume', 'patientSer') IS NULL
BEGIN
 ALTER TABLE TargetVolume ADD patientSer BIGINT;
END

IF COL_LENGTH('DynamicFormItem', 'patientSer') IS NULL
BEGIN
 ALTER TABLE DynamicFormItem ADD patientSer BIGINT;
END

IF COL_LENGTH('DynamicFormInstance', 'patientSer') IS NULL
BEGIN
 ALTER TABLE DynamicFormInstance ADD patientSer BIGINT;
END
/* 2018-03-23 create AssignResource table*/
IF OBJECT_ID('dbo.AssignResource', 'U') IS NULL
CREATE TABLE AssignResource(
id INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
resourceId VARCHAR(32) NOT NULL,
patientSer BIGINT NOT NULL,
encounterId BIGINT NOT NULL,
rtype VARCHAR(12) NOT NULL, -- MACHINE TPS
createdUser     NVARCHAR(64),
createdDate     DATETIME
);

-- 结束治疗的Encounter对应的plan
IF OBJECT_ID('dbo.EncounterEndPlan', 'U') IS NULL
BEGIN
  create table EncounterEndPlan(
    encounterId bigint not null,
    planSetupId varchar(32) not null,
    planCreatedDt DATETIME
  );
END

/****** 2018-03-26 Launch Eclipse and done Task: check status of pending task/appointment ******/
IF OBJECT_ID('dbo.EclipseTask', 'U') IS NULL
CREATE TABLE EclipseTask(
  id            INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
  moduleId      NVARCHAR(64),
  orderId       NVARCHAR(36) NOT NULL,
  patientSer    NVARCHAR(36) NOT NULL,
  status        NVARCHAR(16) NOT NULL, -- Pending, Done,
  crtUser       NVARCHAR(64),
  crtDate       DATETIME,
  updUser       NVARCHAR(64),
  updDate       DATETIME
);

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'Eclipse')
INSERT INTO SystemConfig(name,value,orderBy,category) VALUES('Eclipse','#CDC467',1,'TPS');
IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'Pinnacle')
INSERT INTO SystemConfig(name,value,orderBy,category) VALUES('Pinnacle','#FFE765',1,'TPS');

/****** 2018-03-30 Update the 'PLANNED' status to 'IN_PROGRESS' in Status column of Encounter table ******/
UPDATE Encounter SET status = 'IN_PROGRESS', encounterInfo = REPLACE(CAST(encounterInfo AS varchar(8000)), 'PLANNED', 'IN_PROGRESS')
WHERE status = 'PLANNED';

IF COL_LENGTH('Encounter', 'patientSer') IS NULL
BEGIN
  ALTER TABLE Encounter ADD patientSer BIGINT;
END

/*****  Fill PatientSer of Encounter table ******/
IF COL_LENGTH('Encounter', 'patientId') IS NOT NULL
BEGIN
EXEC ('
  UPDATE Encounter SET patientSer = p.patientSer FROM Encounter e
  JOIN Patient p
  ON p.id = e.patientId
  WHERE e.patientSer is NULL
  ')
END

/****** 2018-04-02 Add the 'PrimaryPhysicianName' column to Encounter table ******/
IF COL_LENGTH('Encounter', 'primaryPhysicianName') IS NULL
BEGIN
 ALTER TABLE Encounter ADD primaryPhysicianName NVARCHAR(32);
END

/****** 2018-04-03 Add the 'physicianPhone' column to Encounter table ******/
IF COL_LENGTH('Encounter', 'physicianPhone') IS NULL
BEGIN
 ALTER TABLE Encounter ADD physicianPhone NVARCHAR(32);
END
/***** 2018-04-03 ADD activityCode for assign resource *****/
IF COL_LENGTH('AssignResource', 'activityCode') IS NULL
BEGIN
  ALTER TABLE AssignResource DROP column rtype;
  ALTER TABLE AssignResource ADD activityCode NVARCHAR(64);
END

IF OBJECT_ID('dbo.AssignResourceConfig', 'U') IS NULL
CREATE TABLE AssignResourceConfig(
  hospital            NVARCHAR(64),
  resourceId          BIGINT,
  resourceCode        NVARCHAR(64),
  resourceName        NVARCHAR(64),
  color               NVARCHAR(16),  -- #CDC467
  activityCode        NVARCHAR(64),  -- usage
  orderNo             SMALLINT
);

IF NOT EXISTS (SELECT * FROM dbo.AssignResourceConfig WHERE resourceCode = 'eclipse')
  INSERT INTO AssignResourceConfig(hospital, resourceId, resourceCode, resourceName, color, activityCode, orderNo)
  VALUES ('default', '1001', 'eclipse', 'Eclipse', '#FF8B00', 'AssignTPSSDCH', 1);

IF NOT EXISTS (SELECT * FROM dbo.AssignResourceConfig WHERE resourceCode = 'monaq')
  INSERT INTO AssignResourceConfig(hospital, resourceId, resourceCode, resourceName, color, activityCode, orderNo)
  VALUES ('default', '1002', 'monaq', 'MONAQ', '#74AF2F', 'AssignTPSSDCH', 1);

IF NOT EXISTS (SELECT * FROM dbo.AssignResourceConfig WHERE resourceCode = 'tomo')
  INSERT INTO AssignResourceConfig(hospital, resourceId, resourceCode, resourceName, color, activityCode, orderNo)
  VALUES ('default', '1003', 'tomo', 'TOMO', '#66E765', 'AssignTPSSDCH', 1);

/****** 2018-04-09 Add the 'category' 'crtUser; 'crtTime' column to EncounterCarePath table ******/
IF COL_LENGTH('EncounterCarePath', 'category') IS NULL
  BEGIN
    ALTER TABLE EncounterCarePath ADD category NVARCHAR(64);
  END
IF COL_LENGTH('EncounterCarePath', 'crtUser') IS NULL
  BEGIN
    ALTER TABLE EncounterCarePath ADD crtUser NVARCHAR(64);
  END
IF COL_LENGTH('EncounterCarePath', 'crtTime') IS NULL
  BEGIN
    ALTER TABLE EncounterCarePath ADD crtTime DATETIME;
  END

/****** 2018-04-13 Add gender from patientInfo in Patient Table ******/
IF COL_LENGTH('dbo.Patient', 'gender') IS NULL
  BEGIN
    ALTER TABLE dbo.Patient ADD gender NVARCHAR(8);
  END

---- Fill in losing gender from Json data.
UPDATE dbo.Patient SET gender = info.gender FROM dbo.Patient p
  JOIN (SELECT json.patientSer,
          CASE REPLACE(SUBSTRING(json.genderstr, 9, CHARINDEX(',', json.genderstr) - 9), '"', '')
          WHEN 'UN' THEN NULL
          WHEN 'null' THEN NULL
          ELSE REPLACE(SUBSTRING(json.genderstr, 9, CHARINDEX(',', json.genderstr) - 9), '"', '')
          END AS gender
        FROM (SELECT patientSer, SUBSTRING(CAST(patientInfo AS NVARCHAR(3000)), CHARINDEX('gender', patientInfo), 20) AS genderstr
              FROM dbo.Patient WHERE gender IS NULL AND patientSer IS NOT NULL) json ) info
    ON info.patientSer = p.patientSer

/**** 2018-04-16 ****/
IF COL_LENGTH('dbo.DynamicFormRecord', 'templateInfo') IS NULL
  BEGIN
    ALTER TABLE dbo.DynamicFormRecord ADD templateInfo NTEXT;
    ---- TODO templateInfo is null for old data.
  END

/***** 2018-04-18 Add Configuration from file - SystemConfig *****/
IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'defaultTokenCacheTimeoutInMinutes')
BEGIN
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('default', 'defaultTokenCacheTimeoutInMinutes', '30', 1);
END
IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'resourceAutoUnlockInMinutes')
BEGIN
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('default', 'resourceAutoUnlockInMinutes', '5', 1);
END
IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'ospTokenValidationInterval')
BEGIN
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('default', 'ospTokenValidationInterval', '5', 1);
END
IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'fhirTokenAuthEnabled')
BEGIN
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('default', 'fhirTokenAuthEnabled', '0', 1);
END

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'FHIRServer')
BEGIN
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('FHIRServer', 'fhirConnectionTimeout', '40000', 1);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('FHIRServer', 'fhirConnectionRequestTimeout', '40000', 2);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('FHIRServer', 'fhirSocketTimeout', '40000', 3);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('FHIRServer', 'fhirLanguage', 'CHS', 4);
END

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'HttpClient')
BEGIN
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'timeout', '10000ms', 1);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'connectionTimeout', '10000ms', 2);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'connectionRequestTimeout', '10000ms', 3);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'timeToLive', '1 hour', 4);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'cookiesEnabled', 'false', 5);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'maxConnections', '1024', 6);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'maxConnectionsPerRoute', '1024', 7);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'keepAlive', '0s', 8);
END

IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'locale')
BEGIN
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('locale', 'language', 'zh', 1);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('locale', 'country', 'CN', 2);
END

/**** 2018-05-07 physicistGroupingActivityCode ****/
IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'physicistGroupingActivityCode')
  BEGIN
    PRINT 'Database GA - INSERT INTO dbo.SystemConfig [physicistGroupingActivityCode]'
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('PhysicistGroup', 'physicistGroupingActivityCode', 'AssignTPSSDCH', 1);
  END
GO

/***** 2018-05-07 Move AuditLog settings from yaml File to SystemConfig *****/
IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'AuditLog')
BEGIN
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('AuditLog', 'hostName', 'localhost', 1);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('AuditLog', 'port', '55020', 2);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('AuditLog', 'timeoutInMs', '300', 3);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('AuditLog', 'logThreadCount', '1', 4);
  INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('AuditLog', 'logBatchSize', '100', 5);
END
GO

/***** 2018-05-07 Clear useless config values *****/
IF EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'default_view' )
  BEGIN
    PRINT 'Database GA - Clear dbo.SystemConfig [default_view]'
    DELETE FROM dbo.SystemConfig WHERE category = 'default_view';
  END
GO

IF EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'default_tab' )
  BEGIN
    PRINT 'Database GA - Clear dbo.SystemConfig [default_tab]'
    DELETE FROM dbo.SystemConfig WHERE category = 'default_tab';
  END
GO

/***** 2018-05-07 Add Dev Mode to switch performance log *****/
IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'performanceLogging')
  BEGIN
    INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('default', 'performanceLogging', 'false', 1);
  END
GO

/***** 2018-05-08 crate table AssignResourceField *****/
IF OBJECT_ID('dbo.AssignResourceField', 'U') IS NULL
  BEGIN
    CREATE TABLE dbo.AssignResourceField (
      id        INT NOT NULL IDENTITY(1,1),
      category  NVARCHAR(50),
      name      NVARCHAR(64),
      value     NVARCHAR(256),
      sortNo    INT
    );
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','lunao_saoMiaoBuWeiGroup','lunao_saoMiaoBuWeiGroup',1);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','toujing_saoMiaoBuWeiGroup','toujing_saoMiaoBuWeiGroup',2);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','hemian_saoMiaoBuWeiGroup','hemian_saoMiaoBuWeiGroup',3);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','xiongbu_saoMiaoBuWeiGroup','xiongbu_saoMiaoBuWeiGroup',4);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','shangfubu_saoMiaoBuWeiGroup','shangfubu_saoMiaoBuWeiGroup',5);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','xiafubu_saoMiaoBuWeiGroup','xiafubu_saoMiaoBuWeiGroup',6);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','penqiang_saoMiaoBuWeiGroup','penqiang_saoMiaoBuWeiGroup',7);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','biyan_saoMiaoBuWeiGroup','biyan_saoMiaoBuWeiGroup',8);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','jizhu_saoMiaoBuWeiGroup','jizhu_saoMiaoBuWeiGroup',9);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','c_saoMiaoBuWeiGroup','c_saoMiaoBuWeiGroup',10);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','t_saoMiaoBuWeiGroup','t_saoMiaoBuWeiGroup',12);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','l_saoMiaoBuWeiGroup','l_saoMiaoBuWeiGroup',13);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','s_saoMiaoBuWeiGroup','s_saoMiaoBuWeiGroup',14);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','siZhi_saoMiaoBuWeiGroup','siZhi_saoMiaoBuWeiGroup',14);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','shangZhi_saoMiaoBuWeiGroup','shangZhi_saoMiaoBuWeiGroup',15);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','xiaZhi_saoMiaoBuWeiGroup','xiaZhi_saoMiaoBuWeiGroup',16);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','daMianMoGuDing_tiWeiGuDingFangShiGroup','daMianMoGuDing_tiWeiGuDingFangShiGroup',17);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup',18);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','tiMoGuDing_tiWeiGuDingFangShiGroup','tiMoGuDing_tiWeiGuDingFangShiGroup',19);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','fuYaDaiGuDing_tiWeiGuDingFangShiGroup','fuYaDaiGuDing_tiWeiGuDingFangShiGroup',20);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup',21);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','fuPengGuDingQi_tiWeiGuDingFangShiGroup','fuPengGuDingQi_tiWeiGuDingFangShiGroup',22);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup',23);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','jianYiZhiLiaoSheBei','jianYiZhiLiaoSheBei',24);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignDeviceSDCH','jianYiJiHuaXiTong','jianYiJiHuaXiTong',25);

    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','lunao_saoMiaoBuWeiGroup','lunao_saoMiaoBuWeiGroup',1);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','toujing_saoMiaoBuWeiGroup','toujing_saoMiaoBuWeiGroup',2);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','hemian_saoMiaoBuWeiGroup','hemian_saoMiaoBuWeiGroup',3);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','xiongbu_saoMiaoBuWeiGroup','xiongbu_saoMiaoBuWeiGroup',4);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','shangfubu_saoMiaoBuWeiGroup','shangfubu_saoMiaoBuWeiGroup',5);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','xiafubu_saoMiaoBuWeiGroup','xiafubu_saoMiaoBuWeiGroup',6);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','penqiang_saoMiaoBuWeiGroup','penqiang_saoMiaoBuWeiGroup',7);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','biyan_saoMiaoBuWeiGroup','biyan_saoMiaoBuWeiGroup',8);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','jizhu_saoMiaoBuWeiGroup','jizhu_saoMiaoBuWeiGroup',9);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','c_saoMiaoBuWeiGroup','c_saoMiaoBuWeiGroup',10);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','t_saoMiaoBuWeiGroup','t_saoMiaoBuWeiGroup',12);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','l_saoMiaoBuWeiGroup','l_saoMiaoBuWeiGroup',13);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','s_saoMiaoBuWeiGroup','s_saoMiaoBuWeiGroup',14);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','siZhi_saoMiaoBuWeiGroup','siZhi_saoMiaoBuWeiGroup',14);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','shangZhi_saoMiaoBuWeiGroup','shangZhi_saoMiaoBuWeiGroup',15);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','xiaZhi_saoMiaoBuWeiGroup','xiaZhi_saoMiaoBuWeiGroup',16);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','daMianMoGuDing_tiWeiGuDingFangShiGroup','daMianMoGuDing_tiWeiGuDingFangShiGroup',17);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup',18);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','tiMoGuDing_tiWeiGuDingFangShiGroup','tiMoGuDing_tiWeiGuDingFangShiGroup',19);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','fuYaDaiGuDing_tiWeiGuDingFangShiGroup','fuYaDaiGuDing_tiWeiGuDingFangShiGroup',20);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup',21);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','fuPengGuDingQi_tiWeiGuDingFangShiGroup','fuPengGuDingQi_tiWeiGuDingFangShiGroup',22);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup',23);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','jianYiZhiLiaoSheBei','jianYiZhiLiaoSheBei',24);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','jianYiJiHuaXiTong','jianYiJiHuaXiTong',25);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPSSDCH','yiXuanZhiLiaoSheBei','yiXuanZhiLiaoSheBei',26);

    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','meigong_canKaoDianWeiZhiGroup','眉弓',1);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiae_canKaoDianWeiZhiGroup','下颚',2);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiongbu_canKaoDianWeiZhiGroup','胸部',3);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','penqiang_canKaoDianWeiZhiGroup','盆腔',4);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','qita_canKaoDianWeiZhiGroup','其他',5);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','lunao_saoMiaoBuWeiGroup','颅脑',6);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','toujing_saoMiaoBuWeiGroup','头颈',7);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','hemian_saoMiaoBuWeiGroup','颌面',8);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiongbu_saoMiaoBuWeiGroup','胸部',9);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','shangfubu_saoMiaoBuWeiGroup','上腹部',10);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiafubu_saoMiaoBuWeiGroup','下腹部',11);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','penqiang_saoMiaoBuWeiGroup','盆腔',12);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','biyan_saoMiaoBuWeiGroup','鼻咽',13);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','jizhu_saoMiaoBuWeiGroup','脊柱',14);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','c_saoMiaoBuWeiGroup','C;',15);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','t_saoMiaoBuWeiGroup','T;',16);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','l_saoMiaoBuWeiGroup','L;',17);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','s_saoMiaoBuWeiGroup','S;',18);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','siZhi_saoMiaoBuWeiGroup','四肢',19);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','shangZhi_saoMiaoBuWeiGroup','上肢（左、右）',20);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiaZhi_saoMiaoBuWeiGroup','下肢（左、右）',21);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','changGuiCTSIM_dingWeiFangShiGroup','常规CTSIM',22);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','changGuiZengQiangCTSIM_dingWeiFangShiGroup','常规增强CTSIM',23);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','menKongCTSIM_dingWeiFangShiGroup','门控CTSIM',24);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','4DCTSIM_dingWeiFangShiGroup','4D CTSIM',25);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','putongdingwei_dingWeiFangShiGroup','普通定位',26);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','daMianMoGuDing_tiWeiGuDingFangShiGroup','大面膜固定',27);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup','小面膜固定',28);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','tiMoGuDing_tiWeiGuDingFangShiGroup','体膜固定',29);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','fuYaDaiGuDing_tiWeiGuDingFangShiGroup','负压袋固定',30);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup','乳腺托架固定',31);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','fuPengGuDingQi_tiWeiGuDingFangShiGroup','腹盆固定器',32);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup','SBRT体架固定器',33);
    INSERT INTO AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','form.jianYiZhiLiaoSheBei','建议治疗设备',36);
  END
GO

/***** 2018-05-08 Add category for Hospital *****/
IF COL_LENGTH('dbo.DynamicFormTemplate', 'category') IS NULL
  BEGIN
    ALTER TABLE dbo.DynamicFormTemplate ADD category NVARCHAR(50);
  END
GO

/***** 2018-05-09 *****/
IF NOT EXISTS (SELECT * FROM dbo.AssignResourceConfig WHERE resourceCode = 'pinnacle')
  INSERT INTO AssignResourceConfig(hospital, resourceId, resourceCode, resourceName, color, activityCode, orderNo)
  VALUES ('default', '1005', 'pinnacle', 'Pinnacle', '#FA8BF0', 'AssignTPSSDCH', 1);

IF EXISTS (SELECT * FROM dbo.AssignResourceConfig WHERE resourceCode = 'monaq')
  UPDATE AssignResourceConfig SET resourceCode = 'monaco', resourceName = 'Monaco' WHERE resourceCode = 'monaq';
IF EXISTS (SELECT * FROM dbo.AssignResource WHERE resourceId = 'monaq')
  UPDATE AssignResource SET resourceId = 'monaco' WHERE resourceId = 'monaq';

  