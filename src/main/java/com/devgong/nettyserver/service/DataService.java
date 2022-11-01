package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.DataRefModel;
import com.devgong.nettyserver.repository.DataUpdateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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

        //memo 방법3 : 파일의 사이즈가 1024가 되버림.
//        log.info("data check : {}", dataArray);
//        Path path = Paths.get(dataRefModel.getFilepath());
//        Files.write(path, test);

        //memo 방법4 : 파일의 사이즈가 1024 가 되버림
        dataArray = request;
        Path path = Paths.get(dataRefModel.getFilepath());
        Files.write(path,dataArray);


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
