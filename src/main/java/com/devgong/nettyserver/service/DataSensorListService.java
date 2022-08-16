package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.DataInsertModel;
import com.devgong.nettyserver.domain.PreInstallSensorListAllModel;
import com.devgong.nettyserver.repository.DataSensorListAllRepository;
import com.devgong.nettyserver.repository.DataSensorReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class DataSensorListService {

    private final DataSensorListAllRepository dataSensorListAllRepository;
    private final DataSensorReportRepository dataSensorReportRepository;
    PreInstallSensorListAllModel dataSensorListAllModel = null;

    public PreInstallSensorListAllModel findDataExistence(String flag, String serialNumber) throws IllegalAccessException {

        if (flag.equals("7")) {

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
        }
        return dataSensorListAllModel;
    }


    public boolean insertUniqueInformation(DataInsertModel dataInsertModel) {

        if (dataInsertModel != null) {
            dataSensorReportRepository.save(dataInsertModel);
            System.out.println("[INSERT] :Data Sensor report/" + dataSensorListAllModel.getAproject() + "/" + dataSensorListAllModel.getSsn() + "/SUCCESS ");
        } else {
            System.out.println("[INSERT] : FAIL");
            return false;
        }

    return true;
    }


}
