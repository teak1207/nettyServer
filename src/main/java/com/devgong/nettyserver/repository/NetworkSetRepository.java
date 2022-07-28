package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.PreinstallNetworkSetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NetworkSetRepository extends JpaRepository<PreinstallNetworkSetModel, Integer> {
    // 서버 설정 정보(2)를 가져오기 위함.

//    PreinstallNetworkSetModel findNetworkSetModelBySidAndPname(String asid,String pname);

//    PreinstallNetworkSetModel findBySidAndPname(String sid,String pname);
//    List<PreinstallNetworkSetModel> findAllByPnameAndSid(String pname, String sid);

        PreinstallNetworkSetModel findAllByPnameAndSid(String pname, String sid);
}
