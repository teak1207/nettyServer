package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.PreInstallSettingModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreInstallSettingRepository extends JpaRepository<PreInstallSettingModel, Integer> {
}
