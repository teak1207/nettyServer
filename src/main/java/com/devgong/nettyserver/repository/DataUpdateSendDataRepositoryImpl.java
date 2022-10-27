package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.DataUpdateModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


@Slf4j
@RequiredArgsConstructor
@Repository
public class DataUpdateSendDataRepositoryImpl implements DataUpdateSendDataRepository {
    @Override
    public DataUpdateModel update() {
        return null;
    }
}
