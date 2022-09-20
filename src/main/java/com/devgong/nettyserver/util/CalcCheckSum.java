package com.devgong.nettyserver.util;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public class CalcCheckSum {

    public byte fudnsml(String input) {
        return input.length() == 1 ? (byte) Character.digit(input.charAt(0), 16)
                : (byte) ((Character.digit(input.charAt(0), 16) << 4) + Character.digit(input.charAt(1), 16));
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (final byte b : a)
            sb.append(String.format("%02x ", b & 0xff));
        return sb.toString();
    }


    public byte[] makeChecksum(String totalData) {
        int total = 0;
        for (int i = 0; i < totalData.length(); i++) {
            total += totalData.charAt(i);    // 문자열 10진수로 바꿔서 저장
        }
        System.out.println("[글자를 전부 더한 수] : " + total);

        String hex = Integer.toHexString(total);
        String first = "";
        String second = "";

        if (hex.length() == 3) {
            first = hex.substring(0, 1);
            second = hex.substring(1, 3);
        } else if (hex.length() == 4) {
            first = hex.substring(0, 2);
            second = hex.substring(2, 4);
        }
        System.out.println("[first] : " + first);
        System.out.println("[second] : " + second);
        // "c" -> "0x0c" (byte)
        byte firstByte = fudnsml(first);
        byte secondByte = fudnsml(second);

        byte[] totalByte = new byte[2];
        totalByte[0] = firstByte;
        totalByte[1] = secondByte;

        return totalByte;

    }

}
