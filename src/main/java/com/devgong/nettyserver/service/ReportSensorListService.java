package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.DataInsertModel;
import com.devgong.nettyserver.domain.PreInstallSensorListAllModel;
import com.devgong.nettyserver.protocol.Packet;
import com.devgong.nettyserver.protocol.Report.ReportRequest;
import com.devgong.nettyserver.repository.DataSensorListAllRepository;
import com.devgong.nettyserver.repository.DataSensorReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class ReportSensorListService {

    private final DataSensorListAllRepository dataSensorListAllRepository;

    private final DataSensorReportRepository dataSensorReportRepository;

    PreInstallSensorListAllModel dataSensorListAllModel = null;


    public PreInstallSensorListAllModel findDataExistence(String serialNumber) throws IllegalAccessException {

        dataSensorListAllModel = dataSensorListAllRepository.findPreInstallSensorListAllModelBySsn(serialNumber);

        if (dataSensorListAllModel.getRegdate() == null || dataSensorListAllModel.getRegdate().equals("")) {

            log.info("[feat] : regdate 값이 없습니다");
        } else if (dataSensorListAllModel.getSsn() == null || dataSensorListAllModel.getSsn().equals("")) {
            log.info("[feat] : serialnum 값이 없습니다");
        } else if (dataSensorListAllModel.getAsid() == null || dataSensorListAllModel.getAsid().equals("")) {
            log.info("[feat] : sid 값이 없습니다");
        } else if (dataSensorListAllModel.getAproject() == null || dataSensorListAllModel.getAproject().equals("")) {
            log.info("[feat] : aproject 값이 없습니다");
        } else if (dataSensorListAllModel.getMphone() == null || dataSensorListAllModel.getMphone().equals("")) {
            log.info("[feat] : phone 값이 없습니다");
        } else if (dataSensorListAllModel.getFreset() == null || dataSensorListAllModel.getFreset().equals("")) {
            log.info("[feat] : F-Reset 값이 없습니다");
        }

        log.info("dataSensorListAllModel : {}", dataSensorListAllModel);


        return dataSensorListAllModel;
    }

    public String convertData(char value) {

        //memo : char --> hex
        String convertedHex = Integer.toHexString(value);
        //memo : hex --> decimal
        int convertDecimal = Integer.parseInt(convertedHex, 16);

        return Integer.toString(convertDecimal);
    }


    public boolean insertUniqueInformation(DataInsertModel dataInsertModel, String sid, String project, String serialNumber, Packet<ReportRequest> request) {


        log.info("py : {}", request.getParameter().getPy());
        log.info("py : {}", request.getParameter().getPy().trim());
        log.info("py : {}", request.getParameter().getPy().trim().length());
        log.info("py : {}", request.getParameter().getPy().substring(0,9).length());

        dataInsertModel.setSn(request.getSensorId());
        dataInsertModel.setEndRecordTime(request.getParameter().getEndRecordTime());
        dataInsertModel.setRecordTime1(request.getParameter().getRecordTime1());
        dataInsertModel.setRecordTime2(request.getParameter().getRecordTime2());
        dataInsertModel.setRecordTime3(request.getParameter().getRecordTime3());
        dataInsertModel.setFmRadio(request.getParameter().getFmRadio());
        dataInsertModel.setFirmwareVersion(request.getParameter().getFirmWareVersion());
        dataInsertModel.setBatteryValue(request.getParameter().getBatteryValue());
        dataInsertModel.setModernRssi(convertData(request.getParameter().getRssi().charAt(0)));
        dataInsertModel.setDeviceStatus(request.getParameter().getDeviceStatus());
        dataInsertModel.setSamplingTime(convertData(request.getParameter().getSamplingTime().charAt(0)));
        dataInsertModel.setPx(request.getParameter().getPx());
        dataInsertModel.setPy(request.getParameter().getPy().substring(0,9));
        dataInsertModel.setPname(request.getParameter().getPname());
        dataInsertModel.setSid(request.getParameter().getSid());
        dataInsertModel.setPeriod(convertData(request.getParameter().getPeriod().charAt(0)));
        dataInsertModel.setServerUrl(request.getParameter().getServerUrl());
        dataInsertModel.setServerPort(request.getParameter().getServerPort());
        dataInsertModel.setDbUrl(request.getParameter().getDbUrl());
        dataInsertModel.setDbPort(request.getParameter().getDbPort());
        dataInsertModel.setSleep(convertData(request.getParameter().getSleep().charAt(0)));
        dataInsertModel.setActive(convertData(request.getParameter().getActive().charAt(0)));
        dataInsertModel.setFReset(convertData(request.getParameter().getFReset().charAt(0)));
        dataInsertModel.setReset(convertData(request.getParameter().getReset().charAt(0)));
        dataInsertModel.setSampleRate(convertData(request.getParameter().getSamplerate().charAt(0)));
        dataInsertModel.setRadioTime(convertData(request.getParameter().getRadioTime().charAt(0)));
        dataInsertModel.setCregCount(convertData(request.getParameter().getCregCount().charAt(0)));


        if (dataInsertModel != null) {

            log.info("dataInsertModel check final : {} ", dataInsertModel);

            dataSensorReportRepository.save(dataInsertModel, sid, project, serialNumber);
            log.info("[INSERT SUCCESS ] : SENSOR_REPORT_(SID)_(SN) 테이블을 확인해주세요");

        } else {
            log.info("[INSERT FAIL] :  SENSOR_REPORT_(SID)_(SN) 추가 X.");
            return false;
        }

        return true;
    }


}
