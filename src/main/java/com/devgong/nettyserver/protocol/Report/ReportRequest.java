package com.devgong.nettyserver.protocol.Report;


import com.devgong.nettyserver.protocol.Serializable;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
public class ReportRequest implements Serializable<ReportRequest> {

    public ReportRequest(byte[] payload) {
        if (payload == null || payload.length != 178) {
            throw new IllegalArgumentException("Setting Request payload error!");
        }
    }

    @Override
    public byte[] serialize() {

        return new byte[178];
    }

    @Override
    public ReportRequest deserialize(byte[] byteArray) {
        return new ReportRequest(byteArray);
    }
}
