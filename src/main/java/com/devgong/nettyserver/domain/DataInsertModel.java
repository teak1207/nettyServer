package com.devgong.nettyserver.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@ToString

@NoArgsConstructor
@Table(name = "factory_report")
public class DataInsertModel {

    @Id
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
