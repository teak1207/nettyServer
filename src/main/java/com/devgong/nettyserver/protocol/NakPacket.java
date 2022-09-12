package com.devgong.nettyserver.protocol;

import lombok.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static com.devgong.nettyserver.protocol.Packet.intToByteArray;
import static com.devgong.nettyserver.protocol.Packet.stringToByte;

@Value
public class NakPacket {
    PacketFlag flag = PacketFlag.NAK;
    String sensorId; // 24 byte
    LocalDateTime dateTime; // 15 byte
    RequestType requestType = RequestType.SERVER; // 1 byte
    int parameterLength = 0; // 4 byte
    byte[] checksum; // 2 byte

    public NakPacket(String sensorId, LocalDateTime dateTime) {
        this.sensorId = sensorId;
        this.dateTime = dateTime;

        byte[] serialized = serializeExceptChecksum();

        int accumulation = 32;
        for (byte b : serialized) {
            accumulation += b;
        }

        String hex = Integer.toHexString(accumulation);
        String first = "";
        String second = "";

        if (hex.length() == 3) {
            first = hex.substring(0, 1);
            second = hex.substring(1, 3);
        } else if (hex.length() == 4) {
            first = hex.substring(0, 2);
            second = hex.substring(2, 4);
        }

        // "c" -> "0x0c" (byte)
        byte firstByte = stringToByte(first);
        byte secondByte = stringToByte(second);

        byte[] totalByte = new byte[2];
        totalByte[0] = firstByte;
        totalByte[1] = secondByte;

        this.checksum = totalByte;
    }

    private byte[] serializeExceptChecksum() {
        byte[] serialized = new byte[45];

        byte[] sensorIdBytes = Arrays.copyOfRange(sensorId.getBytes(), 0, 24);
        byte[] dateTimeBytes = Arrays.copyOfRange(dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd HHmmss")).getBytes(), 0, 15);

        serialized[0] = flag.getFlag();
        System.arraycopy(sensorIdBytes, 0, serialized, 1, 24);
        System.arraycopy(dateTimeBytes, 0, serialized, 25, 15);
        serialized[40] = requestType.getType();
        System.arraycopy(intToByteArray(parameterLength), 0, serialized, 41, 4);

        return serialized;
    }


    public byte[] serialize() {
        byte[] serializeExceptChecksum = serializeExceptChecksum();
        byte[] serialized = new byte[2 + serializeExceptChecksum.length];
        System.arraycopy(serializeExceptChecksum, 0, serialized, 0, serializeExceptChecksum.length);
        System.arraycopy(checksum, 0, serialized, serializeExceptChecksum.length, 2);
        return serialized;
    }
}
