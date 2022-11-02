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
    byte[] dataArray;
    FileWriter fileWriter = null;

    private final RequestSensorListService requestSensorListService;
    private final DataUpdateRepository dataUpdateRepository;

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public boolean updateData(String fname, String sid, String sn) {
        return dataUpdateRepository.updateCompleteTime(fname, sid, sn);
    }


    public void saveData(byte[] request) throws IOException {

        DataRefModel dataRefModel = requestSensorListService.dataRefModel;

        log.info("dataRefModel.getFilepath : {}", dataRefModel.getFilepath());

        log.info("data test : {}", dataRefModel.getFilepath());
        File file = new File(dataRefModel.getFilepath());
        log.info("getFilePath check : {}", dataRefModel.getFilepath());

        //memo : data(String) to byte[] 변환
//        dataArray = request.getBytes();

        //데이터를 찍어보겠음
//        log.info("data request 1: {}", request);
//        log.info("data request 2: {}", request.length());
//        log.info("===================================");
        // 길이가 900~1024 로 찍힘.
//        log.info("dataArray 1: {}", dataArray);
//        log.info("dataArray 2: {}", dataArray.length);
//        log.info("===================================");

        //memo : byte[] 을 .dat 파일에 저장
        //memo 방법1 : 파일의 사이즈 맞음. 하지만 그래프가 이상함
//        fileWriter = new FileWriter(file, true);
//        fileWriter.write(request);
//        fileWriter.flush();
        //memo 방법2 : 파일의 사이즈가 1024가 되버림.
//        writeToFile(dataRefModel.getFilepath(), request);
        //memo 방법3 : 파일의 사이즈가 1024가 되버림.->1024 가 되는게 아니라 마지막 바이트만 저장됨.
//        log.info("data check : {}", dataArray);
//        Path path = Paths.get(dataRefModel.getFilepath());
//        Files.write(path, test);
        //memo 방법4 : 파일의 사이즈가 1024 가 되버림
//        dataArray = request;
        Path path = Paths.get(dataRefModel.getFilepath());
        //memo  : 들어오는 데이터를 ByteArrayOutputStream 에 계속 쌓음.

//        outputStream.write(request);
        byte[] result = new byte[100];
        System.arraycopy(request,0,result,0,request.length);
        // memo : 들어온 데이터를 파일에다가 저장.
//        Files.write(path,outputStream.toByteArray());
        Files.write(path,result);


        i += 1;
        log.info("iii :{}", i);
        log.info("-----------");
    }

    public void writeToFile(String filename, byte[] pData) {

        if (pData == null) {
            return;
        }
        int lByteArraySize = pData.length;
        log.info("lByteArraySize : {}", lByteArraySize);
        try {

            File lOutFile = new File(filename);

            FileOutputStream lFileOutputStream = new FileOutputStream(lOutFile);

            lFileOutputStream.write(pData);

            lFileOutputStream.close();

        } catch (Throwable e) {

            e.printStackTrace(System.out);

        }

    }

}
