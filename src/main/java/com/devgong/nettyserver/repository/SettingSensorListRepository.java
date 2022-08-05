package com.devgong.nettyserver.repository;


import com.devgong.nettyserver.domain.SettingSensorListModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingSensorListRepository extends JpaRepository<SettingSensorListModel, Integer> {

    SettingSensorListModel findAllBySidAndPname(String sid, String pname);

}
