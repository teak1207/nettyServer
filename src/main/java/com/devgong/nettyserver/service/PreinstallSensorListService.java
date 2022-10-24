package com.devgong.nettyserver.service;

import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Service
public class PreinstallSensorListService {

    private final PreInstallSensorListAllRepository preInstallSensorListAllRepository;
    private final DeviceSetRepository deviceSetRepository;
    private final NetworkSetRepository networkSetRepository;
    private final PreInstallSensorListRepository preInstallSensorListRepository;
    private final ReportRepository reportRepository;

    public PreInstallSetModel preInstallFindData(String modemnum) {

        PreInstallSetModel preinstallSetModel = new PreInstallSetModel();

        PreInstallSensorListAllModel preInstallSensorListAllModel;
        PreinstallDeviceSetModel preinstallDeviceSetModel;
        PreinstallNetworkSetModel preinstallNetworkSetModel;
        PreInstallSensorListModel preInstallSensorListModel;

        //seq : sensor_list_all 에서 serialNum에 해당하는 값을 탐색 후,preInstallSensorListAllModel 이라는 객체에 담음.
        preInstallSensorListAllModel = preInstallSensorListAllRepository.findPreInstallSensorListAllModelByMphone(modemnum.trim());
        //seq : 앞서 담은 preInstallSensorListAllModel 객체로 값을 get,set 할 수 있음.
        //seq : preInstallSensorListAllModel의 Aproject,Asid 값으로 leak_project 테이블에서 값을 가져옴.
        preinstallNetworkSetModel = networkSetRepository.findAllByPnameAndSid(preInstallSensorListAllModel.getAproject(), preInstallSensorListAllModel.getAsid());
        //seq : preInstallSensorListAllModel의 Ssn 값으로 leakset_bysensor 테이블에서 값을 가져옴.
        preinstallDeviceSetModel = deviceSetRepository.findBySn(preInstallSensorListAllModel.getSsn());
        //seq : preInstallSensorListAllModel의 Ssn 값으로 sensor_list 테이블에서 값을 가져옴.
        preInstallSensorListModel = preInstallSensorListRepository.findBySerialNumber(preInstallSensorListAllModel.getSsn());

        log.info("-------------------------------");
        log.info("PREINSTALL[NETWORK] : {}", preinstallNetworkSetModel);
        log.info("PREINSTALL[DEVICE] : {}", preinstallDeviceSetModel);
        log.info("-------------------------------");


        //seq : preinstallSetModel 라는 객체에 4개의 테이블에서 가져온 값들을 채워넣어 객체를 리턴함.
        preinstallSetModel.setTime1(preinstallDeviceSetModel.getTime1());
        preinstallSetModel.setTime2(preinstallDeviceSetModel.getTime2());
        preinstallSetModel.setTime3(preinstallDeviceSetModel.getTime3());
        preinstallSetModel.setFmFrequency(preinstallDeviceSetModel.getFmPrequency());//new
        preinstallSetModel.setSid(preinstallNetworkSetModel.getSid()); //new
        preinstallSetModel.setPname(preinstallNetworkSetModel.getPname());//new
        preinstallSetModel.setPx(preInstallSensorListModel.getPx());
        preinstallSetModel.setPy(preInstallSensorListModel.getPy());
        preinstallSetModel.setSerialNumber(preinstallDeviceSetModel.getSn());
        preinstallSetModel.setPeriod(preinstallDeviceSetModel.getPreiod());
        preinstallSetModel.setSamplingTime(preinstallDeviceSetModel.getSampletime());
        preinstallSetModel.setSampleRate(preinstallDeviceSetModel.getSamplerate());
        preinstallSetModel.setServerUrl(preinstallNetworkSetModel.getDataServer());
        preinstallSetModel.setServerPort(preinstallNetworkSetModel.getDataPort());
        preinstallSetModel.setDbUrl(preinstallNetworkSetModel.getDbUrl());//new
        preinstallSetModel.setDbPort(preinstallNetworkSetModel.getDbPort());//new
        preinstallSetModel.setRadioTime(preinstallDeviceSetModel.getRadioTime());//new
        preinstallSetModel.setBaudrate(preinstallDeviceSetModel.getBaudrate());//new

        return preinstallSetModel;
    }

    public boolean insertReport(byte[] bytes) {

        PreinstallReportModel preinstallReportModel = new PreinstallReportModel();

        if (bytes != null) {
            preinstallReportModel.setSerialNumber(new String(Arrays.copyOfRange(bytes, 0, 24)));
            preinstallReportModel.setDateTime(LocalDateTime.now());
            preinstallReportModel.setDebugMsg(new String(Arrays.copyOfRange(bytes, 44, 56)));
            preinstallReportModel.setRecordingTime1(new String(Arrays.copyOfRange(bytes, 57, 61)));
            preinstallReportModel.setRecordingTime2(new String(Arrays.copyOfRange(bytes, 61, 65)));
            preinstallReportModel.setRecordingTime3(new String(Arrays.copyOfRange(bytes, 65, 69)));
            preinstallReportModel.setFmRadio(new String(Arrays.copyOfRange(bytes, 69, 73)));
            preinstallReportModel.setFirmWareVersion(new String(Arrays.copyOfRange(bytes, 73, 79)));
            preinstallReportModel.setBatteryVtg(new String(Arrays.copyOfRange(bytes, 79, 85)));
            preinstallReportModel.setRSSI(String.valueOf(bytes[85] & 0xff));
            preinstallReportModel.setDeviceStatus(new String(Arrays.copyOfRange(bytes, 86, 88)));
            preinstallReportModel.setSamplingTime(String.valueOf(bytes[89] & 0xff));
            preinstallReportModel.setPx(new String(Arrays.copyOfRange(bytes, 89, 99)));
            preinstallReportModel.setPy(new String(Arrays.copyOfRange(bytes, 99, 109)));
            preinstallReportModel.setModemNumber(new String(Arrays.copyOfRange(bytes, 109, 125)));
            preinstallReportModel.setSid(new String(Arrays.copyOfRange(bytes, 125, 141)));
            preinstallReportModel.setPeriod(String.valueOf(bytes[141]));
            preinstallReportModel.setServerUrl(new String(Arrays.copyOfRange(bytes, 142, 174)));
            preinstallReportModel.setServerPort(new String(Arrays.copyOfRange(bytes, 174, 178)));
            preinstallReportModel.setDbUrl(new String(Arrays.copyOfRange(bytes, 178, 210)));
            preinstallReportModel.setDbPort(new String(Arrays.copyOfRange(bytes, 210, 214)));
            preinstallReportModel.setFmTime(String.valueOf(bytes[215]));
            preinstallReportModel.setBaudrate(String.valueOf(bytes[216]));
            preinstallReportModel.setBaudrate(String.valueOf(bytes[217]));
            preinstallReportModel.setPcbVersion(String.valueOf(bytes[218]));

            log.info("preinstallReportModel : {}", preinstallReportModel);
            reportRepository.save(preinstallReportModel);
            log.info("[INSERT] : SUCCESS ");
            return true;


        } else {
            log.error("[INSERT] : FAIL");
            return false;
        }
    }

}
