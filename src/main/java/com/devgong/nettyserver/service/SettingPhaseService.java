package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author devGong
 * @version 1.0
 * Setting 에서 관련 테이블들을 참조, Find 메서드기능 구현.
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class SettingPhaseService {

    private final SettingSensorListAllRepository settingSensorListAllRepository;
    private final SettingSensorListRepository settingSensorListRepository;
    private final SettingLeaksetRepository settingLeaksetRepository;
    private final SettingFactorySensorListRepository settingFactorySensorListRepository;
    private final SettingFactoryLeakprojectRepository settingFactoryLeakprojectRepository;
    private final SettingLeakProjectRepository settingLeakProjectRepository;


    /**
     * @param serialNumber - 디바이스에서 넘겨주는 고유 모뎀 번호
     * @return true or false 을 리턴함.
     * @author devGong
     * (1) 미할당 센서 체크 여부 - sid and project value not exist
     * (2) 미설치 센서 체크 여부 - sid or project value not exist
     * (3) 미배치 센서 체크 여부 - 좌표값 (Px or Py) value not exist
     * (4) 미설정 센서 체크 여부 - 초기 설정값 존재여부 체크 3.5 ver 처음 배치시 설정값 없다고 함.
     */
    public boolean getCheckLiveOperation(String serialNumber) {

        Optional<SettingSensorListAllModel> installResult = settingSensorListAllRepository.findBySsn(serialNumber);
        Optional<SettingSensorListModel> assignResult = Optional.ofNullable(settingSensorListRepository.findBySidAndPnameAndSerialNumber(installResult.get().getAsid(), installResult.get().getAproject(), installResult.get().getSsn()));
        Optional<SettingLeaksetModel> settingResult = Optional.ofNullable(settingLeaksetRepository.findTop1BySidAndPnameAndSnOrderByCidDesc(installResult.get().getAsid(), installResult.get().getAproject(), installResult.get().getSsn()));
        // setting_seq : sensorListALl 에서 Asid 와 Aproject  둘 중 하나라도 없으면  false Return
        if ((installResult.get().getAsid().equals("-")) && installResult.get().getAproject().equals("미배치")) {
            log.info("해당 센서의 SID And Project Value 존재하지 않음");
            return false;

            // setting_seq : sensorListALl 에서 Asid 와 Aproject가 둘다 없어도  false Return
        } else if (installResult.get().getAproject().equals("미배치") || installResult.get().getAsid().equals("-")) {
            log.info(". 해당 센서의 SID Or Project Value 존재하는지 확인바람. ");
            return false;

        } else if (assignResult.get().getPx().equals(" ") || assignResult.get().getPy().equals(" ")) {

            log.info("헤당 센서의 Px Or Py Value 존재여부 확인바람.");
            return false;
        } else if (StringUtils.hasText(String.valueOf(settingResult))) {
            log.info("헤당 센서의 초기 설정값 존재여부 확인바람.");

            return false;
        }


        return true;
    }

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
//    @Nullable
    public Optional<SettingResponseModel> getResponseData(String serialNumber) {

        //setting_seq : sensorListAll 에서 serialNumber 해당하는 값을 탐색 후,sensorListAllModel 이라는 객체에 담음.
        Optional<SettingSensorListAllModel> sensorListAllModel = settingSensorListAllRepository.findBySsn(serialNumber);

        SettingSensorListAllModel sensorInfo;

        if (sensorListAllModel.isEmpty()) return Optional.empty();
        else sensorInfo = sensorListAllModel.get();

        //setting_seq : sensor_list 에서 Asid, Aproject 해당하는 값을 탐색 후,sensorListModel 이라는 객체에 담음.
        SettingSensorListModel sensorListModel = settingSensorListRepository.findBySidAndPnameAndSerialNumber(sensorInfo.getAsid(), sensorInfo.getAproject(), sensorInfo.getSsn());

        //setting_seq : leakset 에서 Asid, Aproject,fReset 해당하는 값을 탐색 후,leakSetModel 이라는 객체에 담음.
        SettingLeaksetModel leakSetModel = settingLeaksetRepository.findTop1BySidAndPnameAndSnOrderByCidDesc(sensorInfo.getAsid(), sensorInfo.getAproject(), sensorInfo.getSsn());

        //setting_seq : factory_sensor_list 에서  Asid, Aproject,Ssn 해당하는 값을 탐색 후,factorySensorListModel 이라는 객체에 담음.
        Optional<SettingFactorySensorListModel> factorySensorListModel = settingFactorySensorListRepository.findBySidAndPnameAndSn(sensorInfo.getAsid(), sensorInfo.getAproject(), sensorInfo.getSsn());

        SettingResponseModel.SettingResponseModelBuilder settingResponseModelBuilder = SettingResponseModel.builder()
                .time1(leakSetModel.getTime1())
                .time2(leakSetModel.getTime2())
                .time3(leakSetModel.getTime3())
                .fmRadio(leakSetModel.getFmFrequency())
                .sid(sensorInfo.getAsid())
                .pname(sensorInfo.getAproject())
                .sleep(leakSetModel.getSleep())
                .reset(leakSetModel.getReset())
                .period(leakSetModel.getPeriod())
                .samplingTime(leakSetModel.getSampletime())
                .fReset(sensorInfo.getFreset())
                .px(sensorListModel.getPx())
                .py(sensorListModel.getPy())
                .active(leakSetModel.getActive())
                .sampleRate(leakSetModel.getSamplerate())
                .radioTime(leakSetModel.getFmtime());

        //setting_seq : factorySensorListModel 존재 유무에 따른 참조테이블이 달라 분기 처리
        if (factorySensorListModel.isEmpty()) {
            //setting_seq : leak_project 에서  Asid, Aproject 해당하는 값을 탐색 후,leakProjectModel 이라는 객체에 담음.
            SettingLeakProjectModel leakProjectModel = settingLeakProjectRepository.findAllBySidAndFactorypPname(sensorInfo.getAsid(), sensorInfo.getAproject());

            settingResponseModelBuilder
                    .serverUrl(leakProjectModel.getData_URL())
                    .serverPort(leakProjectModel.getData_PORT())
                    .dbUrl(leakProjectModel.getDb_URL())
                    .dbPort(leakProjectModel.getDb_PORT());
        } else {
            //setting_seq : factory_leak_project 에서  factory_pname 해당하는 값을 탐색 후,factoryLeakProjectModel 이라는 객체에 담음.
            SettingFactoryLeakprojectModel factoryLeakProjectModel = settingFactoryLeakprojectRepository.findAllByFactoryPname(factorySensorListModel.get().getFactorypname());

            settingResponseModelBuilder
                    .serverUrl(factoryLeakProjectModel.getDataURL())
                    .serverPort(factoryLeakProjectModel.getDataPORT())
                    .dbUrl(factoryLeakProjectModel.getDbURL())
                    .dbPort(factoryLeakProjectModel.getDbPORT());
        }

//        log.info("테스트1 service,select -> 객체에 담음.: {}", settingResponseModelBuilder);
        return Optional.of(settingResponseModelBuilder.build());
    }
}
