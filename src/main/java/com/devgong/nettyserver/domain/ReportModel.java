package com.devgong.nettyserver.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;

@Getter
@Setter
@ToString
@Table(name = "factory_report")
public class ReportModel {

    private char flag;
    private String serialNumber;
    private String dateTime;
    private String paraLen;
    private String debugMsg;

    private String RecordingTime1;
    private String RecordingTime2;
    private String RecordingTime3;
    private String firmWareVersion;
    private String batteryVtg;
    private String RSSI;
    private String samplingTime;
    private String samplingRate;
    private String modemNumber;
    private String Project;
    private String Sid;
    private String Period;
    private String serverUrl;
    private String serverPort;
    private String checkSum;


}
