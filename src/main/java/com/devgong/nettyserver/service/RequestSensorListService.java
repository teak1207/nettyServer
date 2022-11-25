package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.RequestLeakDataModel;
import com.devgong.nettyserver.domain.RequestListAllModel;
import com.devgong.nettyserver.protocol.NewPacket;
import com.devgong.nettyserver.protocol.request.ReqRequest;
import com.devgong.nettyserver.repository.RequestSendDataJdbcRepository;
import com.devgong.nettyserver.repository.RequestSendDataRepository;
import com.devgong.nettyserver.repository.RequestSensorListAllRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
    private final RequestSensorListAllRepository requestSensorListAllRepository;
    private final RequestSendDataRepository requestSendDataRepository;
    private final RequestSendDataJdbcRepository requestSendDataJdbcRepository;

    //danger : 만약 저장경로를 바꾼다하면 이걸 바꿔야하나???
//    static String defaultPath = "/home/scsol/public_html/leak_data_gong/";
    @Value("${sensor.file-path}")
    private String defaultPath;

    char underBar = '_';


    /**
     * @param serialNumber - 디바이스에서 넘겨주는 고유 모뎀 번호
     * @return RequestListAllModel 을 리턴.
     * @author devGong
     * (1) serialNumber 로 find.
     * (2) RequestListAllModel 객체에 담아 리턴.
     */

    public RequestListAllModel findDataExistence(String serialNumber) {
        return requestSensorListAllRepository.findAllBySsn(serialNumber);
    }

    public RequestListAllModel findDataExistence(String serialNumber, String valid, String status) {
        return requestSensorListAllRepository.findAllBySsnAndStatusIsAndValidNot(serialNumber, status, valid);
    }

    public String findDataFname(String serialNumber, String sid) {

        return requestSendDataJdbcRepository.selectBySnAndSid(serialNumber, sid);
    }


    public String saveData(NewPacket<ReqRequest> request, RequestListAllModel sensorListAll) throws UnsupportedEncodingException {

        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        RequestLeakDataModel requestLeakDataModel = new RequestLeakDataModel();

        String convertedFname = String.format("%s%s/%s/%s/%s_%s_%s.dat",
                defaultPath,
                sensorListAll.getAsid(),
                sensorListAll.getAproject(),
                request.getSensorId(),
                request.getSensorId(),
                convertDate(request.getDateTime()),
                convertSampleRate(request.getParameter().getSampleRate()));


        byte[] temp = request.getParameter().getFrameCount().getBytes(StandardCharsets.UTF_8);
        String frameCount = String.valueOf(temp[1]);


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
        requestLeakDataModel.setFnum(frameCount);
        requestLeakDataModel.setInference("");


        //request_seq : leak_send data Insert
        if (requestSendDataRepository.save(request, requestLeakDataModel)) {
            log.info("leak_send data Insert Success");

            return frameCount;
        } else {
            log.info("leak_send data Insert fail");
        }
        return frameCount;
    }


    public boolean confirmPath(RequestListAllModel requestFindResults, NewPacket<ReqRequest> request) throws UnsupportedEncodingException {

        //request_seq : sensor_list_all 테이블에서 가져온 값 체크
        if (requestFindResults.getAsid().isBlank() && requestFindResults.getAproject().isBlank() && requestFindResults.getSsn().isBlank()) {
            log.info("[FAIL] : SENSOR_LIST_ALL 테이블에 값이 존재하질 않습니다");

        } else {
            String path = String.format("%s%s/%s/%s",
                    defaultPath,
                    requestFindResults.getAsid().trim(),
                    requestFindResults.getAproject(),
                    requestFindResults.getSsn());

            log.info("chekc path length : {}", path.length());
            log.info("path : {}", path);

            String filePath = String.format("%s/%s_%s_%s.dat",
                    path,
                    requestFindResults.getSsn(),
                    convertDate(request.getDateTime()),
                    convertSampleRate(request.getParameter().getSampleRate()));

            log.info("filePath : {}", filePath);

            File initFilePath = new File(filePath);
            File file3 = new File(path);
            Path filePathExistence = Paths.get(filePath);

            //request_seq : 선언한 파일이 원하는 경로에 있는지 체크.
            if (file3.isDirectory()) {

                //memo : 해당 경로에 파일 존재할 경우, NAK return
                log.info("경로가 존재합니다. : {}", filePath);

                if (Files.exists(filePathExistence)) {
                    log.info("해당파일이 존재합니다. : {}", filePathExistence);

                    //request_seq : 해당 경로에 파일이 존재하지 않을 경우, 파일을 생성, ACK return
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
