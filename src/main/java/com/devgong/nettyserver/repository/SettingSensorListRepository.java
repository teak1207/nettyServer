package com.devgong.nettyserver.repository;


import com.devgong.nettyserver.domain.SettingSensorListModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingSensorListRepository extends JpaRepository<SettingSensorListModel, Integer> {




    @Query("select m from SettingSensorListModel AS m where m.sid = :sid and m.pname= :pname and m.serialNumber= :sn  and NOT m.col_valid = -1")
    SettingSensorListModel findAllBySidAndPnameAndSerialNumber(String sid, String pname,String serialNumber);

}
