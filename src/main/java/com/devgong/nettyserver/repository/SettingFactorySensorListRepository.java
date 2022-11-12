package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.SettingFactorySensorListModel;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.function.Function;

@Repository
public interface SettingFactorySensorListRepository extends JpaRepository<SettingFactorySensorListModel, Integer> {

    Optional<SettingFactorySensorListModel> findBySidAndPnameAndSn(String sid, String aProject, String ssn);
}
