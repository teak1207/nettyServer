package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.SettingLeakProjectModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingLeakProjectRepository extends JpaRepository<SettingLeakProjectModel, Integer> {

    SettingLeakProjectModel findAllBySidAndFactorypPname(String x, String y);
}
