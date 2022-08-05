-- ================================
-- CREATE
-- auto-generated definition
create table factory_leak_project
(
    cid           int auto_increment
        primary key,
    factory_pname varchar(32) null,
    data_URL      varchar(32) null,
    data_PORT     varchar(8)  null,
    db_URL        varchar(32) null,
    db_PORT       varchar(8)  null
);


-- auto-generated definition
create table sensor_list_all
(
    CID      int auto_increment
        primary key,
    REGDATE  varchar(30) null,
    SSN      varchar(32) null,
    ASID     varchar(32) null,
    APROJECT varchar(32) null,
    MPHONE   varchar(24) null,
    FRESET   varchar(1)  null
)
    auto_increment = 5;



-- auto-generated definition
create table leak_project
(
    CID         int auto_increment
        primary key,
    SID         varchar(32) null,
    PNAME       varchar(64) null,
    DATA_SERVER varchar(32) null,
    DATA_PORT   varchar(8)  null,
    DB_URL      varchar(32) null,
    DB_PORT     varchar(8)  null
)
    auto_increment = 3;

-- auto-generated definition
create table leakset
(
    cid            int auto_increment
        primary key,
    recordingtime1 varchar(8) null,
    recordingtime2 varchar(8) null,
    recordingtime3 varchar(8) null,
    period         varchar (8) null,
    sample         varchar(2) null,
    samplerate     varchar(8) null,
    sleep          varchar(1) null,
    active         varchar(1) null
);

-- auto-generated definition
create table factory_report
(
    cid           int auto_increment
        primary key,
    SERIALNUMBER  varchar(64) null,
    REPORTTIME    varchar(20) null,
    DEBUG         varchar(20) null,
    TIME1         varchar(8)  null,
    TIME2         varchar(8)  null,
    TIME3         varchar(8)  null,
    FM_RADIO      varchar(8)  null,
    FVER          varchar(8)  null,
    BATTERY       varchar(8)  null,
    RSSI          varchar(8)  null,
    DEVICE_STATUS varchar(2)  null,
    SAMPLINGTIME  varchar(20) null,
    PX            varchar(16) null,
    PY            varchar(16) null,
    MODEMNUMBER   varchar(50) null,
    SID           varchar(64) null,
    PERIOD varchar (2) null,
    SERVER_URL    varchar(32) null,
    SERVER_PORT   varchar(8)  null,
    DB_URL        varchar(32) null,
    DB_PORT       varchar(8)  null,
    BAUDRATE      varchar(1)  null,
    PCB_VER       varchar(1)  null
);

-- auto-generated definition
create table factory_sensor_list
(
    cid          int auto_increment
        primary key,
    sid          varchar(64) null,
    pname        varchar(64) null,
    sn           varchar(64) null,
    factorypname varchar(32) null
);


-- auto-generated definition
create table leakmaster_init_infos
(
    ID       int auto_increment
        primary key,
    NAME     varchar(32) not null,
    SID      varchar(32) not null,
    SN       varchar(32) not null,
    SIZE     int         not null,
    PHONENUM varchar(32) not null,
    REGDATE  varchar(32) not null
)
    auto_increment = 3;


-- auto-generated definition
create table leakset
(
    cid          int auto_increment
        primary key,
    time1        varchar(8)  null,
    time2        varchar(8)  null,
    time3        varchar(8)  null,
    fm_frequency varchar(8)  null,
    sid          varchar(32) null,
    pname        varchar(32) null,
    sleep        varchar(1)  null,
    reset        varchar(1)  null,
    period       varchar (8) null,
    sampletime   varchar(2)  null,
    active       varchar(1)  null,
    samplerate   varchar(8)  null,
    fmtime       varchar(8)  null
)
    auto_increment = 2;

-- auto-generated definition
create table leakset_bysensor
(
    CID          int auto_increment
        primary key,
    SN           varchar(32) null,
    TIMEOPT1     varchar(8)  null,
    TIMEOPT2     varchar(8)  null,
    TIMEOPT3     varchar(8)  null,
    FM_FREQUENCY varchar(8)  null,
    PERIOD varchar (8) null,
    SAMPLE       varchar(2)  null,
    SAMPLERATE   varchar(8)  null,
    RADIO_TIME   varchar(8)  null,
    BAUDRATE     varchar(8)  null
)
    auto_increment = 3;



-- auto-generated definition
create table sensor_list
(
    cid   int auto_increment
        primary key,
    sn    varchar(64) null,
    px    varchar(16) null,
    py    varchar(16) null,
    pname varchar(64) null,
    sid   varchar(64) null
)
    auto_increment = 3;





-- ================================
-- INSERT

INSERT INTO leak_project
VALUES (1, 'thingsware.co.kr', '8998');

INSERT INTO leak_project
VALUES (1, 'SWFLB-20200312-0102-0004', '2020-03-17 13:12:01', '', '218.155.80.145', 'scleak', '0200', '0300', '0400',
        '099', '2', '2', '0', '20200310_20', '-1', '5',0);

INSERT INTO sensor_list_all
VALUES (1, now(), 'SWSLB-20220530-0000-0001', 'producttest', 'tesk_chk', '8212-3266-1739');
INSERT INTO sensor_list_all
VALUES (2, now(), 'SWFLB-20210408-0106-0543', 'producttest', 'test_hkchoi', '862785043595621');

INSERT INTO LEAKSET
VALUES (1, '0200', '0300', '0400', '1', '3', '4', '1', '1');

-- ================================
-- SELECT
SELECT *
FROM leak_project;
SELECT *
FROM LEAKSET_BYSENSOR;
-- ================================
-- DROP
DROP TABLE LEAKSET_BYSENSOR;



