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

        return dataSensorListAllModel;
    }


    public boolean insertUniqueInformation(DataInsertModel dataInsertModel, String sid, String project, String serialNumber, Packet<ReportRequest> request) {

        if (dataInsertModel != null) {

//            dataSensorReportRepository.save(dataInsertModel, sid, project, serialNumber);
            log.info("[INSERT SUCCESS ] : SENSOR_REPORT_(SID)_(SN) 테이블을 확인해주세요");

        } else {
            log.info("[INSERT FAIL] :  SENSOR_REPORT_(SID)_(SN) 추가 X.");
            return false;
        }

        return true;
    }


}
