package com.devgong.nettyserver.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestType {
    DEVICE((byte) 'D'),
    SERVER((byte) 'S');

    private final byte type;
}
