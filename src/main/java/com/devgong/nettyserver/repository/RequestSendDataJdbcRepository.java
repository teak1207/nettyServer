package com.devgong.nettyserver.repository;

public interface RequestSendDataJdbcRepository {

    String selectBySnAndSid(String sn, String sid);

}
