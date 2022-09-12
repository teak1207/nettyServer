package com.devgong.nettyserver.protocol.preinstall;

import com.devgong.nettyserver.protocol.DeviceStatus;
import com.devgong.nettyserver.protocol.Serializable;
import lombok.Value;

import java.util.Arrays;

@Value
public class PreInstallReportRequest implements Serializable<PreInstallReportRequest> {

    String debugMessage; // 13 byte
    String recordTime1; // 4 byte
    String recordTime2;  // 4 byte
    String recordTime3; // 4 byte
    String fmRadio; // 4 byte
    String firmwareVersion; // 6 byte
    String batteryValue; // 6 byte
    int modemRssi; // 1 byte
    DeviceStatus deviceStatus; // 2 byte
    int samplingTime; // 1 byte
    String px; // 10 byte
    String py; // 10 byte
    String pname; // 16 byte
    String sid; // 16 byte
    int period; // 1 byte
    String serverUrl; // 32 byte
    String serverPort; // 4 byte
    String dbUrl; // 32 byte
    String dbPort; // 4 byte
    int radioTime; // 1 byte
    int baudrate; // 1 byte
    int baudrateNext; // 1 byte
    int pcbVersion; // 1 byte

    public PreInstallReportRequest(byte[] payload) {
        if (payload == null || payload.length != 174) {
            throw new IllegalArgumentException("PreInstallReportRequest payload error!");
        }

        debugMessage = new String(Arrays.copyOfRange(payload, 0, 13));
        recordTime1 = new String(Arrays.copyOfRange(payload, 13, 17));
        recordTime2 = new String(Arrays.copyOfRange(payload, 17, 21));
        recordTime3 = new String(Arrays.copyOfRange(payload, 21, 25));
        fmRadio = new String(Arrays.copyOfRange(payload, 25, 29));
        firmwareVersion = new String(Arrays.copyOfRange(payload, 29, 35));
        batteryValue = new String(Arrays.copyOfRange(payload, 35, 41));
        modemRssi = payload[41];
        deviceStatus = Arrays.stream(DeviceStatus.values()).filter(status -> Arrays.equals(status.getStatus(), Arrays.copyOfRange(payload, 42, 44))).findAny()
                .orElseThrow(() -> new IllegalStateException("Invalid deviceStatus error : " + new String(Arrays.copyOfRange(payload, 42, 44))));
        samplingTime = payload[44];
        px = new String(Arrays.copyOfRange(payload, 45, 55));
        py = new String(Arrays.copyOfRange(payload, 55, 65));
        pname = new String(Arrays.copyOfRange(payload, 65, 81));
        sid = new String(Arrays.copyOfRange(payload, 81, 97));
        period = payload[97];
        serverUrl = new String(Arrays.copyOfRange(payload, 98, 130));
        serverPort = new String(Arrays.copyOfRange(payload, 130, 134));
        dbUrl = new String(Arrays.copyOfRange(payload, 134, 166));
        dbPort = new String(Arrays.copyOfRange(payload, 166, 170));
        radioTime = payload[170];
        baudrate = payload[171];
        baudrateNext = payload[172];
        pcbVersion = payload[173];
    }

    @Override
    public PreInstallReportRequest deserialize(byte[] byteArray) {
        return new PreInstallReportRequest(byteArray);
    }

    @Override
    public byte[] serialize() {
        byte[] serialized = new byte[174];
        System.arraycopy(debugMessage.getBytes(), 0, serialized, 0, 13);
        System.arraycopy(recordTime1.getBytes(), 0, serialized, 13, 4);
        System.arraycopy(recordTime2.getBytes(), 0, serialized, 17, 4);
        System.arraycopy(recordTime3.getBytes(), 0, serialized, 21, 4);
        System.arraycopy(fmRadio.getBytes(), 0, serialized, 25, 4);
        System.arraycopy(firmwareVersion.getBytes(), 0, serialized, 29, 6);
        System.arraycopy(batteryValue.getBytes(), 0, serialized, 35, 6);
        serialized[41] = (byte) modemRssi;
        System.arraycopy(deviceStatus.getStatus(), 0, serialized, 42, 2);
        serialized[44] = (byte) samplingTime;
        System.arraycopy(px.getBytes(), 0, serialized, 45, 10);
        System.arraycopy(py.getBytes(), 0, serialized, 55, 10);
        System.arraycopy(pname.getBytes(), 0, serialized, 65, 16);
        System.arraycopy(sid.getBytes(), 0, serialized, 81, 16);
        serialized[97] = (byte) period;
        System.arraycopy(serverUrl.getBytes(), 0, serialized, 98, 32);
        System.arraycopy(serverPort.getBytes(), 0, serialized, 130, 4);
        System.arraycopy(dbUrl.getBytes(), 0, serialized, 134, 32);
        System.arraycopy(dbPort.getBytes(), 0, serialized, 166, 4);
        serialized[170] = (byte) radioTime;
        serialized[171] = (byte) baudrate;
        serialized[172] = (byte) baudrateNext;
        serialized[173] = (byte) pcbVersion;
        return serialized;
    }
}
