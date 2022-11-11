package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author devGong
 * @version 1.0
 * Setting 에서 관련 테이블들을 참조, Find 메서드기능 구현.
 */

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


    /**
     * @param serialNumber - 디바이스에서 넘겨주는 고유 모뎀 번호
     * @return SettingResponseModel 을 리턴함.
     * @author devGong
     * (1) serialNumber 로 DB에서 해당 값을 가져오고, preInstallSensorListAllModel 리턴합니다.
     * (2) sensorListAllModel sid,project,serialNumber 값으로 sensor_list 테이블 컬럼 가져오고, sensorListModel 리턴.
     * (3) sensorListAllModel sid,project,serialNumber 값으로 leakset 테이블 컬럼을 가져오고, leakSetModel 리턴.
     * (4) sensorListAllModel sid,project,serialNumber 값으로 factory_sensor_list 테이블 컬럼을 가져오고, factorySensorListModel 리턴
     * (5) factorySensorListModel 존재 유무에 따라 leakProjectModel Or factoryLeakProjectModel 리턴.
     * (6) settingResponse 객체를 앞서 참조한 값으로 초기화 후, 리턴.
     */
    public SettingResponseModel settingRequestData(String serialNumber) {

        //memo : 마지막에 response 하기 위한 객체
        SettingResponseModel settingResponse = new SettingResponseModel();
        SettingSensorListAllModel sensorListAllModel;
        SettingLeakProjectModel leakProjectModel = null;
        SettingFactoryLeakprojectModel factoryLeakProjectModel = null;

        //setting_seq : sensorListAll 에서 serialNumber 해당하는 값을 탐색 후,sensorListAllModel 이라는 객체에 담음.
        sensorListAllModel = settingSensorListAllRepository.findPreInstallModelBySsn(serialNumber);

        //setting_seq : sensor_list 에서 Asid, Aproject 해당하는 값을 탐색 후,sensorListModel 이라는 객체에 담음.
        SettingSensorListModel sensorListModel = settingSensorListRepository.findBySidAndPnameAndSerialNumber(sensorListAllModel.getAsid(), sensorListAllModel.getAproject(), sensorListAllModel.getSsn());

        //setting_seq : leakset 에서 Asid, Aproject,fReset 해당하는 값을 탐색 후,leakSetModel 이라는 객체에 담음.
        SettingLeaksetModel leakSetModel = settingLeaksetRepository.findTop1BySidAndPnameAndSnOrderByCidDesc(sensorListAllModel.getAsid(), sensorListAllModel.getAproject(), sensorListAllModel.getSsn());

        //setting_seq : factory_sensor_list 에서  Asid, Aproject,Ssn 해당하는 값을 탐색 후,factorySensorListModel 이라는 객체에 담음.
        SettingFactorySensorListModel factorySensorListModel = settingFactorySensorListRepository.findAllBySidAndPnameAndSn(sensorListAllModel.getAsid(), sensorListAllModel.getAproject(), sensorListAllModel.getSsn());

        //setting_seq : factorySensorListModel 존재 유무에 따른 참조테이블이 달라 분기 처리
        if (Objects.isNull(factorySensorListModel)) {
            //setting_seq : leak_project 에서  Asid, Aproject 해당하는 값을 탐색 후,leakProjectModel 이라는 객체에 담음.
            leakProjectModel = settingLeakProjectRepository.findAllBySidAndFactorypPname(sensorListAllModel.getAsid(), sensorListAllModel.getAproject());

        } else {
            //setting_seq : factory_leak_project 에서  factory_pname 해당하는 값을 탐색 후,factoryLeakProjectModel 이라는 객체에 담음.
            factoryLeakProjectModel = settingFactoryLeakprojectRepository.findAllByFactoryPname(factorySensorListModel.getFactorypname());
        }

        //setting_seq : 리턴해줄 settingResponse 값을 초기화.
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
        //setting_seq : factorySensorListModel 존재 따른 참조테이블(leakProject)이 달라 분기 처리
        if (Objects.isNull(factorySensorListModel)) {

            settingResponse.setServerUrl(leakProjectModel.getData_URL());
            settingResponse.setServerPort(leakProjectModel.getData_PORT());
            settingResponse.setDbUrl(leakProjectModel.getDb_URL());
            settingResponse.setDbPort(leakProjectModel.getDb_PORT());

            log.info("settingResponse check1 : {} ", settingResponse);

            //setting_seq : factorySensorListModel 존재 따른 참조테이블(factoryLeakProject)이 달라 분기 처리
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
