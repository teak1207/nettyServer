package com.devgong.nettyserver.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class PreInstallResponseModel {

    private char[] recordTime1;
    private char[] recordTime2;
    private char[] recordTime3;
    private char[] fmFrequency;
    private char[] sid;
    private char[] pname;
    private char[] px;
    private char[] py;
    private char[] serialNumber;

    private char[] period;
    private char[] samplingTime;
    private char[] samplerate;

    private char[] serverUrl;
    private char[] serverPort;
    private char[] dbUrl;
    private char[] dbPort;

    private char[] radioTime;
    private char[] baudrate;


}
