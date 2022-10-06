package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SettingSensorListService {

    private final SettingSensorListAllRepository settingSensorListAllRepository;
    private final SettingSensorListRepository settingSensorListRepository;
    private final SettingLeaksetRepository settingLeaksetRepository;
    private final SettingFactorySensorListRepository settingFactorySensorListRepository;
    private final SettingFactoryLeakprojectRepository settingFactoryLeakprojectRepository;
    private final SettingLeakProjectRepository settingLeakProjectRepository;

    public SettingResponseModel settingRequestData(String serialNumber) {

        //memo : 마지막에 response 하기 위한 객체
        SettingResponseModel SettingResponse = new SettingResponseModel();

        SettingSensorListAllModel settingSensorListAllModel;
        SettingLeakProjectModel settingLeakProjectModel = null;
        SettingFactoryLeakprojectModel settingFactoryLeakprojectModel = null;

        //memo : sensorListAll 에서 serialNumber 값으로 찾아옴
        settingSensorListAllModel = settingSensorListAllRepository.findPreInstallModelBySsn(serialNumber);

        log.info("sensor list All check : {}", settingSensorListAllModel);

        SettingSensorListModel settingSensorListModel = settingSensorListRepository.findAllBySidAndPname(settingSensorListAllModel.getAsid(), settingSensorListAllModel.getAproject());
        SettingLeaksetModel settingLeaksetModel = settingLeaksetRepository.findAllBySidAndPnameAndReset(settingSensorListAllModel.getAsid(), settingSensorListAllModel.getAproject(), settingSensorListAllModel.getFreset());

        SettingFactorySensorListModel settingFactorySensorListModel = settingFactorySensorListRepository.findAllBySidAndPnameAndSn(settingSensorListAllModel.getAsid(), settingSensorListAllModel.getAproject(), settingSensorListAllModel.getSsn());


        log.info("settingFactorySensorListModel : {}", settingFactorySensorListModel);
        //check : if 조건문제1
//        if (settingFactorySensorListModel == null) {
        if (true) {

//                System.out.println("[leak_project URL/PORT 정보를 가져옵니다]");
            log.info("1 : {}", 1);
            settingLeakProjectModel = settingLeakProjectRepository.findAllBySidAndFactorypPname(settingSensorListAllModel.getAsid(), settingSensorListAllModel.getAproject());
            log.info("settingLeakProjectModel : {}", settingLeakProjectModel);


        } else {
            log.info("2 : {}", 2);
            settingFactoryLeakprojectModel = settingFactoryLeakprojectRepository.findAllByFactoryPname(settingFactorySensorListModel.getFactorypname());
            log.info("settingFactoryLeakprojectModel : {}", settingFactoryLeakprojectModel);


        }



        SettingResponse.setTime1(settingLeaksetModel.getTime1());
        SettingResponse.setTime2(settingLeaksetModel.getTime2());
        SettingResponse.setTime3(settingLeaksetModel.getTime3());
        SettingResponse.setFmFrequency(settingLeaksetModel.getFmFrequency());
        SettingResponse.setSid(settingSensorListAllModel.getAsid());
        SettingResponse.setPname(settingSensorListAllModel.getAproject());
        SettingResponse.setSleep(settingLeaksetModel.getSleep());
        SettingResponse.setReset(settingLeaksetModel.getReset());
        SettingResponse.setPeriod(settingLeaksetModel.getPeriod());
        SettingResponse.setSamplingTime(settingLeaksetModel.getSampletime());
        SettingResponse.setFReset(settingSensorListAllModel.getFreset());
        SettingResponse.setPx(settingSensorListModel.getPx());
        SettingResponse.setPy(settingSensorListModel.getPy());
        SettingResponse.setActive(settingLeaksetModel.getActive());
        SettingResponse.setSampleRate(settingLeaksetModel.getSamplerate());
        SettingResponse.setRadioTime(settingLeaksetModel.getFmtime());
        //check : if 조건문제2
        if (true) {

            SettingResponse.setServerUrl(settingLeakProjectModel.getData_URL());
            SettingResponse.setServerPort(settingLeakProjectModel.getData_PORT());
            SettingResponse.setDbUrl(settingLeakProjectModel.getDb_URL());
            SettingResponse.setDbPort(settingLeakProjectModel.getDb_PORT());

        } else {
            SettingResponse.setServerUrl(settingFactoryLeakprojectModel.getDataURL());
            SettingResponse.setServerPort(settingFactoryLeakprojectModel.getDataPORT());
            SettingResponse.setDbUrl(settingFactoryLeakprojectModel.getDbURL());
            SettingResponse.setDbPort(settingFactoryLeakprojectModel.getDbPORT());
        }
        return SettingResponse;
    }
}
