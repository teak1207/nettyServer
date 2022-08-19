package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.PreInstallSensorListAllModel;
import com.devgong.nettyserver.domain.RequestListAllModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestSensorListAllRepository extends JpaRepository<RequestListAllModel, Integer> {
    RequestListAllModel findAllBySsn(String serialNum);

}
