package com.devgong.nettyserver.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SettingInitModel {

    private int cid;

    private String RecordingTime1;
    private String RecordingTime2;
    private String RecordingTime3;
    private String period;
    private String sample;
    private String samplerate;
    private String sleep;
    private String active;
}
