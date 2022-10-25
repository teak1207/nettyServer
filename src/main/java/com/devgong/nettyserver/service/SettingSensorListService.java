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

        //seq : sensorListAll 에서 serialNumber 해당하는 값을 탐색 후,sensorListAllModel 이라는 객체에 담음.
        sensorListAllModel = settingSensorListAllRepository.findPreInstallModelBySsn(serialNumber);

        //seq : sensor_list 에서 Asid, Aproject 해당하는 값을 탐색 후,sensorListModel 이라는 객체에 담음.
        SettingSensorListModel sensorListModel = settingSensorListRepository.findBySidAndPnameAndSerialNumber(sensorListAllModel.getAsid(), sensorListAllModel.getAproject(),sensorListAllModel.getSsn());

        //seq : leakset 에서 Asid, Aproject,fReset 해당하는 값을 탐색 후,leakSetModel 이라는 객체에 담음.
        SettingLeaksetModel leakSetModel = settingLeaksetRepository.findTop1BySidAndPnameAndSnOrderByCidDesc(sensorListAllModel.getAsid(), sensorListAllModel.getAproject(), sensorListAllModel.getSsn());

        //seq : factory_sensor_list 에서  Asid, Aproject,Ssn 해당하는 값을 탐색 후,factorySensorListModel 이라는 객체에 담음.
        SettingFactorySensorListModel factorySensorListModel = settingFactorySensorListRepository.findAllBySidAndPnameAndSn(sensorListAllModel.getAsid(), sensorListAllModel.getAproject(), sensorListAllModel.getSsn());

        //seq : factorySensorListModel 존재 유무에 따른 참조테이블이 달라 분기 처리
        if (Objects.isNull(factorySensorListModel)) {

            //seq : leak_project 에서  Asid, Aproject 해당하는 값을 탐색 후,leakProjectModel 이라는 객체에 담음.
            leakProjectModel = settingLeakProjectRepository.findAllBySidAndFactorypPname(sensorListAllModel.getAsid(), sensorListAllModel.getAproject());

        } else {
            //seq : factory_leak_project 에서  factory_pname 해당하는 값을 탐색 후,factoryLeakProjectModel 이라는 객체에 담음.
            factoryLeakProjectModel = settingFactoryLeakprojectRepository.findAllByFactoryPname(factorySensorListModel.getFactorypname());

        }

        //seq : 리턴해줄 settingResponse에 값을 담아줌.
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
        //seq : factorySensorListModel 존재 유무에 따른 참조테이블이 달라 분기 처리
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
