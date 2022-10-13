package com.devgong.nettyserver.protocol.Report;


import com.devgong.nettyserver.protocol.Serializable;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@Value
public class ReportRequest implements Serializable<ReportRequest> {


    String endRecordTime;
    String recordTime1;
    String recordTime2;
    String recordTime3;
    String fmRadio;
    String firmWareVersion;
    String batteryValue;
    String rssi;
    String deviceStatus;
    String samplingTime;
    String px;
    String py;
    String pname;
    String sid;
    String period;
    String serverUrl;
    String serverPort;
    String dbUrl;
    String dbPort;
    String sleep;
    String active;
    String fReset;
    String reset;
    String samplerate;
    String radioTime;
    String cregCount;
    String sleepCount;


    public ReportRequest(byte[] payload) {
        if (payload == null || payload.length != 177) {
            throw new IllegalArgumentException("Setting Request payload error!");
        }
        endRecordTime = new String(Arrays.copyOfRange(payload, 0, 13));
        recordTime1 = new String(Arrays.copyOfRange(payload, 13, 17));
        recordTime2 = new String(Arrays.copyOfRange(payload, 17, 21));
        recordTime3 = new String(Arrays.copyOfRange(payload, 21, 25));
        fmRadio = new String(Arrays.copyOfRange(payload, 25, 29));
        firmWareVersion = new String(Arrays.copyOfRange(payload, 29, 35));
        batteryValue = new String(Arrays.copyOfRange(payload, 35, 41));
        rssi = new String(Arrays.copyOfRange(payload, 41, 42));
        deviceStatus = new String(Arrays.copyOfRange(payload, 42, 44));
        samplingTime = new String(Arrays.copyOfRange(payload, 44, 45));
        px = new String(Arrays.copyOfRange(payload, 45, 55));
        py = new String(Arrays.copyOfRange(payload, 55, 65));
        pname = new String(Arrays.copyOfRange(payload, 65, 81));
        sid = new String(Arrays.copyOfRange(payload, 81, 97));
        period = new String(Arrays.copyOfRange(payload, 97, 98));
        serverUrl = new String(Arrays.copyOfRange(payload, 98, 130));
        serverPort = new String(Arrays.copyOfRange(payload, 130, 134));
        dbUrl = new String(Arrays.copyOfRange(payload, 134, 166));
        dbPort = new String(Arrays.copyOfRange(payload, 166, 170));
        sleep = new String(Arrays.copyOfRange(payload, 170, 171));
        active = new String(Arrays.copyOfRange(payload, 171, 172));
        fReset = new String(Arrays.copyOfRange(payload, 172, 173));
        reset = new String(Arrays.copyOfRange(payload, 173, 174));
        samplerate = new String(Arrays.copyOfRange(payload, 174, 175));
        radioTime = new String(Arrays.copyOfRange(payload, 175, 176));
        cregCount = new String(Arrays.copyOfRange(payload, 176, 177));
        sleepCount = new String(Arrays.copyOfRange(payload, 177, 178));


    }

    @Override
    public byte[] serialize() {

        byte[] serialized = new byte[178];

        byte[] endRecordTimeBytes = Arrays.copyOfRange(endRecordTime.getBytes(), 0, 13);
        byte[] recordTime1Bytes = Arrays.copyOfRange(recordTime1.getBytes(), 0, 4);
        byte[] recordTime2Bytes = Arrays.copyOfRange(recordTime2.getBytes(), 0, 4);
        byte[] recordTime3Bytes = Arrays.copyOfRange(recordTime3.getBytes(), 0, 4);
        byte[] fmRadioBytes = Arrays.copyOfRange(fmRadio.getBytes(), 0, 4);
        byte[] firmWareVersionBytes = Arrays.copyOfRange(firmWareVersion.getBytes(), 0, 6);
        byte[] batteryValueBytes = Arrays.copyOfRange(batteryValue.getBytes(), 0, 6);
        byte[] rssiBytes = Arrays.copyOfRange(rssi.getBytes(), 0, 1);
        byte[] deviceStatusBytes = Arrays.copyOfRange(deviceStatus.getBytes(), 0, 2);
        byte[] samplingTimeBytes = Arrays.copyOfRange(samplingTime.getBytes(), 0, 1);
        byte[] pxBytes = Arrays.copyOfRange(px.getBytes(), 0, 10);
        byte[] pyBytes = Arrays.copyOfRange(py.getBytes(), 0, 10);
        byte[] pNameBytes = Arrays.copyOfRange(pname.getBytes(), 0, 16);
        byte[] sidBytes = Arrays.copyOfRange(sid.getBytes(), 0, 16);
        byte[] periodBytes = Arrays.copyOfRange(period.getBytes(), 0, 1);
        byte[] serverUrlBytes = Arrays.copyOfRange(serverUrl.getBytes(), 0, 32);
        byte[] serverPortBytes = Arrays.copyOfRange(serverPort.getBytes(), 0, 4);
        byte[] dbUrlBytes = Arrays.copyOfRange(dbUrl.getBytes(), 0, 32);
        byte[] dbPortBytes = Arrays.copyOfRange(dbPort.getBytes(), 0, 4);
        byte[] sleepBytes = Arrays.copyOfRange(sleep.getBytes(), 0, 1);
        byte[] activeBytes = Arrays.copyOfRange(active.getBytes(), 0, 1);
        byte[] fResetBytes = Arrays.copyOfRange(fReset.getBytes(), 0, 1);
        byte[] resetBytes = Arrays.copyOfRange(reset.getBytes(), 0, 1);
        byte[] sampleRateBytes = Arrays.copyOfRange(samplerate.getBytes(), 0, 1);
        byte[] radioTimeBytes = Arrays.copyOfRange(radioTime.getBytes(), 0, 1);
        byte[] cregCountBytes = Arrays.copyOfRange(cregCount.getBytes(), 0, 1);
        byte[] sleepCountBytes = Arrays.copyOfRange(sleepCount.getBytes(), 0, 1);

        System.arraycopy(endRecordTimeBytes, 0, serialized, 0, 13);
        System.arraycopy(recordTime1Bytes, 0, serialized, 13, 4);
        System.arraycopy(recordTime2Bytes, 0, serialized, 17, 4);
        System.arraycopy(recordTime3Bytes, 0, serialized, 21, 4);
        System.arraycopy(fmRadioBytes, 0, serialized, 25, 4);
        System.arraycopy(firmWareVersionBytes, 0, serialized, 29, 6);
        System.arraycopy(batteryValueBytes, 0, serialized, 35, 6);
        System.arraycopy(rssiBytes, 0, serialized, 41, 1);
        System.arraycopy(deviceStatusBytes, 0, serialized, 42, 2);
        System.arraycopy(samplingTimeBytes, 0, serialized, 44, 1);
        System.arraycopy(pxBytes, 0, serialized, 45, 10);
        System.arraycopy(pyBytes, 0, serialized, 55, 10);
        System.arraycopy(pNameBytes, 0, serialized, 65, 16);
        System.arraycopy(sidBytes, 0, serialized, 81, 16);
        System.arraycopy(periodBytes, 0, serialized, 97, 1);
        System.arraycopy(serverUrlBytes, 0, serialized, 98, 32);
        System.arraycopy(serverPortBytes, 0, serialized, 130, 4);
        System.arraycopy(dbUrlBytes, 0, serialized, 134, 32);
        System.arraycopy(dbPortBytes, 0, serialized, 166, 4);
        System.arraycopy(sleepBytes, 0, serialized, 170, 1);
        System.arraycopy(activeBytes, 0, serialized, 171, 1);
        System.arraycopy(fResetBytes, 0, serialized, 172, 1);
        System.arraycopy(resetBytes, 0, serialized, 173, 1);
        System.arraycopy(sampleRateBytes, 0, serialized, 174, 1);
        System.arraycopy(radioTimeBytes, 0, serialized, 175, 1);
        System.arraycopy(cregCountBytes, 0, serialized, 176, 1);
        System.arraycopy(sleepCountBytes, 0, serialized, 177, 1);

        log.info("serialized check : {}",serialized);

        return serialized;
    }

    @Override
    public ReportRequest deserialize(byte[] byteArray) {
        return new ReportRequest(byteArray);
    }
}
