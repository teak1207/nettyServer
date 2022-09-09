package com.devgong.nettyserver.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "factory_report")
public class PreinstallReportModel {

    @Id
    @Column(name = "cid")
    private int cid;
    @Column(name = "SN")
    private String serialNumber;
    @Column(name = "DATE")
    private LocalDateTime dateTime;
    @Column(name = "DEBUG")
    private String debugMsg;
    @Column(name = "TIME1")
    private String RecordingTime1;
    @Column(name = "TIME2")
    private String RecordingTime2;
    @Column(name = "TIME3")
    private String RecordingTime3;
    @Column(name = "FM")
    private String fmRadio;
    @Column(name = "FVER")
    private String firmWareVersion;
    @Column(name = "BATT")
    private String batteryVtg;
    @Column(name = "RSSI")
    private String RSSI;
    @Column(name = "STATUS")
    private String deviceStatus;
    @Column(name = "SAMPLE")
    private String samplingTime;
    @Column(name = "PX")
    private String px;
    @Column(name = "PY")
    private String py;
    @Column(name = "PNAME")
    private String modemNumber;
    @Column(name = "SID")
    private String Sid;
    @Column(name = "PERIOD")
    private String Period;
    @Column(name = "DATA_SERVER")
    private String serverUrl;
    @Column(name = "DATA_PORT")
    private String serverPort;
    @Column(name = "DB_SERVER")
    private String dbUrl;
    @Column(name = "DB_PORT")
    private String dbPort;
    @Column(name = "BAUDRATE")
    private String baudrate;
    @Column(name = "PCBVERSION")
    private String pcbVersion;


}
