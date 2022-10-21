package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.DataUpdateModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeakSendDataRepository extends JpaRepository<DataUpdateModel, Integer> {

        //memo : 조건은 fname으로 가져오면 된다!!!!!!
}
