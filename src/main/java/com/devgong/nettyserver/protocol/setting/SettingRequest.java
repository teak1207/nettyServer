package com.devgong.nettyserver.protocol.setting;

import com.devgong.nettyserver.protocol.Serializable;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@Value
public class SettingRequest implements Serializable<SettingRequest> {

    String sid;
    String pname;


    public SettingRequest(byte[] payload) {

        if (payload == null || payload.length != 32) {
            throw new IllegalArgumentException("Setting Request payload error!");
        }

        sid = new String(Arrays.copyOfRange(payload, 0, 16));
        pname = new String(Arrays.copyOfRange(payload, 16, 32));

    }

    @Override
    public byte[] serialize() {
        byte[] serialized = new byte[32];

        byte[] sidBytes = Arrays.copyOfRange(sid.getBytes(), 0, 16);
        log.info("sidBytes : {}", sidBytes);
        log.info("sidBytes.length : {}", sidBytes.length);
        byte[] pnameBytes = Arrays.copyOfRange(sid.getBytes(), 0, 16);
        log.info("pnameBytes : {}", pnameBytes);
        log.info("pnameBytes.length : {}", pnameBytes.length);

        System.arraycopy(sidBytes,0,serialized,0,16);
        System.arraycopy(pnameBytes,0,serialized,16,16);

        return serialized;
    }

    @Override
    public SettingRequest deserialize(byte[] byteArray) {
        return new SettingRequest(byteArray);
    }
}
