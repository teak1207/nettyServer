package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.ReportModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportModel,Integer> {

}
