package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.DataRefModel;
import com.devgong.nettyserver.protocol.NewPacket;
import com.devgong.nettyserver.protocol.data.DataRequest;
import com.devgong.nettyserver.repository.DataUpdateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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


    public void saveData(String request) throws IOException {

        DataRefModel dataRefModel = requestSensorListService.dataRefModel;

        log.info("dataRefModel.getFilepath : {}", dataRefModel.getFilepath());

        log.info("data test : {}", dataRefModel.getFilepath());
        File file = new File(dataRefModel.getFilepath());
        log.info("getFilePath check : {}", dataRefModel.getFilepath());

        //memo : data(String) to byte[] 변환
//        dataArray = request.getBytes();
//        log.info("dataArray : {}", dataArray);
//        log.info("dataArray : {}", dataArray.length);


        //memo : byte[] 을 .dat 파일에 저장

        //memo 방법1 : 파일의 사이즈 맞음. 하지만 그래프가 이상함
        fileWriter = new FileWriter(file, true);
        fileWriter.write(request);
        fileWriter.flush();


        //memo 방법2 : 파일의 사이즈가 1024가 되버림.
//        log.info("data check : {}", dataArray);
//        Path path = Paths.get(dataRefModel.getFilepath());
//        Files.write(path, test);
        i += 1;
        log.info("iii :{}", i);
        log.info("-----------");
    }
}
