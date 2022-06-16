package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.DeviceSetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceSetRepository extends JpaRepository<DeviceSetModel, Integer> {
// 기기 설정값(7)를 가져오기 위함
//      DeviceSetModel findDeviceSetModelBySn(String ssn);
      DeviceSetModel findBySn(String ssn);
}
