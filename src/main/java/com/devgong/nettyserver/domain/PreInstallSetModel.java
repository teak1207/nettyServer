package com.devgong.nettyserver.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PreInstallSetModel {

    private String time1;
    private String time2;
    private String time3;
    private String SerialNumber;
    private String Period;
    private String SamplingTime;
    private String SampleRate;
    private String serverUrl;
    private String serverPort;


}
