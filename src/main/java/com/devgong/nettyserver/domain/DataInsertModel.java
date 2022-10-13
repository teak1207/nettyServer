package com.devgong.nettyserver.domain;


import lombok.*;

import javax.persistence.*;

@Data
public class DataInsertModel {

    private int cid;
    private String endRecordTime;
    private String recordTime1;
    private String recordTime2;
    private String recordTime3;
    private String fmRadio;
    private String firmwareVersion;
    private String batteryValue;
    private String modernRssi;
    private String deviceStatus;
    private String SamplingTime;
    private String px;
    private String py;
    private String pname;
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
    private String sampleRate;
    private String radioTime;
    private String cregCount;


}
