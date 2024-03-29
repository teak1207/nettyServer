package com.devgong.nettyserver.protocol.preinstall;

import com.devgong.nettyserver.protocol.Serializable;
import lombok.ToString;
import lombok.Value;

import java.util.Arrays;

@ToString
@Value
public class PreInstallResponse implements Serializable<PreInstallResponse> {


    String recordTime1; // 4 byte
    String recordTime2;  // 4 byte
    String recordTime3; // 4 byte
    String fmRadio; // 4 byte
    String sid; // 16 byte
    String pname; // 16 byte
    String px; // 10 byte
    String py; // 10 byte
    String sn; // 24 byte
    int period; // 1 byte
    int samplingTime; // 1 byte
    int sampleRate; // 1 byte
    String serverUrl; // 32 byte
    String serverPort; // 4 byte
    String dbUrl; // 32 byte
    String dbPort; // 4 byte
    int radioTime; // 1 byte
    int baudrate; // 1 byte

    public PreInstallResponse(String recordTime1, String recordTime2, String recordTime3, String fmRadio, String sid, String pname, String px, String py, String sn, int period, int samplingTime, int sampleRate, String serverUrl, String serverPort, String dbUrl, String dbPort, int radioTime, int baudrate) {
        this.recordTime1 = recordTime1;
        this.recordTime2 = recordTime2;
        this.recordTime3 = recordTime3;
        this.fmRadio = fmRadio;
        this.sid = sid;
        this.pname = pname;
        this.px = px;
        this.py = py;
        this.sn = sn;
        this.period = period;
        this.samplingTime = samplingTime;
        this.sampleRate = sampleRate;
        this.serverUrl = serverUrl;
        this.serverPort = serverPort;
        this.dbUrl = dbUrl;
        this.dbPort = dbPort;
        this.radioTime = radioTime;
        this.baudrate = baudrate;
    }

    public PreInstallResponse(byte[] payload) {
        if (payload == null || payload.length != 169) {
            throw new IllegalArgumentException("PreInstallResponse payload error!");
        }

        recordTime1 = new String(Arrays.copyOfRange(payload, 0, 4)).trim();
        recordTime2 = new String(Arrays.copyOfRange(payload, 4, 8)).trim();
        recordTime3 = new String(Arrays.copyOfRange(payload, 8, 12)).trim();
        fmRadio = new String(Arrays.copyOfRange(payload, 12, 16)).trim();

        sid = new String(Arrays.copyOfRange(payload, 16, 32)).trim();
        pname = new String(Arrays.copyOfRange(payload, 32, 48)).trim();
        px = new String(Arrays.copyOfRange(payload, 48, 58)).trim();
        py = new String(Arrays.copyOfRange(payload, 58, 68)).trim();
        sn = new String(Arrays.copyOfRange(payload, 68, 92)).trim();
        period = payload[92];
        samplingTime = payload[93];
        sampleRate = payload[94];
        serverUrl = new String(Arrays.copyOfRange(payload, 95, 127)).trim();
        serverPort = new String(Arrays.copyOfRange(payload, 127, 131)).trim();
        dbUrl = new String(Arrays.copyOfRange(payload, 131, 163)).trim();
        dbPort = new String(Arrays.copyOfRange(payload, 163, 167)).trim();
        radioTime = payload[167];
        baudrate = payload[168];
    }

    @Override
    public PreInstallResponse deserialize(byte[] byteArray) {
        return new PreInstallResponse(byteArray);
    }


    @Override
    public byte[] serialize() {
        byte[] serialized = new byte[169];

        byte[] recordTime1Bytes = Arrays.copyOfRange(recordTime1.getBytes(), 0, 4);
        byte[] recordTime2Bytes = Arrays.copyOfRange(recordTime2.getBytes(), 0, 4);
        byte[] recordTime3Bytes = Arrays.copyOfRange(recordTime3.getBytes(), 0, 4);
        byte[] fmRadioBytes = Arrays.copyOfRange(fmRadio.getBytes(), 0, 4);
        byte[] sidBytes = Arrays.copyOfRange(sid.getBytes(), 0, 16);
        byte[] pnameBytes = Arrays.copyOfRange(pname.getBytes(), 0, 16);
        byte[] pxBytes = Arrays.copyOfRange(px.getBytes(), 0, 10);
        byte[] pyBytes = Arrays.copyOfRange(py.getBytes(), 0, 10);
        byte[] snBytes = Arrays.copyOfRange(sn.getBytes(), 0, 24);
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
        System.arraycopy(pxBytes, 0, serialized, 48, 10);
        System.arraycopy(pyBytes, 0, serialized, 58, 10);
        System.arraycopy(snBytes, 0, serialized, 68, 24);
        serialized[92] = (byte) period;
        serialized[93] = (byte) samplingTime;
        serialized[94] = (byte) sampleRate;
        System.arraycopy(serverUrlBytes, 0, serialized, 95, 32);
        System.arraycopy(serverPortBytes, 0, serialized, 127, 4);
        System.arraycopy(dbUrlBytes, 0, serialized, 131, 32);
        System.arraycopy(dbPortBytes, 0, serialized, 163, 4);
        serialized[167] = (byte) radioTime;
        serialized[168] = (byte) baudrate;
        return serialized;
    }
}
