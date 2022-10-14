package com.devgong.nettyserver.protocol.Report;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportModifyData {


    public Object convertData(char value) {

        String convertedHex = Integer.toHexString(value);

        return Integer.parseInt(convertedHex, 16);
    }


}
