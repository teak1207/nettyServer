package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.RequestLeakDataModel;
import com.devgong.nettyserver.protocol.NewPacket;
import com.devgong.nettyserver.protocol.request.ReqRequest;

public interface RequestSendDataRepository {

    boolean  save(NewPacket<ReqRequest> request, RequestLeakDataModel requestLeakDataModel);
}
