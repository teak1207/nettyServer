package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.PreinstallNetworkSetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NetworkSetRepository extends JpaRepository<PreinstallNetworkSetModel, Integer> {

    // 서버 설정 정보(2)를 가져오기 위함.
        PreinstallNetworkSetModel findAllByPnameAndSid(String pname, String sid);
}
