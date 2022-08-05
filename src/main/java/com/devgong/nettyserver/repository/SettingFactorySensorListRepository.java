package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.SettingFactorySensorListModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingFactorySensorListRepository extends JpaRepository<SettingFactorySensorListModel, Integer> {

    SettingFactorySensorListModel findAllBySidAndPnameAndSn(String x, String y, String z);
}
