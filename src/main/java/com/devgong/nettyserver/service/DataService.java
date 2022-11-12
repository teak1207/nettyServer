package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.PreInstallSensorListAllModel;
import com.devgong.nettyserver.repository.DataUpdateRepository;
import com.devgong.nettyserver.repository.PreInstallSensorListAllRepository;
import com.devgong.nettyserver.repository.RequestSendDataJdbcRepository;
import com.devgong.nettyserver.repository.RequestSendDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Service
@Slf4j
public class DataService {

    private final PreInstallSensorListAllRepository preInstallSensorListAllRepository;
    private final RequestSensorListService requestSensorListService;
    private final DataUpdateRepository dataUpdateRepository;
    private final RequestSendDataJdbcRepository requestSendDataJdbcRepository;

//    public boolean updateData(String fname, String sid, String sn) {
//        return dataUpdateRepository.updateCompleteTime(fname, sid, sn);
//    }


    public void saveData(String sn, String sid, byte[] request) throws IOException {
//        int i = 0;

//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PreInstallSensorListAllModel sensorListAllModel;

//        String filePath = requestSensorListService.referenceFilePath;
        //순서 : sn 로  sensor_list_all 가서 sid  값을 가져온다.
        sensorListAllModel = preInstallSensorListAllRepository.findPreInstallModelBySsn(sn);
//        log.info("sensorListAllModel check : {}",sensorListAllModel);
        //순서 : leak_send_data_(sid)_(sn)에서 fname 을 가져온다.
        String fname = requestSendDataJdbcRepository.selectBySnAndSid(sensorListAllModel.getSsn(), sensorListAllModel.getAsid());
        log.info("fname check : {}", fname);

        //순서 : fname을 가자고 filePath 로 활용한다.


        //memo 1 : request byte[] 을 temp 에 누적해서 저장


        //memo 2 : outputStream.write 할때 temp 를 읽어줌
//        outputStream.write(request);


        Path path = Paths.get(fname);
//        outputStream.write(request);

        //memo 3 : Files.write(path, outputStream.toByteArray());
        Files.write(path, request);

        String fnum = requestSendDataJdbcRepository.getFnumOfReceivingSensorBySnAndSid(fname, sn, sid);

        int count = Integer.parseInt(fnum);
        if (count == 1) {
            dataUpdateRepository.updateCompleteTime(fname, sid, sn);
        } else {
            dataUpdateRepository.decrementFnum(fname, sid, sn, count);
        }


//        i += 1;
//        log.info("iii :{}", i);
//        log.info("-----------");
    }


}
