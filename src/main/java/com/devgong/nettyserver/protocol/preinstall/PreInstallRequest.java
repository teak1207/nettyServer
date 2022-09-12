package com.devgong.nettyserver.protocol.preinstall;

import com.devgong.nettyserver.protocol.Serializable;
import lombok.Value;

import java.util.Arrays;

@Value
public class PreInstallRequest implements Serializable<PreInstallRequest> {

    String modemPhoneNumber; // 16 byte
    String debugMessage; // 13 byte

    public PreInstallRequest(byte[] payload) {
        if (payload == null || payload.length != 29) {
            throw new IllegalArgumentException("PreInstallRequest payload error!");
        }

        modemPhoneNumber = new String(Arrays.copyOfRange(payload, 0, 16));
        debugMessage = new String(Arrays.copyOfRange(payload, 16, 29));
    }

    @Override
    public PreInstallRequest deserialize(byte[] byteArray) {
        return new PreInstallRequest(byteArray);
    }

    @Override
    public byte[] serialize() {
        byte[] serialized = new byte[29];
        System.arraycopy(modemPhoneNumber.getBytes(), 0, serialized, 0, 16);
        System.arraycopy(debugMessage.getBytes(), 0, serialized, 16, 13);
        return serialized;
    }
}
