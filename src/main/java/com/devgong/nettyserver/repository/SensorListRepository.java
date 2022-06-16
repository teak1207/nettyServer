package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.SensorListModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorListRepository extends JpaRepository<SensorListModel, Integer> {


    SensorListModel findPreInstallModelByMphone(String modemNum);
}
