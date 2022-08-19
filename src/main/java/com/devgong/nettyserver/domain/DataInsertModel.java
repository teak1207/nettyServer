package com.devgong.nettyserver.domain;


import lombok.*;

import javax.persistence.*;

@Data
public class DataInsertModel {

    private int cid;
    private String endRecordTime;
    private String time1;
    private String time2;
    private String time3;
    private String fmFrequency;
    private String firmwareVersion;
    private String batteryVtg;
    private String RSSI;
    private String deviceStatus;
    private String SamplingTime;
    private String px;
    private String py;
    private String modemNumber;
    private String sid;
    private String period;
    private String serverUrl;
    private String serverPort;
    private String dbUrl;
    private String dbPort;
    private String sleep;
    private String active;
    private String fReset;
    private String reset;
    private String SampleRate;
    private String radioTime;


}
