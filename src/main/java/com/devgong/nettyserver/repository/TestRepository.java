package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.DataLeakSendDataModel;

public interface TestRepository {

    String selectBySnAndSid(String sn, String sid);

}
