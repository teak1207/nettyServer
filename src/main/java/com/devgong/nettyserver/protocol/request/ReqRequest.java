package com.devgong.nettyserver.protocol.request;


import com.devgong.nettyserver.protocol.Serializable;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@Value
public class ReqRequest implements Serializable<ReqRequest> {

    String frameCount;
    String dataSize;
    String sampleRate;

    public ReqRequest(byte[] payload) {

        if (payload == null || payload.length != 5) {
            throw new IllegalArgumentException("Setting Request payload error!");
        }
        frameCount = new String(Arrays.copyOfRange(payload, 0, 2));
        dataSize = new String(Arrays.copyOfRange(payload, 2, 4));
        sampleRate = new String(Arrays.copyOfRange(payload, 4, 5));
    }

    @Override
    public byte[] serialize() {

        byte[] serialized = new byte[5];

        byte[] frameCountBytes = Arrays.copyOfRange(frameCount.getBytes(), 0, 2);
        byte[] dataSizeBytes = Arrays.copyOfRange(dataSize.getBytes(), 0, 2);
        byte[] sampleRateBytes = Arrays.copyOfRange(sampleRate.getBytes(), 0, 1);

        System.arraycopy(frameCountBytes, 0, serialized, 0, 2);
        System.arraycopy(dataSizeBytes, 0, serialized, 2, 2);
        System.arraycopy(sampleRateBytes, 0, serialized, 4, 1);

        log.info("sc3 : {}" , serialized);

        return serialized;
    }

    public byte[] getFrameCountBytes() {

        byte[] frameCountBytes = Arrays.copyOfRange(frameCount.getBytes(), 0, 2);

        return frameCountBytes;
    }

    public byte[] getDataSizeBytes() {

        byte[] DataSizeBytes = Arrays.copyOfRange(dataSize.getBytes(), 0, 2);

        return DataSizeBytes;
    }

    public byte[] getSampleRateBytes() {

        byte[] SampleRateBytes = Arrays.copyOfRange(sampleRate.getBytes(), 0, 1);

        return SampleRateBytes;
    }


    @Override
    public ReqRequest deserialize(byte[] byteArray) {
        return new ReqRequest(byteArray);
    }
}
