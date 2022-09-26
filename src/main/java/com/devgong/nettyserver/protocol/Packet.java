package com.devgong.nettyserver.protocol;

import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Value
@ToString
public class Packet<T extends Serializable<T>> {

    PacketFlag flag; // 1 byte
    String sensorId; // 24 byte
    LocalDateTime dateTime; // 15 byte
    RequestType requestType; // 1 byte
    long parameterLength; // 4 byte
    T parameter;
    byte[] checksum; // 2 byte


    public Packet(PacketFlag flag, String sensorId, LocalDateTime dateTime, RequestType requestType, long parameterLength, T parameter) {
        this.flag = flag;
        this.sensorId = sensorId;
        this.dateTime = dateTime;
        this.requestType = requestType;
        this.parameter = parameter;
        // TODO : Parameter Length 어떻게 byte[4] 로 변환?
        this.parameterLength = parameterLength;
        this.checksum = makeChecksum();

    }

    public Packet(PacketFlag flag, byte[] packet, Class<T> clazz) {
        // TODO : 패킷 길이 제한조건 넣어야 함
        if (packet == null) {
            throw new IllegalArgumentException("Packet error!");
        }
        this.flag = flag;
        sensorId = new String(Arrays.copyOfRange(packet, 0, 24));
        dateTime = LocalDateTime.parse(new String(Arrays.copyOfRange(packet, 24, 39)), DateTimeFormatter.ofPattern("yyyyMMdd HHmmss"));
        requestType = Arrays.stream(RequestType.values()).filter(type -> type.getType() == packet[39]).findAny()
                .orElseThrow(() -> new IllegalStateException("Invalid requestType error : " + packet[39]));
        parameterLength = byteArrayToInt(Arrays.copyOfRange(packet, 40, 44))& 0xff;

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
        System.arraycopy(serializeExceptChecksum, 0, serialized, 0, serializeExceptChecksum.length);
        System.arraycopy(checksum, 0, serialized, serializeExceptChecksum.length, 2);
        return serialized;
    }

    private byte[] serializeExceptChecksum() {
        byte[] serializedParameter = parameter.serialize();
        byte[] serialized = new byte[45 + serializedParameter.length];

        //TODO : 패킷담을때만 long으로 처리하고, 다른데선 int로 처리하기위해 cast 처리
        int test = Optional.of(parameterLength).orElse(0L).intValue();

//        log.info("test555 : {}", test);
//        log.info("test555_contrast : {}", parameterLength);

        byte[] sensorIdBytes = Arrays.copyOfRange(sensorId.getBytes(), 0, 24);
        byte[] dateTimeBytes = Arrays.copyOfRange(dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd HHmmss")).getBytes(), 0, 15);
        byte[] paramterLengthBytes = Arrays.copyOfRange(intToByteArray((int) parameterLength), 0, 4);

//        for (byte c : paramterLengthBytes) {
//            log.info("test666 : {}", c & 0xff);
//        }
//        log.info("paramterLengthBytes.length : {}", paramterLengthBytes.length);

//        int res=0;
//        for(int i =0; i< paramterLengthBytes.length;i++){
//            res = (res*10) + ((paramterLengthBytes[i] & 0xff));
//        }


        serialized[0] = flag.getFlag();
        System.arraycopy(sensorIdBytes, 0, serialized, 1, 24);
        System.arraycopy(dateTimeBytes, 0, serialized, 25, 15);
        serialized[40] = requestType.getType();
        System.arraycopy(paramterLengthBytes, 0, serialized, 41, 4);
        System.arraycopy(serializedParameter, 0, serialized, 45, serializedParameter.length);

        return serialized;
    }

    private boolean validateChecksum() {
        int accumulation = 0;

        for (byte b : serializeExceptChecksum()) {
            log.info("validateChecksum byte(char) : {}", (char) b);
            log.info("validateChecksum byte(char) : {}", b);

            accumulation += b;

        }

        log.info("validateChecksum accumulation : {}", accumulation); //3295
        log.info("validateChecksum accumulation contrast : {}", Integer.parseInt(String.format("%x%x", checksum[0], checksum[1]), 16));  //3263

        return accumulation == Integer.parseInt(String.format("%x%x", checksum[0], checksum[1]), 16);
    }

    public static byte stringToByte(String input) {
        return input.length() == 1 ? (byte) Character.digit(input.charAt(0), 16)
                : (byte) ((Character.digit(input.charAt(0), 16) << 4) + Character.digit(input.charAt(1), 16));
    }

    private byte[] makeChecksum() {
        int accumulation = 0;   // 32의 차이가 이건가 싶어서 주석처리
        for (byte b : serializeExceptChecksum()) {
            //todo : b를 unsigned  처리를 해보자
            accumulation += b & 0xff;
            log.info("accumulation byte(char) : {}", (char) b & 0xff);
            log.info("accumulation byte(char) : {}", accumulation);
        }
        log.info("test222 : {}", accumulation);


        String hex = Integer.toHexString(accumulation);
        String first = "";
        String second = "";

        //TODO : 데이터를 일일히 체크했을 시 ,문제 없음, hex를 찍었더니 값 불일치 발생.


        log.info("hex : {}", hex);
        if (hex.length() == 3) {
            first = hex.substring(0, 1);
            second = hex.substring(1, 3);
            log.info("first , 1 : {} {}", first, second);
        } else if (hex.length() == 4) {
            first = hex.substring(0, 2);
            second = hex.substring(2, 4);
            log.info("first , 2 : {} {}", first, second);
        }

        // "c" -> "0x0c" (byte)
        byte firstByte = stringToByte(first);
        byte secondByte = stringToByte(second);

        byte[] totalByte = new byte[2];
        totalByte[0] = firstByte;
        totalByte[1] = secondByte;

        return totalByte;
    }

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

}
