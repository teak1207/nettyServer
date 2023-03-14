package com.devgong.nettyserver.repository;

import org.apache.commons.lang3.tuple.Pair;

public interface RequestSendDataJdbcRepository {

    Pair<String, String> findSnAndFnameBySnAndSid(String sn, String sid);

//    String getFnumOfReceivingSensorBySnAndSid(String fname, String sn, String sid);

//    Boolean updateFnum(String fname, String sn, String sid);
}
