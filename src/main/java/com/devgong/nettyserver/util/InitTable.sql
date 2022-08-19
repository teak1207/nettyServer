-- ================================
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

create table factory_sensor_list
(
    cid          int auto_increment
        primary key,
    sid          varchar(64) null,
    pname        varchar(64) null,
    sn           varchar(64) null,
    factorypname varchar(32) null
);
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
);

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
);

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
    period varchar (8) null,
    sampletime   varchar(2)  null,
    active       varchar(1)  null,
    samplerate   varchar(8)  null,
    fmtime       varchar(8)  null
);

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
);

create table preinstall
(
    cid        int auto_increment
        primary key,
    MODEMNUM   varchar(30) null,
    FLAG       varchar(5)  null,
    SERIALNUM  varchar(30) null,
    SIGNALTIME varchar(20) null,
    PARALEN    varchar(10) null,
    DEBUGMSG   varchar(10) null,
    CHKSUM     varchar(5)  null
);

create table sensor_list
(
    cid   int auto_increment
        primary key,
    sn    varchar(64) null,
    px    varchar(16) null,
    py    varchar(16) null,
    pname varchar(64) null,
    sid   varchar(64) null
);

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
);

create table `sensor_report_goseong_kw_swflb-20220708-0760-3465`
(
    cid             int auto_increment
        primary key,
    serialNumber    varchar(64) null,
    date            datetime    null,
    id              varchar(16) null,
    ip              varchar(36) null,
    px              varchar(16) null,
    sid             varchar(64) null,
    py              varchar(16) null,
    pname           varchar(64) null,
    time1           varchar(8)  null,
    time2           varchar(8)  null,
    time3           varchar(8)  null,
    end_record_time varchar(20) null,
    fm              varchar(8)  null,
    firmwareVersion varchar(8)  null,
    rssi            varchar(8)  null,
    status          varchar(2)  null,
    sample          varchar(2)  null,
    period varchar (2) null,
    battery         varchar(8)  null,
    project         varchar(32) null,
    server_url      varchar(32) null,
    server_port     varchar(8)  null,
    db_url          varchar(32) null,
    db_port         varchar(8)  null,
    fmtime          varchar(2)  null,
    samplerate      varchar(2)  null,
    sleep           varchar(2)  null,
    active          varchar(2)  null,
    reset           varchar(2)  null,
    f_reset         varchar(2)  null
);

create table `leak_send_data_goseong_kw_swflb-20220708-0760-3465`
(
    cid           int auto_increment
        primary key,
    pname         varchar(64)  null,
    date          varchar(32)  null,
    id            varchar(16)  null,
    ip            varchar(20)  null,
    sid           varchar(64)  null,
    valid         varchar(1)   null,
    request_time  varchar(32)  null,
    fname         varchar(256) null,
    sn            varchar(32)  null,
    complete      varchar(1)   null,
    complete_time varchar(32)  null,
    fnum          varchar(8)   null,
    inference     varchar(8)   null
);





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



