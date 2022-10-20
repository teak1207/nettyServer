package com.devgong.nettyserver.protocol.data;


import com.devgong.nettyserver.protocol.Serializable;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@Value

public class DataRequest implements Serializable<DataRequest> {

    String data;

    public DataRequest(byte[] payload) {

        if (payload == null || payload.length != 557) {
            throw new IllegalArgumentException("Setting Request payload error!");
        }
        data = new String(Arrays.copyOfRange(payload, 0, 512));
    }


    @Override
    public byte[] serialize() {

        byte[] serialized = new byte[512];

        byte[] dataBytes = Arrays.copyOfRange(data.getBytes(), 0, 512);

        System.arraycopy(dataBytes, 0, serialized, 0, 512);

        return serialized;
    }

    @Override
    public DataRequest deserialize(byte[] byteArray) {
        return new DataRequest(byteArray);
    }
}
