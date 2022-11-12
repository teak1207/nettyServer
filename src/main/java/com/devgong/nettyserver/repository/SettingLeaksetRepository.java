package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.SettingLeaksetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingLeaksetRepository extends JpaRepository<SettingLeaksetModel, Integer> {
    SettingLeaksetModel findTop1BySidAndPnameAndSnOrderByCidDesc(String sid, String Pname, String sn);
}
