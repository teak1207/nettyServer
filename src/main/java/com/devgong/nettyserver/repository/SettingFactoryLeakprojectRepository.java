package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.SettingFactoryLeakprojectModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingFactoryLeakprojectRepository extends JpaRepository<SettingFactoryLeakprojectModel, Integer> {

    SettingFactoryLeakprojectModel findAllByFactoryPname(String x);
}
