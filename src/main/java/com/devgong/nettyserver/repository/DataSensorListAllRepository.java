package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.PreInstallSensorListAllModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSensorListAllRepository extends JpaRepository<PreInstallSensorListAllModel,Integer> {

    PreInstallSensorListAllModel findPreInstallSensorListAllModelBySsn(String serialNum);
}
