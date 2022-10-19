package com.devgong.nettyserver.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PacketFlag {
    REQUEST((byte) '4'),
    DATA((byte) '5'),
    SETTING((byte) '6'),
    REPORT((byte) '7'),
    ACK((byte) '8'),
    NAK((byte) '9'),
    PREINSTALL((byte) 'A');

    private final byte flag;
}
