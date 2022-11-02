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

        ByteArrayInputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        DataRefModel dataRefModel = requestSensorListService.dataRefModel;

        log.info("getFilePath check : {}", dataRefModel.getFilepath());

        byte[] temp = new byte[request.length];
        int readCount = 0;
//        File file = new File(dataRefModel.getFilepath());

        inputStream.read(temp,0, request.length);
        outputStream.write(temp,0,temp.length);

//        outputStream.write(request, 0, readCount);


        //memo 방법4 : 파일의 사이즈가 1024 가 되버림
        Path path = Paths.get(dataRefModel.getFilepath());
        //memo  : 들어오는 데이터를 ByteArrayOutputStream 에 계속 쌓음.
//        outputStream.write(request);

        // memo : 들어온 데이터를 파일에다가 저장.
        Files.write(path, outputStream.toByteArray());


        i += 1;
        log.info("iii :{}", i);
        log.info("-----------");
    }


}
