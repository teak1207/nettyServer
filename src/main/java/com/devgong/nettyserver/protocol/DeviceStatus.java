package com.devgong.nettyserver.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DeviceStatus {
    OK(new byte[]{'0', '0'}),
    RADIO_ERROR(new byte[]{'0', '1'}),
    MEMORY_ERROR(new byte[]{'1', '0'});

    private final byte[] status;
}