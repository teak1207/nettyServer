package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.DataLeakSendDataModel;
import com.devgong.nettyserver.domain.DataRefModel;
import com.devgong.nettyserver.domain.RequestLeakDataModel;
import com.devgong.nettyserver.domain.RequestListAllModel;
import com.devgong.nettyserver.protocol.NewPacket;
import com.devgong.nettyserver.protocol.request.ReqRequest;
import com.devgong.nettyserver.repository.RequestSendDataRepository;
import com.devgong.nettyserver.repository.RequestSensorListAllRepository;
import com.devgong.nettyserver.repository.TestRepository;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@RequiredArgsConstructor
@Service
@Slf4j
public class RequestSensorListService {


    RequestListAllModel requestListAllModel = null;

    private final RequestSensorListAllRepository requestSensorListAllRepository;
    private final RequestSendDataRepository requestSendDataRepository;
    private final TestRepository testRepository;
    static String defaultPath = "/home/scsol/public_html/leak_data_gong/";
    char underBar = '_';


    public RequestListAllModel findDataExistence(String serialNumber) {
        requestListAllModel = requestSensorListAllRepository.findAllBySsn(serialNumber);
        log.info("requestListAllModel : {}", requestListAllModel);
        return requestListAllModel;
    }

    public RequestListAllModel findDataExistence(String serialNumber, String valid, String status) {
        requestListAllModel = requestSensorListAllRepository.findAllBySsnAndStatusIsAndValidNot(serialNumber, status, valid);
        log.info("requestListAllModel : {}", requestListAllModel);
        return requestListAllModel;
    }

    public String findDataFname(String serialNumber, String sid) {


        return testRepository.selectBySnAndSid(serialNumber, sid);
    }


    public void saveData(NewPacket<ReqRequest> request, RequestListAllModel sensorListAll) throws UnsupportedEncodingException {

        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        RequestLeakDataModel requestLeakDataModel = new RequestLeakDataModel();

        String convertedFname = defaultPath + sensorListAll.getAsid() + "/" + sensorListAll.getAproject() + "/" + request.getSensorId() + "/" + request.getSensorId() + underBar + convertDate(request.getDateTime()) + underBar + convertSampleRate(request.getParameter().getSampleRate()) + ".dat";

        requestLeakDataModel.setPname(sensorListAll.getAproject());
        requestLeakDataModel.setDate((simpleDateFormat.format(now)));
        requestLeakDataModel.setId("admin");
        requestLeakDataModel.setIp("-1-1");
        requestLeakDataModel.setSid(sensorListAll.getAsid());
        requestLeakDataModel.setValid("");
        requestLeakDataModel.setRequestTime((simpleDateFormat.format(now)));
        requestLeakDataModel.setFname(convertedFname);
        requestLeakDataModel.setSn(sensorListAll.getSsn());
        requestLeakDataModel.setComplete("");
        requestLeakDataModel.setCompleteTime("");
        requestLeakDataModel.setFnum("");
        requestLeakDataModel.setInference("");

        if (requestSendDataRepository.save(request, requestLeakDataModel)) {
            log.info("leak_send data Insert Success");
        } else {
            log.info("leak_send data Insert fail");
        }

    }


    public boolean confirmPath(RequestListAllModel requestFindResults, NewPacket<ReqRequest> request) throws UnsupportedEncodingException {


        if (requestFindResults.getAsid().isBlank() && requestFindResults.getAproject().isBlank() && requestFindResults.getSsn().isBlank()) {
            log.info("[FAIL] : SENSOR_LIST_ALL 테이블에 값이 존재하질 않습니다");

        } else {
            String path = defaultPath + requestFindResults.getAsid() + "/" + requestFindResults.getAproject() + "/" + requestFindResults.getSsn();

            log.info("path : {}", path);

            String filePath = path + "/" + requestFindResults.getSsn() + underBar + convertDate(request.getDateTime()) + underBar + convertSampleRate(request.getParameter().getSampleRate()) + ".dat";
            log.info("filePath : {}", filePath);

            File initFilePath = new File(filePath);
            File file3 = new File(path);
            Path filePathExistence = Paths.get(filePath);

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

    public String convertSampleRate(String input) throws UnsupportedEncodingException {

        String result;

        if (input.length() == 1) {
            result = "00" + Integer.valueOf(getStringToHex(input), 16);
        } else {
            result = "0" + Integer.valueOf(getStringToHex(input), 16);
        }
        return result;
    }

    public String getStringToHex(String test) throws UnsupportedEncodingException {
        byte[] testBytes = test.getBytes("utf-8");
        return DatatypeConverter.printHexBinary(testBytes);
    }
}
