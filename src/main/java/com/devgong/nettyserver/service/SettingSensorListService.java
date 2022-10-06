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

        SettingSensorListAllModel sensorListAllModel;
        SettingLeakProjectModel settingLeakProjectModel = null;
        SettingFactoryLeakprojectModel settingFactoryLeakprojectModel = null;

        //memo : sensorListAll 에서 serialNumber 값으로 찾아옴
        sensorListAllModel = settingSensorListAllRepository.findPreInstallModelBySsn(serialNumber);

        log.info("sensorListAll check : {}", sensorListAllModel);
        SettingSensorListModel sensorListModel = settingSensorListRepository.findAllBySidAndPname(sensorListAllModel.getAsid(), sensorListAllModel.getAproject());
        log.info("sensorList check : {}", sensorListAllModel);
        SettingLeaksetModel leakSetModel = settingLeaksetRepository.findAllBySidAndPnameAndReset(sensorListAllModel.getAsid(), sensorListAllModel.getAproject(), sensorListAllModel.getFreset());
        log.info("leakSet check : {}", leakSetModel);
        SettingFactorySensorListModel factorySensorListModel = settingFactorySensorListRepository.findAllBySidAndPnameAndSn(sensorListAllModel.getAsid(), sensorListAllModel.getAproject(), sensorListAllModel.getSsn());
        log.info("FactorySensorListModel check : {}", factorySensorListModel);





        //check : if 조건문제1
//        if (settingFactorySensorListModel == null) {
        if (true) {

//                System.out.println("[leak_project URL/PORT 정보를 가져옵니다]");
            log.info("1 : {}", 1);
            settingLeakProjectModel = settingLeakProjectRepository.findAllBySidAndFactorypPname(sensorListAllModel.getAsid(), sensorListAllModel.getAproject());
            log.info("settingLeakProjectModel : {}", settingLeakProjectModel);


        } else {
            log.info("2 : {}", 2);
            settingFactoryLeakprojectModel = settingFactoryLeakprojectRepository.findAllByFactoryPname(factorySensorListModel.getFactorypname());
            log.info("settingFactoryLeakprojectModel : {}", settingFactoryLeakprojectModel);


        }



        SettingResponse.setTime1(leakSetModel.getTime1());
        SettingResponse.setTime2(leakSetModel.getTime2());
        SettingResponse.setTime3(leakSetModel.getTime3());
        SettingResponse.setFmFrequency(leakSetModel.getFmFrequency());
        SettingResponse.setSid(sensorListAllModel.getAsid());
        SettingResponse.setPname(sensorListAllModel.getAproject());
        SettingResponse.setSleep(leakSetModel.getSleep());
        SettingResponse.setReset(leakSetModel.getReset());
        SettingResponse.setPeriod(leakSetModel.getPeriod());
        SettingResponse.setSamplingTime(leakSetModel.getSampletime());
        SettingResponse.setFReset(sensorListAllModel.getFreset());
        SettingResponse.setPx(sensorListModel.getPx());
        SettingResponse.setPy(sensorListModel.getPy());
        SettingResponse.setActive(leakSetModel.getActive());
        SettingResponse.setSampleRate(leakSetModel.getSamplerate());
        SettingResponse.setRadioTime(leakSetModel.getFmtime());
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
