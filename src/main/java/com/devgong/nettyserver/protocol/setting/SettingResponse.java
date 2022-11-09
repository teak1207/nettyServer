package com.devgong.nettyserver.protocol.setting;


import com.devgong.nettyserver.protocol.Serializable;
import lombok.ToString;
import lombok.Value;

import java.util.Arrays;

@ToString
@Value
public class SettingResponse implements Serializable<SettingResponse> {


    String recordTime1; // 4 byte
    String recordTime2;  // 4 byte
    String recordTime3; // 4 byte
    String fmRadio; // 4 byte
    String sid; // 16 byte
    String pname; // 16 byte
    String sleep; // 1   byte
    String reset; // 1 byte
    int period; // 1 byte
    int samplingTime; // 1 byte
    String fReset; // 1 byte
    String px; // 10 byte
    String py; // 10 byte
    String active; // 1 byte
    int sampleRate; // 1 byte
    String serverUrl; // 32 byte
    String serverPort; // 4 byte
    String dbUrl; // 32 byte
    String dbPort; // 4 byte
    int radioTime; // 1 byte


    public SettingResponse(String recordTime1, String recordTime2, String recordTime3, String fmRadio, String sid, String pname, String sleep, String reset, int period, int samplingTime, String fReset, String px, String py, String active, int sampleRate, String serverUrl, String serverPort, String dbUrl, String dbPort, int radioTime) {
        this.recordTime1 = recordTime1;
        this.recordTime2 = recordTime2;
        this.recordTime3 = recordTime3;
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

    public SettingResponse(byte[] payload) {
        if (payload == null || payload.length != 148) {
            throw new IllegalArgumentException("Setting Response payload Error");
        }

        recordTime1 = new String(Arrays.copyOfRange(payload, 0, 4)).trim();
        recordTime2 = new String(Arrays.copyOfRange(payload, 4, 8)).trim();
        recordTime3 = new String(Arrays.copyOfRange(payload, 8, 12)).trim();
        fmRadio = new String(Arrays.copyOfRange(payload, 12, 16)).trim();
        sid = new String(Arrays.copyOfRange(payload, 16, 32)).trim();
        pname = new String(Arrays.copyOfRange(payload, 32, 48)).trim();
        sleep = new String(Arrays.copyOfRange(payload, 48, 49)).trim();
        reset = new String(Arrays.copyOfRange(payload, 49, 50)).trim();
        period = payload[50];
        samplingTime = payload[51];
        fReset = new String(Arrays.copyOfRange(payload, 52, 53)).trim();
        px = new String(Arrays.copyOfRange(payload, 53, 63)).trim();
        py = new String(Arrays.copyOfRange(payload, 63, 73)).trim();
        active = new String(Arrays.copyOfRange(payload, 73, 74)).trim();
        sampleRate = payload[74];
        serverUrl = new String(Arrays.copyOfRange(payload, 75, 107)).trim();
        serverPort = new String(Arrays.copyOfRange(payload, 107, 111)).trim();
        dbUrl = new String(Arrays.copyOfRange(payload, 111, 143)).trim();
        dbPort = new String(Arrays.copyOfRange(payload, 143, 147)).trim();
        radioTime = payload[147];


    }


    @Override
    public SettingResponse deserialize(byte[] byteArray) {
        return new SettingResponse(byteArray);
    }

    @Override
    public byte[] serialize() {

        byte[] serialized = new byte[148];

        byte[] recordTime1Bytes = Arrays.copyOfRange(recordTime1.getBytes(), 0, 4);
        byte[] recordTime2Bytes = Arrays.copyOfRange(recordTime2.getBytes(), 0, 4);
        byte[] recordTime3Bytes = Arrays.copyOfRange(recordTime3.getBytes(), 0, 4);
        byte[] fmRadioBytes = Arrays.copyOfRange(fmRadio.getBytes(), 0, 4);
        byte[] sidBytes = Arrays.copyOfRange(sid.getBytes(), 0, 16);
        byte[] pnameBytes = Arrays.copyOfRange(pname.getBytes(), 0, 16);
        byte[] sleepBytes = Arrays.copyOfRange(sleep.getBytes(), 0, 1);
        byte[] resetBytes = Arrays.copyOfRange(sleep.getBytes(), 0, 1);
        byte[] fResetBytes = Arrays.copyOfRange(sleep.getBytes(), 0, 1);
        byte[] pxBytes = Arrays.copyOfRange(px.getBytes(), 0, 10);
        byte[] pyBytes = Arrays.copyOfRange(py.getBytes(), 0, 10);
        byte[] activeBytes = Arrays.copyOfRange(active.getBytes(), 0, 1);
        byte[] serverUrlBytes = Arrays.copyOfRange(serverUrl.getBytes(), 0, 32);
        byte[] serverPortBytes = Arrays.copyOfRange(serverPort.getBytes(), 0, 4);
        byte[] dbUrlBytes = Arrays.copyOfRange(dbUrl.getBytes(), 0, 32);
        byte[] dbPortBytes = Arrays.copyOfRange(dbPort.getBytes(), 0, 4);

        System.arraycopy(recordTime1Bytes, 0, serialized, 0, 4);
        System.arraycopy(recordTime2Bytes, 0, serialized, 4, 4);
        System.arraycopy(recordTime3Bytes, 0, serialized, 8, 4);
        System.arraycopy(fmRadioBytes, 0, serialized, 12, 4);
        System.arraycopy(sidBytes, 0, serialized, 16, 16);
        System.arraycopy(pnameBytes, 0, serialized, 32, 16);
        System.arraycopy(sleepBytes, 0, serialized, 48, 1);
        System.arraycopy(resetBytes, 0, serialized, 49, 1);
        serialized[50] = (byte) period;
        serialized[51] = (byte) samplingTime;
        System.arraycopy(fResetBytes, 0, serialized, 52, 1);
        System.arraycopy(pxBytes, 0, serialized, 53, 10);
        System.arraycopy(pyBytes, 0, serialized, 63, 10);
        System.arraycopy(activeBytes, 0, serialized, 73, 1);
        serialized[74] = (byte) sampleRate;
        System.arraycopy(serverUrlBytes, 0, serialized, 75, 32);
        System.arraycopy(serverPortBytes, 0, serialized, 107, 4);
        System.arraycopy(dbUrlBytes, 0, serialized, 111, 32);
        System.arraycopy(dbPortBytes, 0, serialized, 143, 4);
        serialized[147] = (byte) radioTime;
        return serialized;
    }
}
