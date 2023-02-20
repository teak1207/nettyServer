package com.devgong.nettyserver.repository;


import com.devgong.nettyserver.domain.SettingSensorListModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingSensorListRepository extends JpaRepository<SettingSensorListModel, Integer> {
    @Query("select m from SettingSensorListModel AS m where m.sid = :sid and m.pname= :pname and m.serialNumber= :serialNumber  and NOT m.col_valid = -1 order by m.cid desc ")
    SettingSensorListModel findBySidAndPnameAndSerialNumber(String sid, String pname, String serialNumber);
}
