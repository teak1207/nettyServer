package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.PreInstallSensorListModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreInstallSensorListRepository extends JpaRepository<PreInstallSensorListModel, Integer> {

    PreInstallSensorListModel findBySerialNumber(String ssn);
}
