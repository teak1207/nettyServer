package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
        SettingResponseModel settingResponse = new SettingResponseModel();

        SettingSensorListAllModel sensorListAllModel;
        SettingLeakProjectModel leakProjectModel = null;
        SettingFactoryLeakprojectModel factoryLeakProjectModel = null;

        //memo : sensorListAll 에서 serialNumber 값으로 찾아옴
        sensorListAllModel = settingSensorListAllRepository.findPreInstallModelBySsn(serialNumber);
//        log.info("sensorListAll check : {}", sensorListAllModel);

        log.info("test: {}", sensorListAllModel.getAsid());
        log.info("test: {}", sensorListAllModel.getAproject());

        SettingSensorListModel sensorListModel = settingSensorListRepository.findAllBySidAndPname(sensorListAllModel.getAsid(), sensorListAllModel.getAproject());
//        log.info("sensorList check : {}", sensorListAllModel);


        SettingLeaksetModel leakSetModel = settingLeaksetRepository.findTop1BySidAndPnameAndResetOrderByCidDesc(sensorListAllModel.getAsid(), sensorListAllModel.getAproject(), sensorListAllModel.getFreset());
//        log.info("leakSet check : {}", leakSetModel);

        SettingFactorySensorListModel factorySensorListModel = settingFactorySensorListRepository.findAllBySidAndPnameAndSn(sensorListAllModel.getAsid(), sensorListAllModel.getAproject(), sensorListAllModel.getSsn());
//        log.info("FactorySensorListModel check : {}", factorySensorListModel);


        //check : if 조건문제1
        if (Objects.isNull(factorySensorListModel)) {

            leakProjectModel = settingLeakProjectRepository.findAllBySidAndFactorypPname(sensorListAllModel.getAsid(), sensorListAllModel.getAproject());
//            log.info("leakProjectModel check : {}", leakProjectModel);

            //memo : 여기서  java.lang.NullPointerException 발생함.
        } else {
            factoryLeakProjectModel = settingFactoryLeakprojectRepository.findAllByFactoryPname(factorySensorListModel.getFactorypname());
//            log.info("factoryLeakProjectModel  check: {}", factoryLeakProjectModel);

        }


        settingResponse.setTime1(leakSetModel.getTime1());
        settingResponse.setTime2(leakSetModel.getTime2());
        settingResponse.setTime3(leakSetModel.getTime3());
        settingResponse.setFmRadio(leakSetModel.getFmFrequency());
        settingResponse.setSid(sensorListAllModel.getAsid());
        settingResponse.setPname(sensorListAllModel.getAproject());
        settingResponse.setSleep(leakSetModel.getSleep());
        settingResponse.setReset(leakSetModel.getReset());
        settingResponse.setPeriod(leakSetModel.getPeriod());
        settingResponse.setSamplingTime(leakSetModel.getSampletime());
        settingResponse.setFReset(sensorListAllModel.getFreset());
        settingResponse.setPx(sensorListModel.getPx());
        settingResponse.setPy(sensorListModel.getPy());
        settingResponse.setActive(leakSetModel.getActive());
        settingResponse.setSampleRate(leakSetModel.getSamplerate());
        settingResponse.setRadioTime(leakSetModel.getFmtime());

        log.info("SettingResponse check : {}", settingResponse);

        if (Objects.isNull(factorySensorListModel)) {

            settingResponse.setServerUrl(leakProjectModel.getData_URL());
            settingResponse.setServerPort(leakProjectModel.getData_PORT());
            settingResponse.setDbUrl(leakProjectModel.getDb_URL());
            settingResponse.setDbPort(leakProjectModel.getDb_PORT());

            log.info("settingResponse check1 : {} ", settingResponse);

        } else {
            settingResponse.setServerUrl(factoryLeakProjectModel.getDataURL());
            settingResponse.setServerPort(factoryLeakProjectModel.getDataPORT());
            settingResponse.setDbUrl(factoryLeakProjectModel.getDbURL());
            settingResponse.setDbPort(factoryLeakProjectModel.getDbPORT());
            log.info("settingResponse check2 : {} ", settingResponse);
        }
        return settingResponse;
    }
}
