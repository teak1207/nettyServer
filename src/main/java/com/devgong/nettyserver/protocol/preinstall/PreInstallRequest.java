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

        modemPhoneNumber = new String(Arrays.copyOfRange(payload, 0, 16)).trim();
        debugMessage = new String(Arrays.copyOfRange(payload, 16, 29)).trim();
    }

    @Override
    public PreInstallRequest deserialize(byte[] byteArray) {
        return new PreInstallRequest(byteArray);
    }

    @Override
    public byte[] serialize() {
        byte[] serialized = new byte[29];

        byte[] modemPhoneNumberBytes = Arrays.copyOfRange(modemPhoneNumber.getBytes(), 0, 16);
        byte[] debugMessageBytes  = Arrays.copyOfRange(debugMessage.getBytes(), 0, 13);

        System.arraycopy(modemPhoneNumberBytes, 0, serialized, 0, 16);
        System.arraycopy(debugMessageBytes, 0, serialized, 0, 13);

        return serialized;
    }
}
