package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.PreInstallModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreInstallRepository extends JpaRepository<PreInstallModel, Integer> {


    PreInstallModel findPreInstallModelByModemNumber(String modemNum);
}
