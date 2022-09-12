package com.devgong.nettyserver.protocol;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
@Value
public class Packet<T extends Serializable<T>> {

//    PacketFlag flag; // 1 byte
    String sensorId; // 24 byte
    LocalDateTime dateTime; // 15 byte
    RequestType requestType; // 1 byte
    String parameterLength; // 4 byte
    T parameter;
    byte[] checksum; // 2 byte

//    public Packet(PacketFlag flag, String sensorId, LocalDateTime dateTime, RequestType requestType, String parameterLength, T parameter) {
//        this.flag = flag;
//        this.sensorId = sensorId;
//        this.dateTime = dateTime;
//        this.requestType = requestType;
//        this.parameter = parameter;
//        // TODO : Parameter Length 어떻게 byte[4] 로 변환?
//        this.parameterLength = parameterLength;
//        this.checksum = makeChecksum();
//    }

    public Packet(String sensorId, LocalDateTime dateTime, RequestType requestType, String parameterLength, T parameter) {
        this.sensorId = sensorId;
        this.dateTime = dateTime;
        this.requestType = requestType;
        this.parameter = parameter;
        // TODO : Parameter Length 어떻게 byte[4] 로 변환?
        this.parameterLength = parameterLength;
        this.checksum = makeChecksum();
    }


    public Packet(byte[] packet, Class<T> clazz) {
        // TODO : 패킷 길이 제한조건 넣어야 함
        if (packet == null) {
            throw new IllegalArgumentException("Packet error!");
        }

//        flag = Arrays.stream(PacketFlag.values()).filter(flag -> flag.getFlag() == packet[0]).findAny()
//                .orElseThrow(() -> new IllegalStateException("Invalid flag error : " + packet[0]));
//        sensorId = new String(Arrays.copyOfRange(packet, 1, 25));
//        dateTime = LocalDateTime.parse(new String(Arrays.copyOfRange(packet, 25, 40)), DateTimeFormatter.ofPattern("yyyyMMdd HHmmss"));
//        requestType = Arrays.stream(RequestType.values()).filter(type -> type.getType() == packet[40]).findAny()
//                .orElseThrow(() -> new IllegalStateException("Invalid requestType error : " + packet[40]));
//        parameterLength = new String(Arrays.copyOfRange(packet, 41, 45));

        sensorId = new String(Arrays.copyOfRange(packet, 0, 24));
        dateTime = LocalDateTime.parse(new String(Arrays.copyOfRange(packet, 24, 39)), DateTimeFormatter.ofPattern("yyyyMMdd HHmmss"));
        requestType = Arrays.stream(RequestType.values()).filter(type -> type.getType() == packet[39]).findAny()
                .orElseThrow(() -> new IllegalStateException("Invalid requestType error : " + packet[39]));
        parameterLength = new String(Arrays.copyOfRange(packet, 40, 44));


        try {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor(byte[].class);
            parameter = declaredConstructor.newInstance((Object) Arrays.copyOfRange(packet, 44, packet.length - 2));
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Invalid parameter error!");
        }

        checksum = Arrays.copyOfRange(packet, packet.length - 2, packet.length);

        log.info("input checksum : {}, {}", checksum[0], checksum[1]);

        if (!validateChecksum()) {
            throw new IllegalStateException("Invalid checksum error!");
        }
    }

    public byte[] serialize() {
        byte[] serializeExceptChecksum = serializeExceptChecksum();
        byte[] serialized = new byte[2 + serializeExceptChecksum.length];
        System.arraycopy(checksum, 0, serialized, serializeExceptChecksum.length, 2);
        return serialized;
    }

    private byte[] serializeExceptChecksum() {
        byte[] serializedParameter = parameter.serialize();
//        byte[] serialized = new byte[45 + serializedParameter.length];
        byte[] serialized = new byte[44 + serializedParameter.length];
//        serialized[0] = flag.getFlag();
//        System.arraycopy(sensorId.getBytes(), 0, serialized, 1, 24);
//        System.arraycopy(dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd HHmmss")).getBytes(), 0, serialized, 25, 15);
//        serialized[40] = requestType.getType();
//        System.arraycopy(parameterLength.getBytes(), 0, serialized, 41, 4);
//        System.arraycopy(serializedParameter, 0, serialized, 45, serializedParameter.length);
        System.arraycopy(sensorId.getBytes(), 0, serialized, 0, 24);
        System.arraycopy(dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd HHmmss")).getBytes(), 0, serialized, 24, 15);
        serialized[39] = requestType.getType();
        System.arraycopy(parameterLength.getBytes(), 0, serialized, 40, 4);
        System.arraycopy(serializedParameter, 0, serialized, 44, serializedParameter.length);

        for(byte a : sensorId.getBytes()) {
            log.info("sensorId : {}", (char) a);
        }
        for(byte a : dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd HHmmss")).getBytes()) {
            log.info("dateTime : {}", (char) a);
        }
        for(byte a : parameterLength.getBytes()) {
            log.info("parameterLength : {}", (char) a);
        }
        for(byte a : serializedParameter) {
            log.info("serializedParameter : {}", (char) a);
        }

        return serialized;
    }

    private boolean validateChecksum() {
        byte a = 'A' + 32;
        int accumulation = a;
        for (byte b : serializeExceptChecksum()) {
            log.info("accumulation : {} ", accumulation);
            log.info("validateChecksum byte : {}", b);
            log.info("validateChecksum byte(char) : {}", (char) b);
            accumulation += b;
        }

        log.info("accumulation : {}", accumulation);
        log.info("accumulation contrast : {}", Integer.parseInt(String.format("%x%x", checksum[0], checksum[1]), 16));

        return accumulation == Integer.parseInt(String.format("%x%x", checksum[0], checksum[1]), 16);
    }

    private byte stringToByte(String input) {
        return input.length() == 1 ? (byte) Character.digit(input.charAt(0), 16)
                : (byte) ((Character.digit(input.charAt(0), 16) << 4) + Character.digit(input.charAt(1), 16));
    }

    private byte[] makeChecksum() {
        int accumulation = 0;
        for (byte b : serializeExceptChecksum()) {
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

        return totalByte;
    }
}
