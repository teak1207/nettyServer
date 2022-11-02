package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.DataRefModel;
import com.devgong.nettyserver.repository.DataUpdateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@RequiredArgsConstructor
@Service
@Slf4j
public class DataService {

    int i = 0;

    private final RequestSensorListService requestSensorListService;
    private final DataUpdateRepository dataUpdateRepository;


    public boolean updateData(String fname, String sid, String sn) {
        return dataUpdateRepository.updateCompleteTime(fname, sid, sn);
    }


    public void saveData(byte[] request) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataRefModel dataRefModel = requestSensorListService.dataRefModel;
        log.info("getFilePath check : {}", dataRefModel.getFilepath());
        log.info("request length check : {}", request.length);


        //memo 1 : request byte[] 을 temp 에 누적해서 저장


        //memo 2 : outputStream.write 할때 temp 를 읽어줌
        for (int i = 0; i <= 96; i++) {
            outputStream.write(request);
        }
        Path path = Paths.get(dataRefModel.getFilepath());
//        outputStream.write(request);

        //memo 3 : Files.write(path, outputStream.toByteArray());
        Files.write(path, outputStream.toByteArray());


        i += 1;
        log.info("iii :{}", i);
        log.info("-----------");
    }


}
