package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.SettingLeaksetModel;
import com.devgong.nettyserver.domain.SettingSensorListAllModel;
import com.devgong.nettyserver.domain.SettingSensorListModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingLeaksetRepository extends JpaRepository<SettingLeaksetModel, Integer> {
    SettingLeaksetModel findAllBySidAndPname(String sid, String pname);

}
