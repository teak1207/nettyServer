package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.PreInstallSensorListAllModel;
import com.devgong.nettyserver.repository.DataSensorListAllRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class DataSensorListService {

    private final DataSensorListAllRepository dataSensorListAllRepository;

    public boolean findDataExistence(String flag, String serialNumber) throws IllegalAccessException {

        PreInstallSensorListAllModel dataSensorListAllModel = null;
        boolean test = true;

        if (flag.equals("7")) {

            dataSensorListAllModel = dataSensorListAllRepository.findPreInstallSensorListAllModelBySsn(serialNumber);
            System.out.println(dataSensorListAllModel.toString());


            if (dataSensorListAllModel.getRegdate() == null || dataSensorListAllModel.getRegdate().equals("")) {
                System.out.println("[feat] : regdate 값이 없습니다");
                test = false;
            } else if (dataSensorListAllModel.getSsn() == null || dataSensorListAllModel.getSsn().equals("")) {
                System.out.println("[feat] : serialnum 값이 없습니다");
                test = false;
            } else if (dataSensorListAllModel.getAsid() == null || dataSensorListAllModel.getAsid().equals("")) {
                System.out.println("[feat] : sid 값이 없습니다");
                test = false;
            } else if (dataSensorListAllModel.getAproject() == null || dataSensorListAllModel.getAproject().equals("")) {
                System.out.println("[feat] : aproject 값이 없습니다");
                test = false;
            } else if (dataSensorListAllModel.getMphone() == null || dataSensorListAllModel.getMphone().equals("")) {
                System.out.println("[feat] : phone 값이 없습니다");
                test = false;
            } else if (dataSensorListAllModel.getFreset() == null || dataSensorListAllModel.getFreset().equals("")) {
                System.out.println("[feat] : F-Reset 값이 없습니다");
                test = false;
            }
        }
        return test;
    }

    public boolean insertUniqueInformation(){

        return true;
    }


}
