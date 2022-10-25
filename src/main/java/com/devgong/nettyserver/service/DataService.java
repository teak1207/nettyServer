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

@RequiredArgsConstructor
@Service
@Slf4j
public class DataService {


    private final RequestSensorListService requestSensorListService;

    public void saveData(NewPacket<DataRequest> request) throws IOException {

        DataRefModel dataRefModel = requestSensorListService.dataRefModel;

        log.info("data test : {}", dataRefModel.getFilepath());


        File file = new File(dataRefModel.getFilepath());
        FileWriter fileWriter = null;
        fileWriter = new FileWriter(file, true);
        log.info("data check : {}", request.getParameter().getData());
        fileWriter.write(request.getParameter().getData());
        fileWriter.flush();

    }
}
