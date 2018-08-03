/***** Varian CCIP Database in UTF-8 (code page 65001) *****/
/***** Initial Database Base - Begin *****/
USE [master]
IF DB_ID('Qin') IS NULL
  BEGIN
    PRINT 'CREATE DATABASE [Qin]';
    CREATE DATABASE [Qin]
    CONTAINMENT = NONE
      COLLATE Chinese_PRC_90_CI_AS
  END
GO

USE [master]
IF (SELECT loginname FROM sys.syslogins WHERE loginname = 'VarianCN') IS NULL
  BEGIN
    PRINT 'CREATE LOGIN [VarianCN]';
    CREATE LOGIN [VarianCN] WITH PASSWORD = N'V@rianCN01', DEFAULT_DATABASE = [Qin], DEFAULT_LANGUAGE = [Simplified Chinese], CHECK_EXPIRATION = OFF, CHECK_POLICY = OFF
  END
GO

USE [Qin]
IF USER_ID('VarianCN') IS NULL
  BEGIN
    PRINT 'CREATE USER [VarianCN]';
    CREATE USER [VarianCN] FOR LOGIN [VarianCN]
  END

ALTER USER [VarianCN] WITH DEFAULT_SCHEMA=[dbo]
ALTER ROLE [db_owner] ADD MEMBER [VarianCN]
GO
/***** Initial Database Base - End *****/

/***** Database Version *****/
IF OBJECT_ID('dbo.DBHistory', 'U') IS NULL
  BEGIN
    PRINT 'CREATE TABLE [dbo].[DBHistory]'
    CREATE TABLE [dbo].[DBHistory] (
      [DBHistorySer]            INT  IDENTITY(1,1) PRIMARY KEY NOT NULL,
      [EventType]               NVARCHAR(50) NOT NULL,
      [StartingRelease]         NVARCHAR(50) NOT NULL,
      [EndingRelease]           NVARCHAR(50) NOT NULL,
      [Description]             NVARCHAR(50),
      [UpgrVersion]             NVARCHAR(50),
      [HstryUserName]           NVARCHAR(50),
      [HstryDateTime]           DATETIME NOT NULL
    );
  END
GO

/***** Fresh installation *****/
DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL
  PRINT 'Database - New Installation For @EndingRelease'
ELSE
  PRINT 'Database - Upgrade From [' + @VERSION_HISTORY + '] TO [@EndingRelease]'
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.AssignResource', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.AssignResource'
    CREATE TABLE dbo.AssignResource(
      id              INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
      resourceId      VARCHAR(32) NOT NULL,
      patientSer      BIGINT NOT NULL,
      encounterId     BIGINT NOT NULL,
      activityCode    NVARCHAR(64),
      createdUser     NVARCHAR(64),
      createdDate     DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.AssignResourceConfig', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.AssignResourceConfig'
    CREATE TABLE dbo.AssignResourceConfig(
      hospital            NVARCHAR(64),
      resourceId          BIGINT,
      resourceCode        NVARCHAR(64),
      resourceName        NVARCHAR(64),
      color               NVARCHAR(16),  -- #CDC467
      activityCode        NVARCHAR(64),  -- usage
      orderNo             SMALLINT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND NOT EXISTS (SELECT * FROM dbo.AssignResourceConfig)
  BEGIN
    INSERT INTO dbo.AssignResourceConfig(hospital, resourceId, resourceCode, resourceName, color, activityCode, orderNo) VALUES ('default', '11', 'eclipse', 'Eclipse', '#FF8B00', 'AssignTPS', 1);
    INSERT INTO dbo.AssignResourceConfig(hospital, resourceId, resourceCode, resourceName, color, activityCode, orderNo) VALUES ('default', '12', 'pinnacle', 'Pinnacle', '#FA8BF0', 'AssignTPS', 1);
    INSERT INTO dbo.AssignResourceConfig(hospital, resourceId, resourceCode, resourceName, color, activityCode, orderNo) VALUES ('default', '13', 'tomo', 'TOMO', '#66E765', 'AssignTPS', 1);
    INSERT INTO dbo.AssignResourceConfig(hospital, resourceId, resourceCode, resourceName, color, activityCode, orderNo) VALUES ('default', '14', 'monaco', 'Monaco', '#74AF2F', 'AssignTPS', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)

IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.AssignResourceField', 'U') IS NULL
  BEGIN
    CREATE TABLE dbo.AssignResourceField (
      id        INT NOT NULL IDENTITY(1,1),
      category  NVARCHAR(50),
      name      NVARCHAR(64),
      value     NVARCHAR(256),
      sortNo    INT
    );
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','lunao_saoMiaoBuWeiGroup','lunao_saoMiaoBuWeiGroup',1);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','toujing_saoMiaoBuWeiGroup','toujing_saoMiaoBuWeiGroup',2);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','hemian_saoMiaoBuWeiGroup','hemian_saoMiaoBuWeiGroup',3);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','xiongbu_saoMiaoBuWeiGroup','xiongbu_saoMiaoBuWeiGroup',4);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','shangfubu_saoMiaoBuWeiGroup','shangfubu_saoMiaoBuWeiGroup',5);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','xiafubu_saoMiaoBuWeiGroup','xiafubu_saoMiaoBuWeiGroup',6);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','penqiang_saoMiaoBuWeiGroup','penqiang_saoMiaoBuWeiGroup',7);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','biyan_saoMiaoBuWeiGroup','biyan_saoMiaoBuWeiGroup',8);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','jizhu_saoMiaoBuWeiGroup','jizhu_saoMiaoBuWeiGroup',9);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','c_saoMiaoBuWeiGroup','c_saoMiaoBuWeiGroup',10);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','t_saoMiaoBuWeiGroup','t_saoMiaoBuWeiGroup',12);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','l_saoMiaoBuWeiGroup','l_saoMiaoBuWeiGroup',13);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','s_saoMiaoBuWeiGroup','s_saoMiaoBuWeiGroup',14);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','siZhi_saoMiaoBuWeiGroup','siZhi_saoMiaoBuWeiGroup',14);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','shangZhi_saoMiaoBuWeiGroup','shangZhi_saoMiaoBuWeiGroup',15);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','xiaZhi_saoMiaoBuWeiGroup','xiaZhi_saoMiaoBuWeiGroup',16);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','daMianMoGuDing_tiWeiGuDingFangShiGroup','daMianMoGuDing_tiWeiGuDingFangShiGroup',17);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup',18);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','tiMoGuDing_tiWeiGuDingFangShiGroup','tiMoGuDing_tiWeiGuDingFangShiGroup',19);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','fuYaDaiGuDing_tiWeiGuDingFangShiGroup','fuYaDaiGuDing_tiWeiGuDingFangShiGroup',20);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup',21);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','fuPengGuDingQi_tiWeiGuDingFangShiGroup','fuPengGuDingQi_tiWeiGuDingFangShiGroup',22);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup',23);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','jianYiZhiLiaoSheBei','jianYiZhiLiaoSheBei',24);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','jianYiJiHuaXiTong','jianYiJiHuaXiTong',25);

    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','lunao_saoMiaoBuWeiGroup','lunao_saoMiaoBuWeiGroup',1);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','toujing_saoMiaoBuWeiGroup','toujing_saoMiaoBuWeiGroup',2);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','hemian_saoMiaoBuWeiGroup','hemian_saoMiaoBuWeiGroup',3);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','xiongbu_saoMiaoBuWeiGroup','xiongbu_saoMiaoBuWeiGroup',4);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','shangfubu_saoMiaoBuWeiGroup','shangfubu_saoMiaoBuWeiGroup',5);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','xiafubu_saoMiaoBuWeiGroup','xiafubu_saoMiaoBuWeiGroup',6);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','penqiang_saoMiaoBuWeiGroup','penqiang_saoMiaoBuWeiGroup',7);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','biyan_saoMiaoBuWeiGroup','biyan_saoMiaoBuWeiGroup',8);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','jizhu_saoMiaoBuWeiGroup','jizhu_saoMiaoBuWeiGroup',9);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','c_saoMiaoBuWeiGroup','c_saoMiaoBuWeiGroup',10);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','t_saoMiaoBuWeiGroup','t_saoMiaoBuWeiGroup',12);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','l_saoMiaoBuWeiGroup','l_saoMiaoBuWeiGroup',13);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','s_saoMiaoBuWeiGroup','s_saoMiaoBuWeiGroup',14);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','siZhi_saoMiaoBuWeiGroup','siZhi_saoMiaoBuWeiGroup',14);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','shangZhi_saoMiaoBuWeiGroup','shangZhi_saoMiaoBuWeiGroup',15);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','xiaZhi_saoMiaoBuWeiGroup','xiaZhi_saoMiaoBuWeiGroup',16);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','daMianMoGuDing_tiWeiGuDingFangShiGroup','daMianMoGuDing_tiWeiGuDingFangShiGroup',17);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup',18);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','tiMoGuDing_tiWeiGuDingFangShiGroup','tiMoGuDing_tiWeiGuDingFangShiGroup',19);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','fuYaDaiGuDing_tiWeiGuDingFangShiGroup','fuYaDaiGuDing_tiWeiGuDingFangShiGroup',20);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup',21);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','fuPengGuDingQi_tiWeiGuDingFangShiGroup','fuPengGuDingQi_tiWeiGuDingFangShiGroup',22);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup',23);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','jianYiZhiLiaoSheBei','jianYiZhiLiaoSheBei',24);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','jianYiJiHuaXiTong','jianYiJiHuaXiTong',25);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','yiXuanZhiLiaoSheBei','yiXuanZhiLiaoSheBei',26);

    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','meigong_canKaoDianWeiZhiGroup','眉弓',1);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiae_canKaoDianWeiZhiGroup','下颚',2);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiongbu_canKaoDianWeiZhiGroup','胸部',3);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','penqiang_canKaoDianWeiZhiGroup','盆腔',4);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','qita_canKaoDianWeiZhiGroup','其他',5);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','lunao_saoMiaoBuWeiGroup','颅脑',6);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','toujing_saoMiaoBuWeiGroup','头颈',7);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','hemian_saoMiaoBuWeiGroup','颌面',8);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiongbu_saoMiaoBuWeiGroup','胸部',9);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','shangfubu_saoMiaoBuWeiGroup','上腹部',10);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiafubu_saoMiaoBuWeiGroup','下腹部',11);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','penqiang_saoMiaoBuWeiGroup','盆腔',12);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','biyan_saoMiaoBuWeiGroup','鼻咽',13);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','jizhu_saoMiaoBuWeiGroup','脊柱',14);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','c_saoMiaoBuWeiGroup','C;',15);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','t_saoMiaoBuWeiGroup','T;',16);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','l_saoMiaoBuWeiGroup','L;',17);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','s_saoMiaoBuWeiGroup','S;',18);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','siZhi_saoMiaoBuWeiGroup','四肢',19);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','shangZhi_saoMiaoBuWeiGroup','上肢（左、右）',20);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiaZhi_saoMiaoBuWeiGroup','下肢（左、右）',21);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','changGuiCTSIM_dingWeiFangShiGroup','常规CTSIM',22);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','changGuiZengQiangCTSIM_dingWeiFangShiGroup','常规增强CTSIM',23);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','menKongCTSIM_dingWeiFangShiGroup','门控CTSIM',24);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','4DCTSIM_dingWeiFangShiGroup','4D CTSIM',25);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','putongdingwei_dingWeiFangShiGroup','普通定位',26);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','daMianMoGuDing_tiWeiGuDingFangShiGroup','大面膜固定',27);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup','小面膜固定',28);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','tiMoGuDing_tiWeiGuDingFangShiGroup','体膜固定',29);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','fuYaDaiGuDing_tiWeiGuDingFangShiGroup','负压袋固定',30);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup','乳腺托架固定',31);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','fuPengGuDingQi_tiWeiGuDingFangShiGroup','腹盆固定器',32);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup','SBRT体架固定器',33);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','jianYiZhiLiaoSheBei','建议治疗设备',36);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.Patient', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.Patient'
    CREATE TABLE dbo.Patient(
      id                  INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
      patientSer          BIGINT,
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
      gender              NVARCHAR(8),
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
      patientInfo         NTEXT,
      createdUser         NVARCHAR(64),
      createdDate         DATETIME,
      lastUpdatedUser     NVARCHAR(64),
      lastUpdatedDate     DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.Encounter', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.Encounter'
    CREATE TABLE dbo.Encounter(
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
      insuranceTypeCode         NVARCHAR(32),
      patientSource             NVARCHAR(32),
      organizationId            NVARCHAR(32),
      physicianComment          NVARCHAR(512),
      patientSourceEnum         NVARCHAR(32),
      primaryPhysicianId        NVARCHAR(32),
      primaryPhysicianName      NVARCHAR(32),
      physicianPhone            NVARCHAR(32),
      primaryPhysicianGroupId   NVARCHAR(32),
      primaryPhysicianGroupName NVARCHAR(32),
      physicianBId              NVARCHAR(32),
      physicianBName            NVARCHAR(32),
      physicianCId              NVARCHAR(32),
      physicianCName            NVARCHAR(32),
      encounterInfo             NTEXT,
      createdUser               NVARCHAR(64),
      createdDate               DATETIME,
      lastUpdatedUser           NVARCHAR(64),
      lastUpdatedDate           DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.EncounterCarePath', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.EncounterCarePath'
    CREATE TABLE dbo.EncounterCarePath(
      encounterId  INT NOT NULL,
      cpInstanceId INT,
      category NVARCHAR(64),
      crtUser NVARCHAR(64),
      crtTime DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.EncounterEndPlan', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.EncounterEndPlan'
    CREATE TABLE dbo.EncounterEndPlan(
      encounterId    BIGINT NOT NULL,
      planSetupId    VARCHAR(32) NOT NULL,
      planCreatedDt  DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.EclipseTask', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.EclipseTask'
    CREATE TABLE dbo.EclipseTask(
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
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.Diagnosis', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.Diagnosis'
    CREATE TABLE dbo.Diagnosis(
      id              INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
      code            NVARCHAR(512),
      pinyin          NVARCHAR(128),
      description     NVARCHAR(1024),
      value           NVARCHAR(512),
      system          NVARCHAR(512),
      diagnosisInfo   NTEXT,
      createdUser     NVARCHAR(64),
      createdDate     DATETIME,
      lastUpdatedUser NVARCHAR(64),
      lastUpdatedDate DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.BodyPart', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.BodyPart'
    CREATE TABLE dbo.BodyPart (
      code        VARCHAR(32),
      pinyin      VARCHAR(512),
      description VARCHAR(512)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.DynamicFormTemplate', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.DynamicFormTemplate'
    CREATE TABLE dbo.DynamicFormTemplate(
      id              INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
      category        NVARCHAR(50),
      templateId      NVARCHAR(200),
      templateName    NVARCHAR(256),
      templatePath    NVARCHAR(256)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.DynamicFormTemplate', 'U') IS NOT NULL
  BEGIN
    PRINT 'Database - INSERT INTO dbo.DynamicFormTemplate'
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('Registration', '患者注册', 'config\template\Registration.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PhysicianPatientBanner', '医生组PatientBanner', 'config\template\PhysicianPatientBanner.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PhysicistPatientBanner', '物理师组PatientBanner', 'config\template\PhysicistPatientBanner.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlaceImmobilizationAndCTOrder', '制模定位申请单', 'config\template\PlaceImmobilizationAndCTOrder.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlaceImmobilizationAndCTOrderDefaultValue', '制模定位申请单（默认值）', 'config\template\PlaceImmobilizationAndCTOrderDefaultValue.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('DoImmoRecord', '制模记录单', 'config\template\DoImmoRecord.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('DoCTRecord', 'CT记录单', 'config\template\DoCTRecord.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlacePlanningOrder', '放射计划申请单', 'config\template\PlacePlanningOrder.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('DoRepositioning', '复位记录单', 'config\template\DoRepositioning.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('DoTreatment', '放射治疗记录单', 'config\template\DoTreatment.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PrintCTAndImmobilizationOrder', '制模定位申请单（打印）', 'config\template\PrintCTAndImmobilizationOrder.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('CTContrastAgentConsent', 'ct造影剂使用知情同意书', 'config\template\CTContrastAgentConsent.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('RadiotherapyMRIConsent', 'MRI知情同意书', 'config\template\RadiotherapyMRIConsent.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('RadiotherapyMRIEnhancementConsent', 'MRI增强知情同意书', 'config\template\RadiotherapyMRIEnhancementConsent.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('RadiotherapyMRITreatmentFlowOrder', '只包含CT流程的流程引导', 'config\template\RadiotherapyMRITreatmentFlowOrder.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('RadiotherapyTreatmentFlowOrder', '包含CT和MRI流程的流程引导', 'config\template\RadiotherapyTreatmentFlowOrder.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('RadiotherapyMagneticResonanceChecklist', '放疗磁共振定位检查前确认表', 'config\template\RadiotherapyMagneticResonanceChecklist.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlaceMRIOrder', 'MRI定位申请单', 'config\template\PlaceMRIOrder.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PrintTreatmentRecords', '放射治疗记录单（打印）', 'config\template\PrintTreatmentRecords.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlanningOrder', '计划申请单', 'config\template\PlanningOrder.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('TreatmentApproval', '复位审核单', 'config\template\TreatmentApproval.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlanningApproval', '计划确认单', 'config\template\PlanningApproval.json');
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('DoMRIRecord', 'MRI记录单', 'config\template\DoMRIRecord.json');
    EXEC('UPDATE dbo.DynamicFormTemplate SET category = ''CCIP'' ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.DynamicFormInstance', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.DynamicFormInstance'
    CREATE TABLE dbo.DynamicFormInstance(
      id                      INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
      patientSer              BIGINT,
      hisId                   NVARCHAR(64),
      encounterId             INT,
      dynamicFormInstanceInfo NTEXT,
      createduser             NVARCHAR(64),
      createdDate             DATETIME,
      lastUpdatedUser         NVARCHAR(64),
      lastUpdatedDate         DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.DynamicFormItem', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.DynamicFormItem'
    CREATE TABLE dbo.DynamicFormItem(
      id          INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
      patientSer  BIGINT,
      hisId       NVARCHAR(64),
      encounterId INT,
      itemKey     NVARCHAR(64),
      itemValue   NTEXT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.DynamicFormRecord', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.DynamicFormRecord'
    CREATE TABLE dbo.DynamicFormRecord(
      id                     INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
      hisId                  NVARCHAR(64),
      encounterId            INT,
      carePathInstanceId     NVARCHAR(32),
      templateId             NVARCHAR(200),
      templateInfo           NTEXT,
      dynamicFormRecordInfo  NTEXT,
      createdUser            NVARCHAR(64),
      createdDate            DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.ISOCenter', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.ISOCenter'
    CREATE TABLE dbo.ISOCenter(
      id              INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
      patientSer      BIGINT,
      hisId           NVARCHAR(32),
      encounterId     INT,
      isoCenterInfo   NTEXT,
      createdUser     NVARCHAR(64),
      createdDate     DATETIME,
      lastUpdatedUser NVARCHAR(64),
      lastUpdatedDate DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.ISOCenterItem', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.ISOCenterItem'
    CREATE TABLE dbo.ISOCenterItem(
      isoCenterId INT,
      planId      NVARCHAR(32),
      isoName     NVARCHAR(64),
      vrt         NUMERIC(8, 3),
      lng         NUMERIC(8, 3),
      lat         NUMERIC(8, 3)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.ConfirmPayment', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.ConfirmPayment'
    CREATE TABLE dbo.ConfirmPayment (
      id              INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
      patientSer      BIGINT,
      hisId           NVARCHAR(64),
      encounterId     INT,
      createdUser     NVARCHAR(64),
      createdDate     DATETIME,
      lastUpdatedUser NVARCHAR(64),
      lastUpdatedDate DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.ConfirmStatus', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.ConfirmStatus'
    CREATE TABLE dbo.ConfirmStatus (
      confirmPaymentId      INT,
      carePathInstanceId    INT,
      activityCode          NVARCHAR(64),
      activityContent       NVARCHAR(128),
      status                INT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.TreatmentConfirmStatus', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.TreatmentConfirmStatus'
    CREATE TABLE dbo.TreatmentConfirmStatus (
      confirmPaymentId    INT,
      activityCode        NVARCHAR(64),
      activityContent     NVARCHAR(128),
      totalPaymentCount   INT,
      confirmPaymentCount INT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.TreatmentWorkload', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.TreatmentWorkload'
    CREATE TABLE dbo.TreatmentWorkload (
      id              INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
      patientSer      BIGINT,
      hisId           NVARCHAR(64),
      encounterId     INT,
      treatmentDate   DATETIME,
      createdUser     NVARCHAR(64),
      createdDate     DATETIME,
      lastUpdatedUser NVARCHAR(64),
      lastUpdatedDate DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.TreatmentWorkloadPlan', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.TreatmentWorkloadPlan'
    CREATE TABLE dbo.TreatmentWorkloadPlan (
      workloadId         INT,
      planId             NVARCHAR(64),
      deliveredFractions INT,
      selected           bit,
      comment            NVARCHAR(512)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.TreatmentWorkloadSignature', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.TreatmentWorkloadSignature'
    CREATE TABLE dbo.TreatmentWorkloadSignature (
      workloadId   INT,
      userName     NVARCHAR(64),
      resourceName NVARCHAR(128),
      resourceSer  INT,
      signDate     DATETIME,
      signType     NVARCHAR(12)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.TreatmentWorkloadWorker', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.TreatmentWorkloadWorker'
    CREATE TABLE dbo.TreatmentWorkloadWorker (
      workloadId INT,
      workerName NVARCHAR(128),
      orderNum   INT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.TreatmentAppointment', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.TreatmentAppointment'
    CREATE TABLE dbo.TreatmentAppointment(
      id              INT IDENTITY (1, 1) PRIMARY KEY,
      patientSer      BIGINT,
      uid             CHAR(36)     NOT NULL,
      appointmentId   NVARCHAR(32),
      hisId           NVARCHAR(64) NULL,
      encounterId     INT          NOT NULL,
      patientId       NVARCHAR(32) NULL,
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
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.VID', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.VID'
    CREATE TABLE dbo.VID(
      vid INT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.TaskLocking', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.TaskLocking'
    CREATE TABLE dbo.TaskLocking(
      taskId       NVARCHAR(36) NOT NULL,
      activityType NVARCHAR(32) NOT NULL,
      lockUserName NVARCHAR(32) NOT NULL,
      resourceSer  INT          NOT NULL,
      resourceName NVARCHAR(64) NOT NULL,
      lockTime     DATETIME     NOT NULL
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.QueuingManagement', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.QueuingManagement'
    CREATE TABLE dbo.QueuingManagement(
      id            INT IDENTITY (1, 1) PRIMARY KEY,
      patientSer     BIGINT,
      appointmentId NVARCHAR(36) NOT NULL,
      activityCode  NVARCHAR(32) NOT NULL,
      hisId         NVARCHAR(32) NULL,
      encounterId   INT          NOT NULL,
      deviceId      NVARCHAR(32) NOT NULL,
      checkInStatus NVARCHAR(12) NOT NULL,
      checkInIdx    INT          NOT NULL,
      startTime     DATETIME     NOT NULL,
      checkInTime   DATETIME     NOT NULL
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.UserProfile', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.UserProfile'
    CREATE TABLE dbo.UserProfile(
      id            INT IDENTITY(1,1) NOT NULL,
      userId        NVARCHAR(64) NOT NULL,
      userRole      TINYINT NOT NULL, -- 1:Login User, 2:Patient, 3, 99:Others
      propertyName  NVARCHAR(20) NOT NULL, -- property Name, no more than 20 chars
      propertyValue NVARCHAR(50) NULL,
      crtUser       NVARCHAR(64) NOT NULL,
      crtDT         DATETIME DEFAULT (GETDATE()) NOT NULL,
      updUser       NVARCHAR(64) NOT NULL,
      updDT         DATETIME DEFAULT (GETDATE()) NOT NULL
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.SetupPhoto', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.SetupPhoto'
    CREATE TABLE dbo.SetupPhoto(
      id            INT IDENTITY(1,1) PRIMARY KEY,
      patientSer    BIGINT,
      deviceId      NVARCHAR(64),
      hisId         NVARCHAR(64),
      createdUser   NVARCHAR(64),
      createdDT     DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.SetupPhotoDetail', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.SetupPhotoDetail'
    CREATE TABLE dbo.SetupPhotoDetail(
      id            INT IDENTITY(1,1) PRIMARY KEY,
      setupPhotoId  INT,
      photoId       NVARCHAR(200),
      photo         VARBINARY(MAX)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.SetupPhotoArchive', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.SetupPhotoArchive'
    CREATE TABLE dbo.SetupPhotoArchive(
      id                     INT IDENTITY(1,1) PRIMARY KEY,
      dynamicFormRecordId    INT,
      photoId                NVARCHAR(200),
      photo                  VARBINARY(MAX),
      createdUser            NVARCHAR(64),
      createdDT              DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.TargetVolume', 'U') IS  NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.TargetVolume'
    CREATE TABLE dbo.TargetVolume(
      id               INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
      patientSer       BIGINT,
      hisId            NVARCHAR(64),
      encounterId      INT NOT NULL,
      name             NVARCHAR(64),
      memo             NVARCHAR(512)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.TargetVolumeItem', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.TargetVolumeItem'
    CREATE TABLE dbo.TargetVolumeItem(
      targetVolumeId           INT NOT NULL,
      fieldId                  NVARCHAR(64),
      fieldValue               NVARCHAR(64),
      rNum                     INT,
      seq                      INT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.PlanTargetVolume', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.PlanTargetVolume'
    CREATE TABLE dbo.PlanTargetVolume(
      patientSer BIGINT,
      hisId            NVARCHAR(64),
      encounterId      INT NOT NULL,
      planId           NVARCHAR(64),
      targetVolumeName NVARCHAR(64)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND OBJECT_ID('dbo.SystemConfig', 'U') IS NULL
  BEGIN
    PRINT 'Database - CREATE TABLE dbo.SystemConfig'
    CREATE TABLE dbo.SystemConfig (
      id             INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
      category       NVARCHAR(50),
      name           NVARCHAR(64),
      value          NVARCHAR(256),
      orderBy        INT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
IF @VERSION_HISTORY IS NULL AND NOT EXISTS (SELECT * FROM dbo.SystemConfig)
  BEGIN
    PRINT 'Database - INSERT INTO dbo.SystemConfig'
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('activityRefreshTime', '20', 1);
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('defaultDepartment', '1', 1);
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('appointmentStoredToLocal', 'false', 1);
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('TreatmentActivityCode', 'DoTreatment', 1);
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('CallingBatchCount', '3', 1);
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('CountPerCalling', '3', 1);
    INSERT INTO dbo.SystemConfig (name, value, OrderBy) VALUES ('VIDPrefix', 'V', 1);
    INSERT INTO dbo.SystemConfig (name, value, OrderBy) VALUES ('VID', '1', 1);
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('DiagnosisSearchTopN','50',1);
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('RecurringAppointmentTimeLimit', '50', 1)
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('CountPerSlot', 3, 1)
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('RefreshCacheFromFHIRInterval', 5, 1);
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('PatientIdInAria', 'ariaId', 1);
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('PatientId2InAria', 'hisId', 1);
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('auditLogEntryNumPerBatch', '100', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('default_banner', 'Physicist', 'PhysicistPatientBanner', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('default', 'defaultTokenCacheTimeoutInMinutes', '30', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('default', 'resourceAutoUnlockInMinutes', '5', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('default', 'ospTokenValidationInterval', '5', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('default', 'fhirTokenAuthEnabled', '0', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('default', 'performanceLogging', 'false', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('FHIRServer', 'fhirConnectionTimeout', '40000', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('FHIRServer', 'fhirConnectionRequestTimeout', '40000', 2);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('FHIRServer', 'fhirSocketTimeout', '40000', 3);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('FHIRServer', 'fhirLanguage', 'CHS', 4);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('HttpClient', 'timeout', '10000ms', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('HttpClient', 'connectionTimeout', '10000ms', 2);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('HttpClient', 'connectionRequestTimeout', '10000ms', 3);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('HttpClient', 'timeToLive', '1 hour', 4);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('HttpClient', 'cookiesEnabled', 'false', 5);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('HttpClient', 'maxConnections', '1024', 6);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('HttpClient', 'maxConnectionsPerRoute', '1024', 7);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('HttpClient', 'keepAlive', '0s', 8);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('locale', 'language', 'zh', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('locale', 'country', 'CN', 2);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('AuditLog', 'hostName', 'localhost', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('AuditLog', 'port', '55020', 2);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('AuditLog', 'timeoutInMs', '300', 3);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('AuditLog', 'logThreadCount', '1', 4);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('AuditLog', 'logBatchSize', '100', 5);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('GroupRole', 'GroupRoleOncologist', 'Oncologist', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('GroupRole', 'GroupRoleNurse', 'Nurse', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('GroupRole', 'GroupRolePhysicist','Physicist', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('GroupRole', 'GroupRoleTherapist','Therapist', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('GroupPrefix', 'GroupOncologistPrefix', 'Oncologist', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('GroupPrefix', 'GroupNursePrefix', 'Nurse', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('GroupPrefix', 'GroupPhysicistPrefix', 'Physicist', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('GroupPrefix', 'GroupTechnicianPrefix', 'Technician', 1);
    INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES ('PhysicistGroup', 'physicistGroupingActivityCode', 'AssignTPS', 1);
  END
GO

/***** Database 01.00 - Begin *****/
DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY
  PRINT 'Database 01.00 - Begin';
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.Patient', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.Patient'
    CREATE TABLE dbo.Patient(
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
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.Encounter', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.Encounter'
    CREATE TABLE dbo.Encounter(
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
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.EncounterActivity', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.EncounterActivity'
    CREATE TABLE dbo.EncounterActivity(
      encounterId INT NOT NULL,
      activityId INT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.Diagnosis', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.Diagnosis'
    CREATE TABLE dbo.Diagnosis(
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
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.DynamicFormTemplate', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.DynamicFormTemplate'
    CREATE TABLE dbo.DynamicFormTemplate(
      id              INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
      templateId      NVARCHAR(200),
      templateName    NVARCHAR(256),
      templatePath    NVARCHAR(256),
      createdUser     NVARCHAR(64),
      createdDate     DATETIME,
      lastUpdatedUser NVARCHAR(64),
      lastUpdatedDate DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.DynamicFormInstance', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.DynamicFormInstance'
    CREATE TABLE dbo.DynamicFormInstance(
      id                      INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
      hisId                   NVARCHAR(64),
      encounterId             INT,
      createduser             NVARCHAR(64),
      createdDate             DATETIME,
      lastUpdatedUser         NVARCHAR(64),
      lastUpdatedDate         DATETIME,
      dynamicFormInstanceInfo NTEXT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.DynamicFormItem', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.DynamicFormItem'
    CREATE TABLE dbo.DynamicFormItem(
      hisId       NVARCHAR(64),
      encounterId INT,
      itemKey     NVARCHAR(64),
      itemValue   NTEXT,
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.ISOCenter', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.ISOCenter'
    CREATE TABLE dbo.ISOCenter(
      id              INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
      hisId           NVARCHAR(32),
      encounterId     INT,
      createdUser     NVARCHAR(64),
      createdDate     DATETIME,
      lastUpdatedUser NVARCHAR(64),
      lastUpdatedDate DATETIME,
      isoCenterInfo   NTEXT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.ISOCenterItem', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.ISOCenterItem'
    CREATE TABLE dbo.ISOCenterItem(
      isoCenterId INT,
      planId      NVARCHAR(32),
      isoName     NVARCHAR(64),
      vrt         NUMERIC(8, 3),
      lng         NUMERIC(8, 3),
      lat         NUMERIC(8, 3)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.ConfirmPayment', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.ConfirmPayment'
    CREATE TABLE dbo.ConfirmPayment (
      id              INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
      hisId           NVARCHAR(64),
      encounterId     INT,
      createdUser     NVARCHAR(64),
      createdDate     DATETIME,
      lastUpdatedUser NVARCHAR(64),
      lastUpdatedDate DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.ConfirmStatus', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.ConfirmStatus'
    CREATE TABLE dbo.ConfirmStatus (
      confirmPaymentId INT,
      activityCode     NVARCHAR(64),
      activityContent  NVARCHAR(128),
      status           INT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.TreatmentConfirmStatus', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.TreatmentConfirmStatus'
    CREATE TABLE dbo.TreatmentConfirmStatus (
      confirmPaymentId    INT,
      activityCode        NVARCHAR(64),
      activityContent     NVARCHAR(128),
      totalPaymentCount   INT,
      confirmPaymentCount INT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.TreatmentWorkload', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.TreatmentWorkload'
    CREATE TABLE dbo.TreatmentWorkload (
      id              INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
      hisId           NVARCHAR(64),
      encounterId     INT,
      treatmentDate   DATETIME,
      createdUser     NVARCHAR(64),
      createdDate     DATETIME,
      lastUpdatedUser NVARCHAR(64),
      lastUpdatedDate DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.TreatmentWorkloadPlan', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.TreatmentWorkloadPlan'
    CREATE TABLE dbo.TreatmentWorkloadPlan (
      workloadId         INT,
      planId             NVARCHAR(64),
      deliveredFractions INT,
      comment            NVARCHAR(512)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.TreatmentWorkloadSignature', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.TreatmentWorkloadSignature'
    CREATE TABLE dbo.TreatmentWorkloadSignature (
      workloadId   INT,
      userName     NVARCHAR(64),
      resourceName NVARCHAR(128),
      resourceSer  INT,
      signDate     DATETIME,
      signType     NVARCHAR(12)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.TreatmentWorkloadWorker', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.TreatmentWorkloadWorker'
    CREATE TABLE dbo.TreatmentWorkloadWorker (
      workloadId INT,
      workerName NVARCHAR(128),
      orderNum   INT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.SystemConfig', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.SystemConfig';
    CREATE TABLE dbo.SystemConfig (
      id      INT IDENTITY (1, 1) PRIMARY KEY NOT NULL,
      name    NVARCHAR(64),
      value   NVARCHAR(256),
      orderBy INTEGER
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'insuranceType')
  BEGIN
    PRINT 'Database 01.00 - INSERT INTO dbo.SystemConfig [insuranceType]'
    INSERT INTO dbo.SystemConfig(name, value, orderBy) VALUES ('insuranceType', N'北京医保', 1);
    INSERT INTO dbo.SystemConfig(name, value, orderBy) VALUES ('insuranceType', N'北京新农合', 2);
    INSERT INTO dbo.SystemConfig(name, value, orderBy) VALUES ('insuranceType', N'自费', 3);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'activityRefreshTime')
  BEGIN
    PRINT 'Database 01.00 - INSERT INTO dbo.SystemConfig [activityRefreshTime]'
    INSERT INTO dbo.SystemConfig(name, value, orderBy) VALUES ('activityRefreshTime', '20', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'defaultDepartment')
  BEGIN
    PRINT 'Database 01.00 - INSERT INTO dbo.SystemConfig [defaultDepartment]'
    INSERT INTO dbo.SystemConfig(name, value, orderBy) VALUES ('defaultDepartment', '1', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'appointmentStoredToLocal')
  BEGIN
    PRINT 'Database 01.00 - INSERT INTO dbo.SystemConfig [appointmentStoredToLocal]'
    INSERT INTO dbo.SystemConfig(name, value, orderBy) VALUES ('appointmentStoredToLocal', 'false', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'TreatmentActivityCode')
  BEGIN
    PRINT 'Database 01.00 - INSERT INTO dbo.SystemConfig [TreatmentActivityCode]'
    INSERT INTO dbo.SystemConfig(name, value, orderBy) VALUES ('TreatmentActivityCode', 'DoTreatment', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'CallingBatchCount')
  BEGIN
    PRINT 'Database 01.00 - INSERT INTO dbo.SystemConfig [CallingBatchCount]'
    INSERT INTO dbo.SystemConfig(name, value, orderBy) VALUES ('CallingBatchCount', '3', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'CountPerCalling')
  BEGIN
    PRINT 'Database 01.00 - INSERT INTO dbo.SystemConfig [CountPerCalling]'
    INSERT INTO dbo.SystemConfig(name, value, orderBy) VALUES ('CountPerCalling', '3', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.TreatmentAppointment', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.TreatmentAppointment'
    CREATE TABLE dbo.TreatmentAppointment(
      id              INT IDENTITY (1, 1) PRIMARY KEY,
      uid             CHAR(36)     NOT NULL,
      appointmentId   NVARCHAR(32),
      hisId           NVARCHAR(64) NOT NULL,
      encounterId     INT           NOT NULL,
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
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.VID', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.VID'
    CREATE TABLE dbo.VID(
      vid INT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'VIDPrefix')
  BEGIN
    PRINT 'Database 01.00 - INSERT INTO dbo.SystemConfig [VIDPrefix]'
    INSERT INTO dbo.SystemConfig(name, value, OrderBy) VALUES ('VIDPrefix', 'V', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'VID')
  BEGIN
    PRINT 'Database 01.00 - INSERT INTO dbo.SystemConfig [VID]'
    INSERT INTO dbo.SystemConfig(name, value, OrderBy) VALUES ('VID', '1', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.TaskLocking', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.TaskLocking'
    CREATE TABLE dbo.TaskLocking(
      taskId       NVARCHAR(36) NOT NULL,
      activityType NVARCHAR(32) NOT NULL,
      lockUserName NVARCHAR(32) NOT NULL,
      resourceSer  INT          NOT NULL,
      resourceName NVARCHAR(64) NOT NULL,
      lockTime     DATETIME     NOT NULL
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.QueuingManagement', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.QueuingManagement'
    CREATE TABLE dbo.QueuingManagement(
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
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.EncounterCarePath', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.00 - CREATE TABLE dbo.EncounterCarePath'
    CREATE TABLE dbo.EncounterCarePath(
      encounterId  INT NOT NULL,
      cpInstanceId INT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY
  BEGIN
    PRINT 'Database 01.00 - End'
  END
GO
/***** Database 01.00 - End *****/

/***** MR1 Begin  *****/
DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY
  PRINT 'Database 01.01 - Begin'
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.EncounterCarePath', 'U') IS NOT NULL
  BEGIN
    PRINT 'Database 01.01 - dbo.EncounterCarePath Fill Up'
    INSERT dbo.EncounterCarePath(encounterId, cpInstanceId)
      SELECT e.id AS encounterId, t.TemplateSer AS cpInstanceId FROM dbo.Encounter e
        INNER JOIN [$(ARIA_DB)].dbo.Template t
          ON t.PatientSer = e.patientSer
      WHERE (CAST(e.id AS VARCHAR) + ':' + CAST(t.TemplateSer AS VARCHAR))
            NOT IN (SELECT CAST(encounterId AS VARCHAR) + ':' + CAST(cpInstanceId AS VARCHAR) FROM dbo.EncounterCarePath)
    ---- update encounter CarePath in Encounter Json.
    PRINT 'Database 01.01 - UPDATE dbo.Encounter encounterActivityList -> encounterCarePathList'
    UPDATE dbo.Encounter
    SET encounterInfo = REPLACE(REPLACE(CAST(encounterInfo AS NVARCHAR(3000)), 'encounterActivityList', 'encounterCarePathList'), 'activityId', 'cpInstanceId')
    FROM dbo.Encounter
    WHERE CHARINDEX('encounterActivityList', CAST(encounterInfo AS NVARCHAR(3000))) > 0
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.Diagnosis', 'pinyin') IS NULL
  BEGIN
    ---- Remove all data, and Server Startup would retrieve all from FHIR.
    PRINT 'Database 01.01 - DELETE FROM dbo.Diagnosis'
    DELETE FROM dbo.Diagnosis;
    PRINT 'Database 01.01 - ALTER TABLE dbo.Diagnosis ADD pinyin'
    ALTER TABLE dbo.Diagnosis ADD pinyin NVARCHAR(128);
  END
GO

/****** User Profile Table  ******/
DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.UserProfile', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.01 - CREATE TABLE dbo.UserProfile'
    CREATE TABLE dbo.UserProfile(
      id            INT IDENTITY(1,1) NOT NULL,
      userId        NVARCHAR(64) NOT NULL,
      userRole      TINYINT NOT NULL, -- 1:Login User, 2:Patient, 3, 99:Others
      propertyName  NVARCHAR(20) NOT NULL, -- property Name, no more than 20 chars
      propertyValue NVARCHAR(50) NULL,
      crtUser       NVARCHAR(64) NOT NULL,
      crtDT         DATETIME DEFAULT (GETDATE()) NOT NULL,
      updUser       NVARCHAR(64) NOT NULL,
      updDT         DATETIME DEFAULT (GETDATE()) NOT NULL
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'DiagnosisSearchTopN')
  BEGIN
    PRINT 'Database 01.01 - INSERT INTO dbo.SystemConfig [DiagnosisSearchTopN]'
    INSERT INTO dbo.SystemConfig(name, value, orderBy) VALUES ('DiagnosisSearchTopN','50',1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.Encounter', 'insuranceTypeCode') IS NULL
  BEGIN
    PRINT 'Database 01.01 - ALTER TABLE dbo.Encounter ADD insuranceTypeCode'
    ALTER TABLE dbo.Encounter ADD insuranceTypeCode NVARCHAR(32);
    PRINT 'Database 01.01 - UPDATE dbo.Encounter SET insuranceTypeCode'
    EXEC ('
      UPDATE dbo.Encounter SET insuranceTypeCode=''1'' WHERE insuranceType = N''北京医保'';
      UPDATE dbo.Encounter SET encounterInfo = REPLACE(CAST(encounterInfo AS varchar(8000)), N''"insuranceType":"北京医保",'', ''"insuranceTypeCode":"1","insuranceType":"北京医保",'');
      UPDATE dbo.Encounter SET insuranceTypeCode=''2'' WHERE insuranceType = N''北京新农合'';
      UPDATE dbo.Encounter SET encounterInfo = REPLACE(CAST(encounterInfo AS varchar(8000)), N''"insuranceType":"北京新农合",'', ''"insuranceTypeCode":"2","insuranceType":"北京新农合",'');
      UPDATE dbo.Encounter SET insuranceTypeCode=''3'' WHERE insuranceType = N''自费'';
      UPDATE dbo.Encounter SET encounterInfo = REPLACE(CAST(encounterInfo AS varchar(8000)), N''"insuranceType":"自费",'', ''"insuranceTypeCode":"3","insuranceType":"自费",'');
      UPDATE dbo.Encounter SET encounterInfo = REPLACE(CAST(encounterInfo AS varchar(8000)), ''"insuranceType":"",'', ''"insuranceTypeCode":"","insuranceType":"",'');
      UPDATE dbo.Encounter SET encounterInfo = REPLACE(CAST(encounterInfo AS varchar(8000)), ''"insuranceType":"0",'', ''"insuranceTypeCode":"","insuranceType":"0",'');
      UPDATE dbo.Encounter SET encounterInfo = REPLACE(CAST(encounterInfo AS varchar(8000)), ''"insuranceType":null,'', ''"insuranceTypeCode":"","insuranceType":null,'');
      UPDATE dbo.Encounter SET insuranceTypeCode=''1'', insuranceType = N''北京医保'' WHERE insuranceType = ''1'';
      UPDATE dbo.Encounter SET encounterInfo = REPLACE(CAST(encounterInfo AS varchar(8000)), ''"insuranceType":"1",'', N''"insuranceTypeCode":"1","insuranceType":"北京医保",'');
      UPDATE dbo.Encounter SET insuranceTypeCode=''2'', insuranceType = N''北京新农合'' WHERE insuranceType = ''2'';
      UPDATE dbo.Encounter SET encounterInfo = REPLACE(CAST(encounterInfo AS varchar(8000)), ''"insuranceType":"2",'', N''"insuranceTypeCode":"2","insuranceType":"北京新农合",'');
      UPDATE dbo.Encounter SET insuranceTypeCode=''3'', insuranceType = N''自费'' WHERE insuranceType = ''3'';
      UPDATE dbo.Encounter SET encounterInfo = REPLACE(CAST(encounterInfo AS varchar(8000)), ''"insuranceType":"3",'', N''"insuranceTypeCode":"3","insuranceType":"自费",'');
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.BodyPart', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.01 - CREATE TABLE  dbo.BodyPart'
    CREATE TABLE  dbo.BodyPart (
      code        VARCHAR(32),
      description VARCHAR(512),
      pinyin      VARCHAR(512)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.TreatmentWorkloadPlan', 'selected') IS NULL
  BEGIN
    PRINT 'Database 01.01 - ALTER TABLE dbo.TreatmentWorkloadPlan ADD selected'
    ALTER TABLE dbo.TreatmentWorkloadPlan ADD selected bit;
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.DynamicFormItem', 'id') IS NULL
  BEGIN
    PRINT 'Database 01.01 - ALTER TABLE dbo.DynamicFormItem ADD id'
    ALTER TABLE dbo.DynamicFormItem ADD id INT IDENTITY(1,1) PRIMARY KEY NOT NULL;
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.DynamicFormRecord', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.01 - CREATE TABLE dbo.DynamicFormRecord'
    CREATE TABLE dbo.DynamicFormRecord(
      id                     INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
      hisId                  NVARCHAR(64),
      encounterId            INT,
      carePathInstanceId     NVARCHAR(32),
      templateId             NVARCHAR(200),
      createdUser            NVARCHAR(64),
      createdDate            DATETIME,
      dynamicFormRecordInfo  NTEXT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
/****** Group Default View  ******/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.SystemConfig', 'category') IS NULL
  BEGIN
    PRINT 'Database 01.01 - ALTER TABLE dbo.SystemConfig ADD category'
    ALTER TABLE dbo.SystemConfig ADD category NVARCHAR(50);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
/****** Group Default View: initial data  ******/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY
  BEGIN
    PRINT 'Database 01.01 - INSERT INTO dbo.SystemConfig [default_view, default_tab]'
    EXEC ('
      IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE value = ''schedule-home'' AND category = ''default_view'' )
      INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES (''default_view'', ''Nursing'', ''schedule-home'', 1);
      IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE value = ''schedule'' AND category = ''default_tab'' )
      INSERT INTO dbo.SystemConfig (category, name, value, orderBy) VALUES (''default_tab'', ''Nursing'', ''schedule'', 1);
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
/***** Setup Photo *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.SetupPhoto', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.01 - CREATE TABLE dbo.SetupPhoto'
    CREATE TABLE dbo.SetupPhoto(
      id            INT IDENTITY(1,1) PRIMARY KEY,
      deviceId      NVARCHAR(64),
      hisId         NVARCHAR(64),
      createdUser   NVARCHAR(64),
      createdDT     DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.SetupPhotoDetail', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.01 - CREATE TABLE dbo.SetupPhotoDetail'
    CREATE TABLE dbo.SetupPhotoDetail(
      id            INT IDENTITY(1,1) PRIMARY KEY,
      setupPhotoId  INT,
      photoId       NVARCHAR(200),
      photo         VARBINARY(MAX)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.SetupPhotoArchive', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.01 - CREATE TABLE dbo.SetupPhotoArchive'
    CREATE TABLE dbo.SetupPhotoArchive(
      id                     INT IDENTITY(1,1) PRIMARY KEY,
      dynamicFormRecordId    INT,
      photoId                NVARCHAR(200),
      photo                  VARBINARY(MAX),
      createdUser            NVARCHAR(64),
      createdDT              DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.PatientIdentifier', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.01 - CREATE TABLE dbo.PatientIdentifier'
    CREATE TABLE dbo.PatientIdentifier(
      domain              NVARCHAR(64),
      value               NVARCHAR(64),
      type                NVARCHAR(8), -- HC-hisId PI-ariaId NNCHN=nationalId
      status              NVARCHAR(8),
      used                NVARCHAR(8)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
/***** Recurring Appointment Time Limit: 50 by default *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'RecurringAppointmentTimeLimit')
  BEGIN
    PRINT 'Database 01.01 - INSERT INTO dbo.SystemConfig [RecurringAppointmentTimeLimit]'
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('RecurringAppointmentTimeLimit', '50', 1)
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
/***** Default Patient Banner for Physicist *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY
  BEGIN
    PRINT 'Database 01.01 - INSERT INTO dbo.SystemConfig [Physicist]'
    EXEC ('
      IF NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = ''Physicist'')
      INSERT INTO dbo.SystemConfig (name, value, orderBy, category) VALUES (''Physicist'', ''PhysicistPatientBanner'', 1, ''default_banner'');

      IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE templateId = ''PhysicianPatientBanner'')
      INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES (''PhysicianPatientBanner'', N''医生组PatientBanner'', ''config\template\PhysicianPatientBanner.json'');

      IF NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE templateId = ''PhysicistPatientBanner'')
      INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES (''PhysicistPatientBanner'', N''物理师组PatientBanner'', ''config\template\PhysicistPatientBanner.json'');
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
/****** Recurring Appointment: The max count per time slot ******/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'CountPerSlot')
  BEGIN
    PRINT 'Database 01.01 - INSERT INTO dbo.SystemConfig [CountPerSlot]'
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('CountPerSlot', 3, 1)
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
/***** Refresh cache from FHIR interval and set PatientID1, PatientID2 mapping *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'RefreshCacheFromFHIRInterval')
  BEGIN
    PRINT 'Database 01.01 - INSERT INTO dbo.SystemConfig [RefreshCacheFromFHIRInterval]'
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('RefreshCacheFromFHIRInterval', 5, 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'PatientIdInAria')
  BEGIN
    PRINT 'Database 01.01 - INSERT INTO dbo.SystemConfig [PatientIdInAria]'
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('PatientIdInAria', 'ariaId', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'PatientId2InAria')
  BEGIN
    PRINT 'Database 01.01 - INSERT INTO dbo.SystemConfig [PatientId2InAria]'
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('PatientId2InAria', 'hisId', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
/****** Physicist Grouping: The flag of view all patients for physicist ******/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'ViewAllPatientsForPhysicist')
  BEGIN
    PRINT 'Database 01.01 - INSERT INTO dbo.SystemConfig [ViewAllPatientsForPhysicist]'
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('ViewAllPatientsForPhysicist', 'true', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
/***** Target Volume table *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.TargetVolumeItem', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.01 - CREATE TABLE dbo.TargetVolumeItem'
    CREATE TABLE dbo.TargetVolumeItem(
      targetVolumeId           INT NOT NULL,
      fieldId                  NVARCHAR(64),
      fieldValue               NVARCHAR(64),
      rNum                     INT,
      seq                      INT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.TargetVolume', 'U') IS  NULL
  BEGIN
    PRINT 'Database 01.01 - CREATE TABLE dbo.TargetVolume'
    CREATE TABLE dbo.TargetVolume(
      id               INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
      hisId            NVARCHAR(64),
      encounterId      INT NOT NULL,
      name             NVARCHAR(64),
      memo             NVARCHAR(512)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
/***** Plan Target Volume table *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.PlanTargetVolume', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.01 - CREATE TABLE dbo.PlanTargetVolume'
    CREATE TABLE dbo.PlanTargetVolume(
      hisId            NVARCHAR(64),
      encounterId      INT NOT NULL,
      planId           NVARCHAR(64),
      targetVolumeName NVARCHAR(64)
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
/***** Add configuration for the number of audit log entries per batch *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'auditLogEntryNumPerBatch')
  BEGIN
    PRINT 'Database 01.01 - INSERT INTO dbo.SystemConfig [auditLogEntryNumPerBatch]'
    INSERT INTO dbo.SystemConfig (name, value, orderBy) VALUES ('auditLogEntryNumPerBatch', '100', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.01'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY
  PRINT 'Database 01.01 - End'
GO
/********** MR1 End **********/

/********** MR2 Begin **********/
DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY
  PRINT 'Database 01.02 - Begin'
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
/***** Add column carePathInstanceId in table ConfirmStatus *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.ConfirmStatus', 'carePathInstanceId') IS NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.ConfirmStatus ADD carePathInstanceId'
    ALTER TABLE dbo.ConfirmStatus ADD carePathInstanceId INT;
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
/***** Add column patientSer in table  *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.Patient', 'patientSer') IS NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.Patient ADD patientSer'
    ALTER TABLE dbo.Patient ADD patientSer BIGINT;
    EXEC ('
      UPDATE dbo.Patient SET patientSer = cast(e.patientSer AS BIGINT) FROM Patient p
      JOIN dbo.Encounter e
      ON p.id = e.patientId
      WHERE p.patientSer is NULL
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.TreatmentAppointment', 'patientSer') IS NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.TreatmentAppointment ADD patientSer'
    ALTER TABLE dbo.TreatmentAppointment ADD patientSer BIGINT;
    EXEC ('
      UPDATE dbo.TreatmentAppointment SET patientSer = p.patientSer FROM dbo.TreatmentAppointment e
      JOIN dbo.Patient p
      ON p.id = e.patientId
      WHERE e.patientSer is NULL
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.QueuingManagement', 'patientSer') IS NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.QueuingManagement ADD patientSer'
    ALTER TABLE dbo.QueuingManagement ADD patientSer BIGINT;
    /********** this patientId is real patientSer **********/
    EXEC ('
      UPDATE dbo.QueuingManagement SET patientSer = e.patientId FROM dbo.QueuingManagement e
      WHERE e.patientSer is NULL
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.TreatmentWorkload', 'patientSer') IS NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.TreatmentWorkload ADD patientSer'
    ALTER TABLE dbo.TreatmentWorkload ADD patientSer BIGINT;
    EXEC ('
      UPDATE dbo.TreatmentWorkload SET patientSer = cast(e.patientSer AS BIGINT) FROM dbo.TreatmentWorkload t
      JOIN dbo.Encounter e
      ON t.encounterId = e.id
      WHERE t.patientSer is NULL
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.PlanTargetVolume', 'patientSer') IS NULL
  BEGIN
    PRINT 'Database 01.02 - patientSer'
    ALTER TABLE dbo.PlanTargetVolume ADD patientSer BIGINT;
    EXEC ('
      UPDATE dbo.PlanTargetVolume SET patientSer = cast(e.patientSer AS BIGINT) FROM dbo.PlanTargetVolume t
      JOIN dbo.Encounter e
      ON t.encounterId = e.id
      WHERE t.patientSer is NULL
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.ConfirmPayment', 'patientSer') IS NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.ConfirmPayment ADD patientSer'
    ALTER TABLE dbo.ConfirmPayment ADD patientSer BIGINT;
    EXEC ('
      UPDATE dbo.ConfirmPayment SET patientSer = e.patientSer FROM dbo.ConfirmPayment t
      JOIN dbo.Encounter e
      ON t.encounterId = e.id
      WHERE t.patientSer is NULL
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.ISOCenter', 'patientSer') IS NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.ISOCenter ADD patientSer'
    ALTER TABLE dbo.ISOCenter ADD patientSer BIGINT;
    EXEC ('
      UPDATE dbo.ISOCenter SET patientSer = cast(e.patientSer AS BIGINT) FROM dbo.ISOCenter t
      JOIN dbo.Encounter e
      ON t.encounterId = e.id
      WHERE t.patientSer is NULL
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.SetupPhoto', 'patientSer') IS NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.SetupPhoto ADD patientSer'
    ALTER TABLE dbo.SetupPhoto ADD patientSer BIGINT;
    EXEC ('
      UPDATE dbo.SetupPhoto SET patientSer = CAST(p.patientSer AS BIGINT) FROM dbo.SetupPhoto t
      JOIN dbo.Patient p
        ON p.hisId = t.hisId
      WHERE t.patientSer is NULL
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.TargetVolume', 'patientSer') IS NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.TargetVolume ADD patientSer'
    ALTER TABLE dbo.TargetVolume ADD patientSer BIGINT;
    EXEC ('
      UPDATE dbo.TargetVolume SET patientSer = cast(p.patientSer AS BIGINT) FROM dbo.TargetVolume t
      JOIN dbo.Patient p
        ON p.hisId = t.hisId
      WHERE t.patientSer is NULL
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.DynamicFormItem', 'patientSer') IS NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.DynamicFormItem ADD patientSer'
    ALTER TABLE dbo.DynamicFormItem ADD patientSer BIGINT;
    EXEC ('
      UPDATE dbo.DynamicFormItem SET patientSer = CAST(e.patientSer AS BIGINT) FROM dbo.DynamicFormItem t
      JOIN dbo.Encounter e
      ON t.encounterId = e.id
      WHERE t.patientSer is NULL
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.DynamicFormInstance', 'patientSer') IS NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.DynamicFormInstance ADD patientSer'
    ALTER TABLE dbo.DynamicFormInstance ADD patientSer BIGINT;
    EXEC ('
      UPDATE dbo.DynamicFormInstance SET patientSer = e.patientSer FROM dbo.DynamicFormInstance t
      JOIN dbo.Encounter e
      ON t.encounterId = e.id
      WHERE t.patientSer is NULL
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
/***** Create AssignResource Table *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.AssignResource', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.02 - CREATE TABLE dbo.AssignResource'
    CREATE TABLE dbo.AssignResource(
      id              INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
      resourceId      VARCHAR(32) NOT NULL,
      patientSer      BIGINT NOT NULL,
      encounterId     BIGINT NOT NULL,
      activityCode    NVARCHAR(64),
      createdUser     NVARCHAR(64),
      createdDate     DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
/***** Create EncounterEndPlan Table *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.EncounterEndPlan', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.02 - CREATE TABLE dbo.EncounterEndPlan'
    CREATE TABLE dbo.EncounterEndPlan(
      encounterId   BIGINT NOT NULL,
      planSetupId    VARCHAR(32) not null,
      planCreatedDt  DATETIME
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
/***** Launch Eclipse and done Task: check status of pending task/appointment *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.EclipseTask', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.02 - CREATE TABLE dbo.EclipseTask'
    CREATE TABLE dbo.EclipseTask(
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
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'Eclipse')
  BEGIN
    PRINT 'Database 01.02 - INSERT INTO dbo.SystemConfig [Eclipse]'
    EXEC ('INSERT INTO dbo.SystemConfig(name, value, orderBy, category) VALUES(''Eclipse'',''#CDC467'',1,''TPS'');')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'Pinnacle')
  BEGIN
    PRINT 'Database 01.02 - INSERT INTO dbo.SystemConfig [Pinnacle]'
    EXEC ('INSERT INTO dbo.SystemConfig(name, value, orderBy, category) VALUES(''Pinnacle'',''#FFE765'',1,''TPS'');')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
/***** Add Registration Template as Standard Template *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE templateId = 'Registration')
  BEGIN
    PRINT 'Database 01.02 - INSERT INTO dbo.DynamicFormTemplate [Registration]'
    INSERT INTO DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('Registration', N'患者注册', 'config\template\Registration.json');
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
/***** 2018-03-30 Update the 'PLANNED' status to 'IN_PROGRESS' in Status column of Encounter table *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY
  BEGIN
    PRINT 'Database 01.02 - UPDATE dbo.Encounter SET status [PLANNED] -> [IN_PROGRESS]'
    UPDATE dbo.Encounter SET status = 'IN_PROGRESS', encounterInfo = REPLACE(CAST(encounterInfo AS varchar(8000)), 'PLANNED', 'IN_PROGRESS') WHERE status = 'PLANNED';
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND EXISTS(
/** check if Encounter.patientSer is char or varchar **/
    SELECT CONVERT(VARCHAR(100), a.name), CONVERT(VARCHAR(50), b.name)
    FROM dbo.syscolumns a
      JOIN dbo.systypes b
        ON a.xusertype=b.xusertype
      JOIN dbo.sysobjects d
        ON a.id=d.id and d.xtype='U' AND d.name<>'dtproperties'
    WHERE d.name = 'Encounter' AND a.name = 'patientSer' AND b.name like '%char%'
)
  BEGIN
    PRINT 'Database 01.02 - patientSer var->int 1/3 EXEC SP_RENAME [patientSer] -> [patientSerChar]'
    EXEC SP_RENAME 'dbo.Encounter.patientSer','patientSerChar','COLUMN';

    PRINT 'Database 01.02 - patientSer var->int 2/3 ALTER TABLE dbo.Encounter ADD patientSer'
    ALTER TABLE dbo.Encounter ADD patientSer BIGINT;
    /**  Fill PatientSer of Encounter table **/
    PRINT 'Database 01.02 - patientSer var->int 3/3 UPDATE dbo.Encounter SET patientSer'
    EXEC('UPDATE dbo.Encounter SET patientSer = CAST(patientSerChar AS BIGINT) FROM dbo.Encounter')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
/***** Add the 'PrimaryPhysicianName' column to Encounter table *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.Encounter', 'primaryPhysicianName') IS NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.Encounter ADD primaryPhysicianName'
    ALTER TABLE dbo.Encounter ADD primaryPhysicianName NVARCHAR(32);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
/***** Add the 'physicianPhone' column to Encounter table *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.Encounter', 'physicianPhone') IS NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.Encounter ADD physicianPhone'
    ALTER TABLE dbo.Encounter ADD physicianPhone NVARCHAR(32);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.AssignResourceConfig', 'U') IS NULL
  BEGIN
    PRINT 'Database 01.02 - CREATE TABLE dbo.AssignResourceConfig'
    CREATE TABLE dbo.AssignResourceConfig(
      hospital            NVARCHAR(64),
      resourceId          BIGINT,
      resourceCode        NVARCHAR(64),
      resourceName        NVARCHAR(64),
      color               NVARCHAR(16),  -- #CDC467
      activityCode        NVARCHAR(64),  -- usage
      orderNo             SMALLINT
    );
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.AssignResourceConfig WHERE resourceCode = 'eclipse')
  BEGIN
    PRINT 'Database 01.02 - INSERT INTO dbo.AssignResourceConfig [eclipse]'
    INSERT INTO dbo.AssignResourceConfig(hospital, resourceId, resourceCode, resourceName, color, activityCode, orderNo)
    VALUES ('default', '11', 'eclipse', 'Eclipse', '#FF8B00', 'AssignTPS', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.AssignResourceConfig WHERE resourceCode = 'monaq')
  BEGIN
    PRINT 'Database 01.02 - INSERT INTO dbo.AssignResourceConfig [monaq]'
    INSERT INTO dbo.AssignResourceConfig(hospital, resourceId, resourceCode, resourceName, color, activityCode, orderNo)
    VALUES ('default', '12', 'monaq', 'MONAQ', '#74AF2F', 'AssignTPS', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.AssignResourceConfig WHERE resourceCode = 'tomo')
  BEGIN
    PRINT 'Database 01.02 - INSERT INTO dbo.AssignResourceConfig [tomo]'
    INSERT INTO dbo.AssignResourceConfig(hospital, resourceId, resourceCode, resourceName, color, activityCode, orderNo)
    VALUES ('default', '13', 'tomo', 'TOMO', '#66E765', 'AssignTPS', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
/** ALTER useless column from NOT NULL to NULL **/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.TreatmentAppointment', 'hisId') IS NOT NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.TreatmentAppointment ALTER COLUMN hisId'
    ALTER TABLE dbo.TreatmentAppointment ALTER COLUMN hisId NVARCHAR(64) NULL;
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.TreatmentAppointment', 'patientId') IS NOT NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.TreatmentAppointment ALTER COLUMN patientId'
    ALTER TABLE dbo.TreatmentAppointment ALTER COLUMN patientId NVARCHAR(32) NULL;
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.QueuingManagement', 'patientId') IS NOT NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.QueuingManagement ALTER COLUMN patientId'
    ALTER TABLE dbo.QueuingManagement ALTER COLUMN patientId INT NULL;
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.QueuingManagement', 'hisId') IS NOT NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.QueuingManagement ALTER COLUMN hisId'
    ALTER TABLE dbo.QueuingManagement ALTER COLUMN hisId NVARCHAR(32) NULL;
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
/*****  Add Gender *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.Patient', 'gender') IS NULL
  BEGIN
    PRINT 'Database 01.02 - ALTER TABLE dbo.Patient ADD gender'
    ALTER TABLE dbo.Patient ADD gender NVARCHAR(8);
    /** Fill in losing gender from Json data. **/
    PRINT 'Database 01.02 - UPDATE dbo.Patient SET gender'
    EXEC('
      UPDATE dbo.Patient SET gender = info.gender FROM dbo.Patient p
      JOIN (SELECT json.patientSer,
              CASE REPLACE(SUBSTRING(json.genderstr, 9, CHARINDEX('','', json.genderstr) - 9), ''"'', '''')
                WHEN ''UN'' THEN NULL
                WHEN ''null'' THEN NULL
                ELSE REPLACE(SUBSTRING(json.genderstr, 9, CHARINDEX('','', json.genderstr) - 9), ''"'', '''')
              END AS gender
            FROM (SELECT patientSer, SUBSTRING(CAST(patientInfo AS NVARCHAR(3000)), CHARINDEX(''gender'', patientInfo), 20) AS genderstr
                  FROM dbo.Patient WHERE gender IS NULL AND patientSer IS NOT NULL) json ) info
        ON info.patientSer = p.patientSer
    ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '01.02'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY
  BEGIN
    PRINT 'Database 01.02 - END'
  END
GO
/***** MR2 END *****/

/***** Database 2.0 Start *****/
DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY
  PRINT 'Database 02.00 - Begin'
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
/***** Add the 'category' 'crtUser; 'crtTime' column to EncounterCarePath table *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.EncounterCarePath', 'category') IS NULL
  BEGIN
    PRINT 'Database 02.00 - dbo.EncounterCarePath ADD category'
    ALTER TABLE dbo.EncounterCarePath ADD category NVARCHAR(64);
    EXEC('UPDATE dbo.EncounterCarePath SET category = ''PRIMARY'' WHERE category IS NULL');
  END

IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.EncounterCarePath', 'crtUser') IS NULL
  BEGIN
    PRINT 'Database 02.00 - dbo.EncounterCarePath ADD crtUser'
    ALTER TABLE dbo.EncounterCarePath ADD crtUser NVARCHAR(64);
    EXEC('UPDATE dbo.EncounterCarePath SET crtUser = ''UpgradeBy2.0'' WHERE crtUser IS NULL');
  END

IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.EncounterCarePath', 'crtTime') IS NULL
  BEGIN
    PRINT 'Database 02.00 - dbo.EncounterCarePath ADD crtTime'
    ALTER TABLE dbo.EncounterCarePath ADD crtTime DATETIME;
    EXEC('UPDATE dbo.EncounterCarePath SET crtTime = GETDATE() WHERE crtTime IS NULL');
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
/***** Add templateInfo for History DynamicForm *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.DynamicFormRecord', 'templateInfo') IS NULL
  BEGIN
    PRINT 'Database 02.00 - dbo.DynamicFormRecord ADD templateInfo'
    ALTER TABLE dbo.DynamicFormRecord ADD templateInfo NTEXT;

    /***** Step1. Fetch Template JSON Data into DynamicFormTemplateInfoTemp *****/
    CREATE TABLE dbo.DynamicFormTemplateInfoTemp(
      tid NVARCHAR(200),
      tpath  NVARCHAR(256),
      tinfo NTEXT
    );

    PRINT 'Database 02.00 - CREATE TABLE dbo.DynamicFormTemplateInfoTemp'
    DECLARE @tid NVARCHAR(200);
    DECLARE @tpath NVARCHAR(256);
    DECLARE dtCursor CURSOR FOR
      SELECT dt.templateId AS tid, (N'$(SERVER_ROOT)' + '\'+ dt.templatePath) AS tpath
      FROM dbo.DynamicFormTemplate dt
      WHERE dt.templateId IN ( SELECT DISTINCT templateId FROM dbo.DynamicFormRecord )
    OPEN dtCursor
    FETCH NEXT FROM dtCursor INTO @tid, @tpath
    WHILE @@fetch_status = 0
      BEGIN
        PRINT 'Import DynamicForm Template Json File :' + @tpath
        EXEC('INSERT INTO dbo.DynamicFormTemplateInfoTemp(tid, tpath, tinfo) SELECT ''' + @tid + ''', ''' + @tpath + ''', tmpCCIP001.* FROM OPENROWSET (BULK ''' + @tpath + ''', SINGLE_CLOB) AS tmpCCIP001');
        FETCH NEXT FROM dtCursor INTO @tid, @tpath
      END
    CLOSE dtCursor
    DEALLOCATE dtCursor

    /***** Step2. INSERT template Json to DynamicFormRecord by TemplateId *****/
    IF OBJECT_ID('dbo.DynamicFormTemplateInfoTemp', 'U') IS NOT NULL
      BEGIN
        PRINT 'Database 02.00 - UPDATE dbo.DynamicFormRecord SET templateInfo = tmp.tinfo'
        UPDATE dbo.DynamicFormRecord SET templateInfo = tmp.tinfo
        FROM DynamicFormRecord dfr
          INNER JOIN dbo.DynamicFormTemplateInfoTemp tmp
            ON tmp.tid = dfr.templateId
        WHERE dfr.templateInfo IS NULL
      END

    /***** Step3. Remove DynamicFormTemplateInfoTemp *****/
    IF OBJECT_ID('dbo.DynamicFormTemplateInfoTemp', 'U') IS NOT NULL
      BEGIN
        PRINT 'Database 02.00 - DROP TABLE dbo.DynamicFormTemplateInfoTemp'
        DROP TABLE dbo.DynamicFormTemplateInfoTemp
      END
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
/***** Move Configuration from file to SystemConfig *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'defaultTokenCacheTimeoutInMinutes')
  BEGIN
    PRINT 'Database 02.00 - INSERT INTO dbo.SystemConfig [defaultTokenCacheTimeoutInMinutes]'
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('default', 'defaultTokenCacheTimeoutInMinutes', '30', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'resourceAutoUnlockInMinutes')
  BEGIN
    PRINT 'Database 02.00 - INSERT INTO dbo.SystemConfig [resourceAutoUnlockInMinutes]'
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('default', 'resourceAutoUnlockInMinutes', '5', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'ospTokenValidationInterval')
  BEGIN
    PRINT 'Database 02.00 - INSERT INTO dbo.SystemConfig [ospTokenValidationInterval]'
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('default', 'ospTokenValidationInterval', '5', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'fhirTokenAuthEnabled')
  BEGIN
    PRINT 'Database 02.00 - INSERT INTO dbo.SystemConfig [fhirTokenAuthEnabled]'
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('default', 'fhirTokenAuthEnabled', '0', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'performanceLogging')
  BEGIN
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('default', 'performanceLogging', 'false', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'FHIRServer')
  BEGIN
    PRINT 'Database 02.00 - INSERT INTO dbo.SystemConfig [FHIRServer]'
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('FHIRServer', 'fhirConnectionTimeout', '40000', 1);
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('FHIRServer', 'fhirConnectionRequestTimeout', '40000', 2);
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('FHIRServer', 'fhirSocketTimeout', '40000', 3);
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('FHIRServer', 'fhirLanguage', 'CHS', 4);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'HttpClient')
  BEGIN
    PRINT 'Database 02.00 - INSERT INTO dbo.SystemConfig [HttpClient]'
    INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'timeout', '10000ms', 1);
    INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'connectionTimeout', '10000ms', 2);
    INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'connectionRequestTimeout', '10000ms', 3);
    INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'timeToLive', '1 hour', 4);
    INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'cookiesEnabled', 'false', 5);
    INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'maxConnections', '1024', 6);
    INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'maxConnectionsPerRoute', '1024', 7);
    INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('HttpClient', 'keepAlive', '0s', 8);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'locale')
  BEGIN
    PRINT 'Database 02.00 - INSERT INTO dbo.SystemConfig [locale]'
    INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('locale', 'language', 'zh', 1);
    INSERT INTO SystemConfig(category, name, value, orderBy) VALUES ('locale', 'country', 'CN', 2);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'physicistGroupingActivityCode')
  BEGIN
    PRINT 'Database 02.00 - INSERT INTO dbo.SystemConfig [physicistGroupingActivityCode]'
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('PhysicistGroup', 'physicistGroupingActivityCode', 'AssignTPS', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'AuditLog')
  BEGIN
    PRINT 'Database 02.00 - INSERT INTO dbo.SystemConfig [AuditLog]'
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('AuditLog', 'hostName', 'localhost', 1);
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('AuditLog', 'port', '55020', 2);
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('AuditLog', 'timeoutInMs', '300', 3);
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('AuditLog', 'logThreadCount', '1', 4);
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('AuditLog', 'logBatchSize', '100', 5);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'insuranceType')
  BEGIN
    PRINT 'Database 02.00 - Clear dbo.SystemConfig [insuranceType]'
    DELETE FROM dbo.SystemConfig WHERE name = 'insuranceType';
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'default_view' )
  BEGIN
    PRINT 'Database 02.00 - Clear dbo.SystemConfig [default_view]'
    DELETE FROM dbo.SystemConfig WHERE category = 'default_view';
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'default_tab' )
  BEGIN
    PRINT 'Database 02.00 - Clear dbo.SystemConfig [default_tab]'
    DELETE FROM dbo.SystemConfig WHERE category = 'default_tab';
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'TPS')
  BEGIN
    PRINT 'Database 02.00 - Clear dbo.SystemConfig [TPS]'
    DELETE FROM dbo.SystemConfig WHERE category = 'TPS';
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.AssignResourceField', 'U') IS NULL
  BEGIN
    CREATE TABLE dbo.AssignResourceField (
      id        INT NOT NULL IDENTITY(1,1),
      category  NVARCHAR(50),
      name      NVARCHAR(64),
      value     NVARCHAR(256),
      sortNo    INT
    );
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','lunao_saoMiaoBuWeiGroup','lunao_saoMiaoBuWeiGroup',1);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','toujing_saoMiaoBuWeiGroup','toujing_saoMiaoBuWeiGroup',2);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','hemian_saoMiaoBuWeiGroup','hemian_saoMiaoBuWeiGroup',3);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','xiongbu_saoMiaoBuWeiGroup','xiongbu_saoMiaoBuWeiGroup',4);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','shangfubu_saoMiaoBuWeiGroup','shangfubu_saoMiaoBuWeiGroup',5);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','xiafubu_saoMiaoBuWeiGroup','xiafubu_saoMiaoBuWeiGroup',6);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','penqiang_saoMiaoBuWeiGroup','penqiang_saoMiaoBuWeiGroup',7);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','biyan_saoMiaoBuWeiGroup','biyan_saoMiaoBuWeiGroup',8);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','jizhu_saoMiaoBuWeiGroup','jizhu_saoMiaoBuWeiGroup',9);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','c_saoMiaoBuWeiGroup','c_saoMiaoBuWeiGroup',10);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','t_saoMiaoBuWeiGroup','t_saoMiaoBuWeiGroup',12);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','l_saoMiaoBuWeiGroup','l_saoMiaoBuWeiGroup',13);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','s_saoMiaoBuWeiGroup','s_saoMiaoBuWeiGroup',14);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','siZhi_saoMiaoBuWeiGroup','siZhi_saoMiaoBuWeiGroup',14);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','shangZhi_saoMiaoBuWeiGroup','shangZhi_saoMiaoBuWeiGroup',15);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','xiaZhi_saoMiaoBuWeiGroup','xiaZhi_saoMiaoBuWeiGroup',16);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','daMianMoGuDing_tiWeiGuDingFangShiGroup','daMianMoGuDing_tiWeiGuDingFangShiGroup',17);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup',18);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','tiMoGuDing_tiWeiGuDingFangShiGroup','tiMoGuDing_tiWeiGuDingFangShiGroup',19);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','fuYaDaiGuDing_tiWeiGuDingFangShiGroup','fuYaDaiGuDing_tiWeiGuDingFangShiGroup',20);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup',21);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','fuPengGuDingQi_tiWeiGuDingFangShiGroup','fuPengGuDingQi_tiWeiGuDingFangShiGroup',22);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup',23);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','jianYiZhiLiaoSheBei','jianYiZhiLiaoSheBei',24);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignDevice','jianYiJiHuaXiTong','jianYiJiHuaXiTong',25);

    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','lunao_saoMiaoBuWeiGroup','lunao_saoMiaoBuWeiGroup',1);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','toujing_saoMiaoBuWeiGroup','toujing_saoMiaoBuWeiGroup',2);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','hemian_saoMiaoBuWeiGroup','hemian_saoMiaoBuWeiGroup',3);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','xiongbu_saoMiaoBuWeiGroup','xiongbu_saoMiaoBuWeiGroup',4);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','shangfubu_saoMiaoBuWeiGroup','shangfubu_saoMiaoBuWeiGroup',5);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','xiafubu_saoMiaoBuWeiGroup','xiafubu_saoMiaoBuWeiGroup',6);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','penqiang_saoMiaoBuWeiGroup','penqiang_saoMiaoBuWeiGroup',7);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','biyan_saoMiaoBuWeiGroup','biyan_saoMiaoBuWeiGroup',8);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','jizhu_saoMiaoBuWeiGroup','jizhu_saoMiaoBuWeiGroup',9);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','c_saoMiaoBuWeiGroup','c_saoMiaoBuWeiGroup',10);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','t_saoMiaoBuWeiGroup','t_saoMiaoBuWeiGroup',12);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','l_saoMiaoBuWeiGroup','l_saoMiaoBuWeiGroup',13);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','s_saoMiaoBuWeiGroup','s_saoMiaoBuWeiGroup',14);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','siZhi_saoMiaoBuWeiGroup','siZhi_saoMiaoBuWeiGroup',14);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','shangZhi_saoMiaoBuWeiGroup','shangZhi_saoMiaoBuWeiGroup',15);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','xiaZhi_saoMiaoBuWeiGroup','xiaZhi_saoMiaoBuWeiGroup',16);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','daMianMoGuDing_tiWeiGuDingFangShiGroup','daMianMoGuDing_tiWeiGuDingFangShiGroup',17);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup',18);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','tiMoGuDing_tiWeiGuDingFangShiGroup','tiMoGuDing_tiWeiGuDingFangShiGroup',19);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','fuYaDaiGuDing_tiWeiGuDingFangShiGroup','fuYaDaiGuDing_tiWeiGuDingFangShiGroup',20);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup',21);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','fuPengGuDingQi_tiWeiGuDingFangShiGroup','fuPengGuDingQi_tiWeiGuDingFangShiGroup',22);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup',23);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','jianYiZhiLiaoSheBei','jianYiZhiLiaoSheBei',24);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','jianYiJiHuaXiTong','jianYiJiHuaXiTong',25);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('AssignTPS','yiXuanZhiLiaoSheBei','yiXuanZhiLiaoSheBei',26);

    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','meigong_canKaoDianWeiZhiGroup','眉弓',1);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiae_canKaoDianWeiZhiGroup','下颚',2);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiongbu_canKaoDianWeiZhiGroup','胸部',3);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','penqiang_canKaoDianWeiZhiGroup','盆腔',4);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','qita_canKaoDianWeiZhiGroup','其他',5);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','lunao_saoMiaoBuWeiGroup','颅脑',6);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','toujing_saoMiaoBuWeiGroup','头颈',7);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','hemian_saoMiaoBuWeiGroup','颌面',8);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiongbu_saoMiaoBuWeiGroup','胸部',9);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','shangfubu_saoMiaoBuWeiGroup','上腹部',10);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiafubu_saoMiaoBuWeiGroup','下腹部',11);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','penqiang_saoMiaoBuWeiGroup','盆腔',12);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','biyan_saoMiaoBuWeiGroup','鼻咽',13);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','jizhu_saoMiaoBuWeiGroup','脊柱',14);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','c_saoMiaoBuWeiGroup','C;',15);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','t_saoMiaoBuWeiGroup','T;',16);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','l_saoMiaoBuWeiGroup','L;',17);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','s_saoMiaoBuWeiGroup','S;',18);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','siZhi_saoMiaoBuWeiGroup','四肢',19);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','shangZhi_saoMiaoBuWeiGroup','上肢（左、右）',20);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiaZhi_saoMiaoBuWeiGroup','下肢（左、右）',21);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','changGuiCTSIM_dingWeiFangShiGroup','常规CTSIM',22);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','changGuiZengQiangCTSIM_dingWeiFangShiGroup','常规增强CTSIM',23);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','menKongCTSIM_dingWeiFangShiGroup','门控CTSIM',24);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','4DCTSIM_dingWeiFangShiGroup','4D CTSIM',25);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','putongdingwei_dingWeiFangShiGroup','普通定位',26);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','daMianMoGuDing_tiWeiGuDingFangShiGroup','大面膜固定',27);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','xiaoMianMoGuDing_tiWeiGuDingFangShiGroup','小面膜固定',28);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','tiMoGuDing_tiWeiGuDingFangShiGroup','体膜固定',29);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','fuYaDaiGuDing_tiWeiGuDingFangShiGroup','负压袋固定',30);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','ruXianTuoJiaGuDing_tiWeiGuDingFangShiGroup','乳腺托架固定',31);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','fuPengGuDingQi_tiWeiGuDingFangShiGroup','腹盆固定器',32);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','SBRTTiJiaGuDingQi_tiWeiGuDingFangShiGroup','SBRT体架固定器',33);
    INSERT INTO dbo.AssignResourceField (category, name, value, sortNo) VALUES ('DynamicFormFieldValue','jianYiZhiLiaoSheBei','建议治疗设备',36);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
/***** Group role in Aria *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'GroupRole')
  BEGIN
    PRINT 'Database 02.00 - INSERT INTO dbo.SystemConfig [GroupRole]'
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('GroupRole', 'GroupRoleOncologist', 'Oncologist', 1);
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('GroupRole', 'GroupRoleNurse', 'Nurse', 1);
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('GroupRole', 'GroupRolePhysicist','Physicist', 1);
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('GroupRole', 'GroupRoleTherapist','Therapist', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
/*****  Group prefix *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE category = 'GroupPrefix')
  BEGIN
    PRINT 'Database 02.00 - INSERT INTO dbo.SystemConfig [GroupPrefix]'
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('GroupPrefix', 'GroupOncologistPrefix', 'Oncologist', 1);
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('GroupPrefix', 'GroupNursePrefix', 'Nurse', 1);
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('GroupPrefix', 'GroupPhysicistPrefix', 'Physicist', 1);
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('GroupPrefix', 'GroupTechnicianPrefix', 'Technician', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
/***** PhysicistGroup physicistGroupingActivityCode *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.SystemConfig WHERE name = 'physicistGroupingActivityCode')
  BEGIN
    PRINT 'Database 02.00 - INSERT INTO dbo.SystemConfig [physicistGroupingActivityCode]'
    INSERT INTO dbo.SystemConfig(category, name, value, orderBy) VALUES ('PhysicistGroup', 'physicistGroupingActivityCode', 'AssignTPS', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
/***** Clear up Dynamic Form Template Files *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND OBJECT_ID('dbo.DynamicFormTemplate', 'U') IS NOT NULL
  BEGIN
    PRINT 'Database 02.00 - DynamicFormTemplate:DoFirstTreatment -> DoTreatment '
    UPDATE dbo.DynamicFormTemplate SET templatePath = 'config\template\DoTreatment.json' WHERE templateId = 'DoTreatment';
  END

IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE templateId = 'PlaceMRIOrder')
  BEGIN
    PRINT 'Database 02.00 - DynamicFormTemplate: Add template PlaceMRIOrder'
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlaceMRIOrder', 'MRI定位申请单', 'config\template\PlaceMRIOrder.json');
  END
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE templateId = 'DoMRIRecord')
  BEGIN
    PRINT 'Database 02.00 - DynamicFormTemplate: Add template  DoMRIRecord'
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('DoMRIRecord', 'MRI记录单', 'config\template\DoMRIRecord.json');
  END
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.DynamicFormTemplate WHERE templateId = 'PlaceImmobilizationAndCTOrderDefaultValue')
  BEGIN
    PRINT 'Database 02.00 - DynamicFormTemplate: Add template  PlaceImmobilizationAndCTOrderDefaultValue'
    INSERT INTO dbo.DynamicFormTemplate(templateId, templateName, templatePath) VALUES ('PlaceImmobilizationAndCTOrderDefaultValue', '制模定位申请单（默认值）', 'config\template\PlaceImmobilizationAndCTOrderDefaultValue.json');
  END
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY
  BEGIN
    PRINT 'Database 02.00 - DynamicFormTemplate: Rename PlaceImmobilizationAndCTOrder to 制模CT定位申请单 '
    UPDATE  dbo.DynamicFormTemplate SET templateName='制模CT定位申请单' WHERE templateId = 'PlaceImmobilizationAndCTOrder';
    PRINT 'Database 02.00 - DynamicFormTemplate: Rename PlanningOrder  to 计划申请单 '
    UPDATE  dbo.DynamicFormTemplate SET templateName='计划申请单' WHERE templateId = 'PlanningOrder';
    PRINT 'Database 02.00 - DynamicFormTemplate: Rename PrintCTAndImmobilizationOrder  to 制模定位申请单（打印） '
    UPDATE  dbo.DynamicFormTemplate SET templateName='制模定位申请单（打印）' WHERE templateId = 'PrintCTAndImmobilizationOrder';
    PRINT 'Database 02.00 - DynamicFormTemplate: Rename PrintTreatmentRecords  to 放射治疗记录单（打印）'
    UPDATE  dbo.DynamicFormTemplate SET templateName='放射治疗记录单（打印）' WHERE templateId = 'PrintTreatmentRecords';
  END

IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND COL_LENGTH('dbo.DynamicFormTemplate', 'category') IS NULL
  BEGIN
    PRINT 'Database 02.00 - DynamicFormTemplate ADD category'
    ALTER TABLE dbo.DynamicFormTemplate ADD category NVARCHAR(50);
    EXEC('UPDATE dbo.DynamicFormTemplate SET category = ''CCIP'' ')
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
/***** Clear up TPS with 4 kind *****/
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND NOT EXISTS (SELECT * FROM dbo.AssignResourceConfig WHERE resourceCode = 'pinnacle')
  BEGIN
    PRINT 'Database 02.00 - INSERT INTO dbo.AssignResourceConfig [pinnacle]'
    INSERT INTO dbo.AssignResourceConfig(hospital, resourceId, resourceCode, resourceName, color, activityCode, orderNo) VALUES ('default', '1005', 'pinnacle', 'Pinnacle', '#FA8BF0', 'AssignTPS', 1);
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND EXISTS (SELECT * FROM dbo.AssignResourceConfig WHERE resourceCode = 'monaq')
  BEGIN
    PRINT 'Database 02.00 - UPDATE dbo.AssignResourceConfig [monaq] -> [monaco]'
    UPDATE dbo.AssignResourceConfig SET resourceCode = 'monaco', resourceName = 'Monaco' WHERE resourceCode = 'monaq';
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY AND EXISTS (SELECT * FROM dbo.AssignResource WHERE resourceId = 'monaq')
  BEGIN
    PRINT 'Database 02.00 - UPDATE dbo.AssignResource [monaq] -> [monaco]'
    UPDATE dbo.AssignResource SET resourceId = 'monaco' WHERE resourceId = 'monaq';
  END
GO

DECLARE @VERSION_HISTORY NVARCHAR(50);
DECLARE @VERSION_SECTION NVARCHAR(50);
SELECT @VERSION_HISTORY = (SELECT TOP 1 EndingRelease AS VERSION FROM [dbo].[DBHistory] ORDER BY [HstryDateTime] DESC)
SET @VERSION_SECTION = '02.00'
IF @VERSION_HISTORY IS NOT NULL AND @VERSION_SECTION > @VERSION_HISTORY
  PRINT 'Database 02.00 - End'
GO
/***** Database 2.0 End *****/

/** DB History
  INSERT INTO dbo.DBHistory VALUES((select case When (select top 1 [DBHistorySer]  from dbo.DBHistory)  IS NULL THEN 'New Installation'else 'Upgrade' End),(select case When (select top 1 [EndingRelease]  from dbo.DBHistory order by [HstryDateTime] desc)  IS NULL THEN '01.01.0226.0'else (select top 1 [EndingRelease]  from dbo.DBHistory order by [HstryDateTime] desc) End),('01.01.0226.0'),('ARIA CCIP 1.0 MR1 '+'01.01.0226.0'),(''),('@HstryUserName'), getdate())
  INSERT INTO dbo.DBHistory VALUES((select case When (select top 1 [DBHistorySer]  from dbo.DBHistory)  IS NULL THEN 'New Installation'else 'Upgrade' End),(select case When (select top 1 [EndingRelease]  from dbo.DBHistory order by [HstryDateTime] desc)  IS NULL THEN '01.02.0231.0'else (select top 1 [EndingRelease]  from dbo.DBHistory order by [HstryDateTime] desc) End),('01.02.0231.0'),('ARIA CCIP 1.0 MR2 '+'01.02.0231.0'),(''),('@HstryUserName'), getdate())
*/
INSERT INTO dbo.DBHistory VALUES((select case When (select top 1 [DBHistorySer]  from dbo.DBHistory)  IS NULL THEN 'New Installation'else 'Upgrade' End),(select case When (select top 1 [EndingRelease]  from dbo.DBHistory order by [HstryDateTime] desc)  IS NULL THEN '@EndingRelease'else (select top 1 [EndingRelease]  from dbo.DBHistory order by [HstryDateTime] desc) End),('@EndingRelease'),('ARIA CCIP '+'@EndingRelease'),(''),('@HstryUserName'), getdate())
PRINT 'SQL Script Execute Successfully For @EndingRelease.'
GO
