package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.DataRefModel;
import com.devgong.nettyserver.domain.RequestListAllModel;
import com.devgong.nettyserver.protocol.NewPacket;
import com.devgong.nettyserver.protocol.request.ReqRequest;
import com.devgong.nettyserver.repository.RequestSensorListAllRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class RequestSensorListService {


    RequestListAllModel requestListAllModel = null;
    DataRefModel dataRefModel = new DataRefModel();

    private final RequestSensorListAllRepository requestSensorListAllRepository;

    public RequestListAllModel findDataExistence(String serialNumber) {

        requestListAllModel = requestSensorListAllRepository.findAllBySsn(serialNumber);

        log.info("requestListAllModel : {}", requestListAllModel);

        return requestListAllModel;
    }

    public boolean confirmPath(RequestListAllModel requestFindResults, NewPacket<ReqRequest> request) throws UnsupportedEncodingException {

        String convertedSampleRate;
//        log.info("pathchk3 : {}", Integer.valueOf(getStringToHex(request.getParameter().getFrameCount()), 16));
//        log.info("pathchk3 : {}", Integer.valueOf(getStringToHex(request.getParameter().getDataSize()), 16));
//        log.info("pathchk3 : {}", Integer.valueOf(getStringToHex(request.getParameter().getSampleRate()), 16));

        if (request.getParameter().getSampleRate().length() == 1) {
            convertedSampleRate = "00" + Integer.valueOf(getStringToHex(request.getParameter().getSampleRate()), 16);
            log.info(convertedSampleRate);
        } else {
            convertedSampleRate = "0" + Integer.valueOf(getStringToHex(request.getParameter().getSampleRate()), 16);
            log.info(convertedSampleRate);
        }


        if (requestFindResults.getAsid().isBlank() && requestFindResults.getAproject().isBlank() && requestFindResults.getSsn().isBlank()) {
            log.info("[FAIL] : SENSOR_LIST_ALL 테이블에 값이 존재하질 않습니다");

        } else {
            String defaultPath = "/home/scsol/public_html/leak_data_gong/";
            String path = defaultPath + requestFindResults.getAsid() + "/" + requestFindResults.getAproject() + "/" + requestFindResults.getSsn();

            log.info("path : {}", path);

            char underBar = '_';


            String filePath = path + "/" + requestFindResults.getSsn() + underBar + convertDate(request.getDateTime()) + underBar + convertedSampleRate + ".dat";
            log.info("filePath : {}", filePath);

            File initFilePath = new File(filePath);
            File file3 = new File(path);
            Path filePathExistence = Paths.get(filePath);
            dataRefModel.setFilepath(filePath);
//            log.info("dataRefModel: {}", dataRefModel.getFilepath());

            if (file3.isDirectory()) {

                //memo : 해당 경로에 파일 존재할 경우, NAK return
                log.info("경로가 존재합니다. : {}", filePath);

                if (Files.exists(filePathExistence)) {
                    log.info("해당파일이 존재합니다. : {}", filePathExistence);
                } else {

                    try {
                        if (initFilePath.createNewFile()) {
                            log.info("파일 생성 완료 : {} ", filePath);
                            return true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                //memo : 해당 경로에 파일이 없을 경우,File 생성후, ACK return
                log.info("경로가 존재하지 않습니다. : {}", filePath);
            }
        }
        return false;
    }

    public String convertDate(LocalDateTime date) {

        String convertValue = date.toString();

        convertValue = convertValue.replace("-", "");
        convertValue = convertValue.replace(":", "");
        convertValue = convertValue.replace("T", "_");


        return convertValue;
    }


    public String getStringToHex(String test) throws UnsupportedEncodingException {
        byte[] testBytes = test.getBytes("utf-8");
        return DatatypeConverter.printHexBinary(testBytes);
    }
}
