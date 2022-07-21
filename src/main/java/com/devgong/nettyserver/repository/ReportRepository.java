package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.PreinstallReportModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<PreinstallReportModel,Integer> {

}
