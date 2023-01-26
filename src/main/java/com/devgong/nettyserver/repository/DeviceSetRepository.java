package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.PreinstallDeviceSetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;

@Repository
public interface DeviceSetRepository extends JpaRepository<PreinstallDeviceSetModel, Integer> {
// 기기 설정값(7)를 가져오기 위함
//      PreinstallDeviceSetModel findDeviceSetModelBySn(String ssn);
      PreinstallDeviceSetModel findBySn(String ssn) throws NoResultException;
}
