package com.devgong.nettyserver.protocol.Report;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ReportModifyData {


    public int convertData(char value) {

        String convertedHex = Integer.toHexString(value);

        return Integer.parseInt(convertedHex, 16);
    }


}
