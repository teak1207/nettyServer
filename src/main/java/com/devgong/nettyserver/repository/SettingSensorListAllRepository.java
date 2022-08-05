package com.devgong.nettyserver.repository;


import com.devgong.nettyserver.domain.SettingSensorListAllModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingSensorListAllRepository extends JpaRepository<SettingSensorListAllModel, Integer> {

    SettingSensorListAllModel findPreInstallModelBySsn(String modemNum);
}
