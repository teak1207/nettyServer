package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.DataInsertModel;
import com.devgong.nettyserver.domain.PreInstallSensorListAllModel;
import com.devgong.nettyserver.protocol.Packet;
import com.devgong.nettyserver.protocol.Report.ReportRequest;
import com.devgong.nettyserver.repository.DataSensorListAllRepository;
import com.devgong.nettyserver.repository.DataSensorReportRepository;
import com.sun.xml.bind.v2.runtime.reflect.Lister;
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
            System.out.println("[feat] : regdate 값이 없습니다");
        } else if (dataSensorListAllModel.getSsn() == null || dataSensorListAllModel.getSsn().equals("")) {
            System.out.println("[feat] : serialnum 값이 없습니다");
        } else if (dataSensorListAllModel.getAsid() == null || dataSensorListAllModel.getAsid().equals("")) {
            System.out.println("[feat] : sid 값이 없습니다");
        } else if (dataSensorListAllModel.getAproject() == null || dataSensorListAllModel.getAproject().equals("")) {
            System.out.println("[feat] : aproject 값이 없습니다");
        } else if (dataSensorListAllModel.getMphone() == null || dataSensorListAllModel.getMphone().equals("")) {
            System.out.println("[feat] : phone 값이 없습니다");
        } else if (dataSensorListAllModel.getFreset() == null || dataSensorListAllModel.getFreset().equals("")) {
            System.out.println("[feat] : F-Reset 값이 없습니다");
        }

        log.info("dataSensorListAllModel222 : {}", dataSensorListAllModel);


        return dataSensorListAllModel;
    }


    public boolean insertUniqueInformation(DataInsertModel dataInsertModel, String sid, String project, String serialNumber, Packet<ReportRequest> request) {



        char c = request.getParameter().getRssi().charAt(0);

        log.info("request check : {}", request);
        log.info("fuck : {}", (request.getParameter().getRssi().charAt(0)));
        log.info("fuck : {}", Integer.toHexString(request.getParameter().getRssi().charAt(0)));







        dataInsertModel.setSn(request.getSensorId());
        dataInsertModel.setEndRecordTime(request.getParameter().getEndRecordTime());
        dataInsertModel.setRecordTime1(request.getParameter().getRecordTime1());
        dataInsertModel.setRecordTime2(request.getParameter().getRecordTime2());
        dataInsertModel.setRecordTime3(request.getParameter().getRecordTime3());
        dataInsertModel.setFmRadio(request.getParameter().getFmRadio());
        dataInsertModel.setFirmwareVersion(request.getParameter().getFirmWareVersion());
        dataInsertModel.setBatteryValue(request.getParameter().getBatteryValue());
        dataInsertModel.setModernRssi(request.getParameter().getRssi());
        dataInsertModel.setDeviceStatus(request.getParameter().getDeviceStatus());
        dataInsertModel.setSamplingTime(request.getParameter().getSamplingTime());
        dataInsertModel.setPx(request.getParameter().getPx());
        dataInsertModel.setPy(request.getParameter().getPy());
        dataInsertModel.setPname(request.getParameter().getPname());
        dataInsertModel.setSid(request.getParameter().getSid());
        dataInsertModel.setPeriod(request.getParameter().getPeriod());
        dataInsertModel.setServerUrl(request.getParameter().getServerUrl());
        dataInsertModel.setServerPort(request.getParameter().getServerPort());
        dataInsertModel.setDbUrl(request.getParameter().getDbUrl());
        dataInsertModel.setDbPort(request.getParameter().getDbPort());
        dataInsertModel.setSleep(request.getParameter().getSleep());
        dataInsertModel.setActive(request.getParameter().getActive());
        dataInsertModel.setFReset(request.getParameter().getFReset());
        dataInsertModel.setReset(request.getParameter().getReset());
        dataInsertModel.setSampleRate(request.getParameter().getSamplerate());
        dataInsertModel.setRadioTime(request.getParameter().getRadioTime());
        dataInsertModel.setCregCount(request.getParameter().getCregCount());


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
