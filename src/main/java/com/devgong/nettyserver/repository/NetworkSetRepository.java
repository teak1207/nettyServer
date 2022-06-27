package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.NetworkSetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.*;

@Repository
public interface NetworkSetRepository extends JpaRepository<NetworkSetModel, Integer> {
    // 서버 설정 정보(2)를 가져오기 위함.

//    NetworkSetModel findNetworkSetModelBySidAndPname(String asid,String pname);

//    NetworkSetModel findBySidAndPname(String sid,String pname);
//    List<NetworkSetModel> findAllByPnameAndSid(String pname, String sid);

        NetworkSetModel findAllByPnameAndSid(String pname,String sid);
}
