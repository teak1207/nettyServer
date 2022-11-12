package com.devgong.nettyserver.repository;


import com.devgong.nettyserver.domain.SettingSensorListAllModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingSensorListAllRepository extends JpaRepository<SettingSensorListAllModel, Integer> {

    Optional<SettingSensorListAllModel> findBySsn(String modemNum);
}
