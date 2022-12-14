package com.devgong.nettyserver.protocol.request;


import com.devgong.nettyserver.protocol.Serializable;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;

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

        log.info("chk3 : {}", byteArrayToHex(payload));

        byte[] Bytes = frameCount.getBytes();
        log.info("chk4 : {}",  byteArrayToHex(Bytes) );

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

        log.info("sc3 : {}", serialized);

        return serialized;
    }

    public byte[] getFrameCountBytes() {

        byte[] frameCountBytes = Arrays.copyOfRange(frameCount.getBytes(), 0, 2);


        return frameCountBytes;
    }


    public int getFrameCountBytesConverted() {

        byte[] frameCountBytes = Arrays.copyOfRange(frameCount.getBytes(), 0, 2);

        return bytesToInt(frameCountBytes);
    }


    public byte[] getDataSizeBytes() {

        byte[] DataSizeBytes = Arrays.copyOfRange(dataSize.getBytes(), 0, 2);

        return DataSizeBytes;
    }

    public byte[] getSampleRateBytes() {

        byte[] SampleRateBytes = Arrays.copyOfRange(sampleRate.getBytes(), 0, 1);

        return SampleRateBytes;
    }

    String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (final byte b : a)
            sb.append(String.format("%02x ", b & 0xff));
        return sb.toString();
    }


    public int bytesToInt(byte[] bytes) {
        int result = (int) bytes[1] & 0xFF;
        result |= (int) bytes[0] << 8 & 0xFF00;

        return result;
    }

    public String getStringToHex(String input) throws UnsupportedEncodingException {
        byte[] Bytes = input.getBytes();
        return DatatypeConverter.printHexBinary(Bytes);
    }




    @Override
    public ReqRequest deserialize(byte[] byteArray) {
        return new ReqRequest(byteArray);
    }
}
