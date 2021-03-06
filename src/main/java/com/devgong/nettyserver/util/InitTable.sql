--================================
-- CREATE
/*CREATE TABLE LEAKSET_BYSENSOR
(
    CID         INT AUTO_INCREMENT,
    SN          VARCHAR(32),
    REGDATE     VARCHAR(20),
    INSERTID    VARCHAR(16),
    IP          VARCHAR(32),
    SID         VARCHAR(32),
    ID          VARCHAR(32),
    TIMEOPT1    VARCHAR(8),
    STIMEOPT2   VARCHAR(8),
    STIMEOPT3   VARCHAR(8),
    FM          VARCHAR(8),
    SLEEP       VARCHAR(1),
    DEVICERESET VARCHAR(1),
    PERIOD      VARCHAR (8),
    PNAME       VARCHAR(32),
    TDATE       VARCHAR(20),
    SAMPLE      VARCHAR(2),
    VALID       VARCHAR(1),
    SAMPLERATE  VARCHAR(8),
    FMTIME      VARCHAR(8),
    BAUDRATE    VARCHAR(8),
    FRAMESIZE   VARCHAR(2),

    PRIMARY KEY (CID)
);*/

CREATE TABLE SENSOR_LIST_ALL
(
    CID      INT AUTO_INCREMENT,
    REGDATE  varchar(30),
    SSN      VARCHAR(32),
    ASID     VARCHAR(32),
    APROJECT VARCHAR(32),
    MPPHONE  VARCHAR(24),
    PRIMARY KEY (CID)
);

CREATE TABLE LEAK_PROJECT
(
    CID         INT AUTO_INCREMENT,
    SID         VARCHAR(32),
    PNAME         VARCHAR(64),
    DATA_SERVER VARCHAR(32),
    DATA_PORT   VARCHAR(8),
    PRIMARY KEY (CID)
);

CREATE TABLE LEAKSET_BYSENSOR
(
    CID         INT AUTO_INCREMENT,
    SN          VARCHAR(32),
    TIMEOPT1    VARCHAR(8),
    TIMEOPT2   VARCHAR(8),
    TIMEOPT3   VARCHAR(8),
    PERIOD      VARCHAR (8),
    SAMPLE      VARCHAR(2),
    SAMPLERATE  VARCHAR(8),
    PRIMARY KEY (CID)
);

create table factory_report
(
    cid          int auto_increment,
    serialNumber VARCHAR(64) null,
    REPORTTIME   DATETIME    null,
    ID           VARCHAR(16) null,
    IP           VARCHAR(36) null,
    PX           VARCHAR(16) null,
    SID          VARCHAR(64) null,
    PY           VARCHAR(16) null,
    PNAME        VARCHAR(64) null,
    TIME1        VARCHAR(8)  null,
    TIME2        VARCHAR(8)  null,
    TIME3        VARCHAR(8)  null,
    DEBUG        VARCHAR(20) null,
    FM           VARCHAR(8)  null,
    FVER         VARCHAR(8)  null,
    RSSI         VARCHAR(8)  null,
    STATUS_TEMP  VARCHAR(2)  null,
    PERIOD_TEMP  VARCHAR(2)  null,
    BATTERY      VARCHAR(8)  null,
    PROJECT      VARCHAR(32) null,
    DATA_SERVER  VARCHAR(32) null,
    DATA_PORT    VARCHAR(8)  null,
    DB_SERVER    VARCHAR(32) null,
    DB_PORT      VARCHAR(8)  null,
    REPORT_CHK   VARCHAR(16) null,
    FMTIME       VARCHAR(8)  null,
    BAUDRATE     VARCHAR(8)  null,
    PCBVERSION   VARCHAR(8)  null,
    ACKNAK       VARCHAR(4)  null,
    constraint factory_report_pk
        primary key (cid)
);

-- auto-generated definition
create table leakset
(
    cid            int auto_increment
        primary key,
    recordingtime1 varchar(8) null,
    recordingtime2 varchar(8) null,
    recordingtime3 varchar(8) null,
    period varchar (8) null,
    sample         varchar(2) null,
    samplerate     varchar(8) null,
    sleep          varchar(1) null,
    active         varchar(1) null
);





-- ================================
-- INSERT

INSERT INTO  leak_project VALUES (1,'thingsware.co.kr','8998');

INSERT INTO  leak_project VALUES (1,'SWFLB-20200312-0102-0004','2020-03-17 13:12:01','','218.155.80.145','scleak','0200','0300','0400','099','2','2','0','20200310_20','-1','5',);

INSERT INTO sensor_list_all VALUES  (1,now() ,'SWSLB-20220530-0000-0001','producttest','tesk_chk','8212-3266-1739');
INSERT INTO sensor_list_all VALUES  (2,now() ,'SWFLB-20210408-0106-0543','producttest','test_hkchoi','862785043595621');

INSERT INTO LEAKSET VALUES (1,'0200','0300','0400','1','3','4','1','1');

-- ================================
-- SELECT
SELECT * FROM leak_project;
SELECT * FROM LEAKSET_BYSENSOR;
-- ================================
-- DROP
DROP TABLE LEAKSET_BYSENSOR;



