package com.devgong.nettyserver.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

    @Column(name = "SERIALNUMBER")
    private String serialNumber;

    @Column(name = "REPORTTIME")
    private String dateTime;

    @Column(name = "DEBUG")
    private String debugMsg;
    @Column(name = "TIME1")
    private String RecordingTime1;
    @Column(name = "TIME2")
    private String RecordingTime2;
    @Column(name = "TIME3")
    private String RecordingTime3;
    @Column(name = "FVER")
    private String firmWareVersion;
    @Column(name = "BATTERY")
    private String batteryVtg;
    @Column(name = "RSSI")
    private String RSSI;
    @Column(name = "SAMPLINGTIME")
    private String samplingTime;
    @Column(name = "SAMPLINGRATE")
    private String samplingRate;
    @Column(name = "MODEMNUMBER")
    private String modemNumber;
    @Column(name = "PROJECT")
    private String Project;
    @Column(name = "SID")
    private String Sid;
    @Column(name = "PERIOD")
    private String Period;
    @Column(name = "SERVER_URL")
    private String serverUrl;
    @Column(name = "SERVER_PORT")
    private String serverPort;


}
