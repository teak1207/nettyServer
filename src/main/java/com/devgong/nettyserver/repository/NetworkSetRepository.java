package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.PreinstallNetworkSetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NetworkSetRepository extends JpaRepository<PreinstallNetworkSetModel, Integer> {

    // 서버 설정 정보(2)를 가져오기 위함.
        @Query("select m from PreinstallNetworkSetModel as m where m.pname= :pname and m.sid = :sid and m.active = '1' and m.dataServer is not null and m.dataPort is not null ")
        PreinstallNetworkSetModel findAllByPnameAndSid(String pname, String sid) ;
}
