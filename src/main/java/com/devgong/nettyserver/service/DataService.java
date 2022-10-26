package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.DataRefModel;
import com.devgong.nettyserver.protocol.NewPacket;
import com.devgong.nettyserver.protocol.data.DataRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Service
@Slf4j
public class DataService {


    private final RequestSensorListService requestSensorListService;

    public void saveData(NewPacket<DataRequest> request) throws IOException {
        int i = 1;
        DataRefModel dataRefModel = requestSensorListService.dataRefModel;

        log.info("data test : {}", dataRefModel.getFilepath());
        File file = new File(dataRefModel.getFilepath());
        log.info("getFilePath check : {}", dataRefModel.getFilepath());

        //memo : data to byte[] 변환
        byte[] test = request.getParameter().getData().getBytes();

/*        FileWriter fileWriter = null;
        fileWriter = new FileWriter(file, true);
        fileWriter.write(request.getParameter().getData());
        fileWriter.flush();*/

        //memo : byte[] 을 .dat 파일에 저장
//        log.info("data check : {}", test);
        Path path = Paths.get(dataRefModel.getFilepath());
        Files.write(path, test);
        i++;
        log.info("iii :{}", i);
    }
}
