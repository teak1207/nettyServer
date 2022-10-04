package com.devgong.nettyserver.protocol.preinstall;

import com.devgong.nettyserver.protocol.Serializable;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@Value
public class PreInstallRequest implements Serializable<PreInstallRequest> {

    String modemPhoneNumber; // 16 byte
    String debugMessage; // 13 byte

    public PreInstallRequest(byte[] payload) {
        if (payload == null || payload.length != 29) {
            throw new IllegalArgumentException("PreInstallRequest payload error!");
        }

//        modemPhoneNumber = new String(Arrays.copyOfRange(payload, 0, 16)).trim();
        modemPhoneNumber = new String(Arrays.copyOfRange(payload, 0, 16));
//        debugMessage = new String(Arrays.copyOfRange(payload, 16, 29)).trim();
        debugMessage = new String(Arrays.copyOfRange(payload, 16, 29));
    }

    @Override
    public PreInstallRequest deserialize(byte[] byteArray) {
        return new PreInstallRequest(byteArray);
    }

    @Override
    public byte[] serialize() {
        byte[] serialized = new byte[29];

        byte[] modemPhoneNumberBytes = Arrays.copyOfRange(modemPhoneNumber.getBytes(), 0, 16);
        log.info("modemPhoneNumberBytes length : {}", modemPhoneNumberBytes.length);
        byte[] debugMessageBytes  = Arrays.copyOfRange(debugMessage.getBytes(), 0, 13);
        log.info("debugMessageBytes length : {}", debugMessageBytes.length);

        System.arraycopy(modemPhoneNumberBytes, 0, serialized, 0, 16);
        System.arraycopy(debugMessageBytes, 0, serialized, 16, 13);

        for(byte b : serialized) {
            log.info("serialized : {}", b);
            log.info("serialized(char) : {}", (char)b);
        }

        return serialized;
    }

}
