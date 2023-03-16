package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.PreInstallSensorListAllModel;
import com.devgong.nettyserver.repository.DataUpdateRepository;
import com.devgong.nettyserver.repository.PreInstallSensorListAllRepository;
import com.devgong.nettyserver.repository.RequestSendDataJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class DataService {


    /*
     * @author devgong
     * Data process, 저장을 처리함.
     * (1) String sn - 해당센서정보를 가져오기 위함.
     * (2) String sid - data sequence 사용하기 위함.
     * (3) byte[] request - 저장할 data []
     * @version 1.0
     */


    private final PreInstallSensorListAllRepository preInstallSensorListAllRepository;
    private final RequestSendDataJdbcRepository requestSendDataJdbcRepository;
    private final DataSequenceService dataSequenceService;


    public void saveData(String sn, String sid, byte[] request) throws IOException {

        PreInstallSensorListAllModel sensorListAllModel = preInstallSensorListAllRepository.findPreInstallModelBySsn(sn);

        //memo: 명확한 구분값으로 fname을 select 해옴.
        Pair<Integer, String> result = requestSendDataJdbcRepository.findCidAndFnameBySnAndSid(sensorListAllModel.getSsn(), sensorListAllModel.getAsid());

        Path path = Paths.get(result.getRight());

        //memo: Files.write(path, outputStream.toByteArray()) , 데이터를 순차적으로 누적 저장.
        Files.write(path, request, StandardOpenOption.APPEND);

//        dataSequenceService.decrementDataSequence(String.valueOf(result.getLeft()), sid, sn, LocalDateTime.now());
        dataSequenceService.decrementDataSequence(sn, sid, sn, LocalDateTime.now());

    }

/*    public boolean updateData(String fname, String sid, String sn) {

        return dataUpdateRepository.updateCompleteTime(fname, sid, sn);
    }*/
}
