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


    public void saveData(NewPacket<DataRequest> request) throws IOException {

        DataRefModel dataRefModel = requestSensorListService.dataRefModel;

        log.info("dataRefModel.getFilepath : {}", dataRefModel.getFilepath());

        log.info("data test : {}", dataRefModel.getFilepath());
        File file = new File(dataRefModel.getFilepath());
        log.info("getFilePath check : {}", dataRefModel.getFilepath());

        //memo : data to byte[] 변환
        dataArray = request.getParameter().getData().getBytes();

        fileWriter = new FileWriter(file, true);
        fileWriter.write(request.getParameter().getData());
        fileWriter.flush();

        //memo : byte[] 을 .dat 파일에 저장
//        log.info("data check : {}", dataArray);
//        Path path = Paths.get(dataRefModel.getFilepath());
//        Files.write(path, test);
        i += 1;
        log.info("iii :{}", i);
        log.info("-----------");
    }
}
