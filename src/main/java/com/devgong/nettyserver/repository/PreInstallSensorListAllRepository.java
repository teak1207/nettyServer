package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.PreInstallSensorListAllModel;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;

@Repository
public interface PreInstallSensorListAllRepository extends JpaRepository<PreInstallSensorListAllModel, Integer> {

    PreInstallSensorListAllModel findPreInstallModelBySsn(String sn) throws NoResultException;

    PreInstallSensorListAllModel findPreInstallSensorListAllModelByMphone(String modemNum) throws NoResultException;
}
