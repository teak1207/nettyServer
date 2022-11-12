package com.devgong.nettyserver.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SettingResponseModel {

    private final String time1;
    private final String time2;
    private final String time3;
    private final String fmRadio;
    private final String sid;
    private final String pname;
    private final String sleep;
    private final String reset;
    private final String period;
    private final String samplingTime;
    private final String fReset;
    private final String px;
    private final String py;
    private final String active;
    private final String sampleRate;
    private final String serverUrl;
    private final String serverPort;
    private final String dbUrl;
    private final String dbPort;
    private final String radioTime;

    @Builder
    public SettingResponseModel(String time1, String time2, String time3, String fmRadio, String sid, String pname, String sleep, String reset, String period, String samplingTime, String fReset, String px, String py, String active, String sampleRate, String serverUrl, String serverPort, String dbUrl, String dbPort, String radioTime) {
        this.time1 = time1;
        this.time2 = time2;
        this.time3 = time3;
        this.fmRadio = fmRadio;
        this.sid = sid;
        this.pname = pname;
        this.sleep = sleep;
        this.reset = reset;
        this.period = period;
        this.samplingTime = samplingTime;
        this.fReset = fReset;
        this.px = px;
        this.py = py;
        this.active = active;
        this.sampleRate = sampleRate;
        this.serverUrl = serverUrl;
        this.serverPort = serverPort;
        this.dbUrl = dbUrl;
        this.dbPort = dbPort;
        this.radioTime = radioTime;
    }
}
