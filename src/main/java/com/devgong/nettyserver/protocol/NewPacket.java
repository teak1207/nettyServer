package com.devgong.nettyserver.protocol;

import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
@Value
@ToString
public class NewPacket<T extends Serializable<T>> {

    PacketFlag flag; // 1 byte
    String sensorId; // 24 byte
    LocalDateTime dateTime; // 15 byte
    RequestType requestType; // 1 byte
    long parameterLength; // 4 byte
    T parameter;

    public NewPacket(PacketFlag flag, String sensorId, LocalDateTime dateTime, RequestType requestType, long parameterLength, T parameter) {
        this.flag = flag;
        this.sensorId = sensorId;
        this.dateTime = dateTime;
        this.requestType = requestType;
        this.parameter = parameter;
        this.parameterLength = parameterLength;
    }

    public NewPacket(PacketFlag flag, byte[] packet, Class<T> clazz) {

        if (packet == null) {
            throw new IllegalArgumentException("Packet error!");
        }
        this.flag = flag;
        sensorId = new String(Arrays.copyOfRange(packet, 0, 24));
        dateTime = LocalDateTime.parse(new String(Arrays.copyOfRange(packet, 24, 39)), DateTimeFormatter.ofPattern("yyyyMMdd HHmmss"));
        requestType = Arrays.stream(RequestType.values()).filter(type -> type.getType() == packet[39]).findAny()
                .orElseThrow(() -> new IllegalStateException("Invalid requestType error : " + packet[39]));
        parameterLength = byteArrayToInt(Arrays.copyOfRange(packet, 40, 44));

        try {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor(byte[].class);
            parameter = declaredConstructor.newInstance((Object) Arrays.copyOfRange(packet, 44, packet.length));


        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Invalid parameter error!");
        }

    }

//    public byte[] serialize() {
//        byte[] serializeExceptChecksum = serializeExceptChecksum();
//        byte[] serialized = new byte[2 + serializeExceptChecksum.length];
//        System.arraycopy(serializeExceptChecksum, 0, serialized, 0, serializeExceptChecksum.length);
//        System.arraycopy(checksum, 0, serialized, serializeExceptChecksum.length, 2);
//        return serialized;
//    }

//    private byte[] serializeExceptChecksum() {
//        byte[] serializedParameter = parameter.serialize();
//        byte[] serialized = new byte[45 + serializedParameter.length];
//
//        int test = Optional.of(parameterLength).orElse(0L).intValue();
//
//
//        byte[] sensorIdBytes = Arrays.copyOfRange(sensorId.getBytes(), 0, 24);
//        byte[] dateTimeBytes = Arrays.copyOfRange(dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd HHmmss")).getBytes(), 0, 15);
//        byte[] paramterLengthBytes = Arrays.copyOfRange(intToByteArray((int) parameterLength), 0, 4);
//
//        int res = 0;
//        for (int i = 0; i < paramterLengthBytes.length; i++) {
//            res = (res * 10) + ((paramterLengthBytes[i] & 0xff));
//        }
//
//        serialized[0] = flag.getFlag();
//        System.arraycopy(sensorIdBytes, 0, serialized, 1, 24);
//        System.arraycopy(dateTimeBytes, 0, serialized, 25, 15);
//        serialized[40] = requestType.getType();
//        System.arraycopy(paramterLengthBytes, 0, serialized, 41, 4);
//        System.arraycopy(serializedParameter, 0, serialized, 45, serializedParameter.length);
//
//        return serialized;
//    }


    public static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }

    public static int byteArrayToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                ((bytes[3] & 0xFF));
    }

    public static byte[] longToByteArray(long data) {

        return new byte[]{
                (byte) ((data >> 56) & 0xff),
                (byte) ((data >> 48) & 0xff),
                (byte) ((data >> 40) & 0xff),
                (byte) ((data >> 32) & 0xff),
                (byte) ((data >> 24) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 0) & 0xff),
        };
    }

    public byte[] getLocalDateBytes(byte[] packet) {


        byte[] array = Arrays.copyOfRange(packet, 24, 39);
        return array;
    }

}
