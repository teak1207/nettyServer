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

    private final PreInstallSensorListAllRepository preInstallSensorListAllRepository;
    private final DataUpdateRepository dataUpdateRepository;
    private final RequestSendDataJdbcRepository requestSendDataJdbcRepository;
    private final DataSequenceService dataSequenceService;

//    public boolean updateData(String fname, String sid, String sn) {
//        return dataUpdateRepository.updateCompleteTime(fname, sid, sn);
//    }

    public void saveData(String sn, String sid, byte[] request) throws IOException {

//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PreInstallSensorListAllModel sensorListAllModel = preInstallSensorListAllRepository.findPreInstallModelBySsn(sn);
        //순서 : sn 로  sensor_list_all 가서 sid  값을 가져온다.
        //순서 : leak_send_data_(sid)_(sn)에서 fname 을 가져온다.

        //memo: 명확한 구분값으로 fname을 select 해옴.
        Pair<Integer, String> result = requestSendDataJdbcRepository.findCidAndFnameBySnAndSid(sensorListAllModel.getSsn(), sensorListAllModel.getAsid());

        log.info("fname check : {}", result.getRight());

        Path path = Paths.get(result.getRight());

        //memo: Files.write(path, outputStream.toByteArray()) , 데이터를 순차적으로 누적 저장.
        Files.write(path, request, StandardOpenOption.APPEND);

        dataSequenceService.decrementDataSequence(result.getLeft(), sid, sn, LocalDateTime.now());


        //memo: fnum 을 업데이트하기 위해서 가져옴.


//        String fnum = requestSendDataJdbcRepository.getFnumOfReceivingSensorBySnAndSid(fname, sn, sid);

        //memo: fnum 을 0으로 업데이트
//        requestSendDataJdbcRepository.updateFnum(fname,sn,sid);

//        int count = Integer.parseInt(fnum);

//        log.info(fnum);

//        if (count == 1) {
//            dataUpdateRepository.updateCompleteTime(fname, sid, sn);
//        } else {
//            dataUpdateRepository.decrementFnum(fname, sid, sn, count);
//        }


    }
}
