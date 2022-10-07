package com.devgong.nettyserver.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SettingResponseModel {

    private String time1;
    private String time2;
    private String time3;
    private String fmRadio;
    private String sid;
    private String pname;
    private String sleep;
    private String reset;
    private String period;
    private String SamplingTime;
    private String fReset;
    private String px;
    private String py;
    private String active;
    private String SampleRate;
    private String serverUrl;
    private String serverPort;
    private String dbUrl;
    private String dbPort;
    private String radioTime;


}
