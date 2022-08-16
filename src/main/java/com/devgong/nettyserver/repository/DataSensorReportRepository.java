package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.DataInsertModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSensorReportRepository  extends JpaRepository<DataInsertModel,Integer> {
}
