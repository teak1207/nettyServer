package com.devgong.nettyserver.service;

import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.protocol.PacketFlag;
import com.devgong.nettyserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PreinstallSensorListService {

    private final PreInstallSensorListAllRepository preInstallSensorListAllRepository;
    private final DeviceSetRepository deviceSetRepository;
    private final NetworkSetRepository networkSetRepository;
    private final PreInstallSensorListRepository preInstallSensorListRepository;
    private final ReportRepository reportRepository;

    public PreInstallSetModel preInstallfindData(String modemnum) {
        /*
         * model -> repository 가서 값을 찾기위한 Object Value.
         * totaldata -> 넘어오는 데이터의 길이로 정확한 값이 넘어왔는지를 판단하기 위한 value
         */

        PreInstallSetModel preinstallSetModel = new PreInstallSetModel();

        PreInstallSensorListAllModel preInstallSensorListAllModel;
        PreinstallDeviceSetModel preinstallDeviceSetModel;
        PreinstallNetworkSetModel preinstallNetworkSetModel;
        PreInstallSensorListModel preInstallSensorListModel;

        log.info("modemnum : {}, byte : {}", modemnum, modemnum.getBytes().length);
        log.info("test : {}","test1234" );



        preInstallSensorListAllModel = preInstallSensorListAllRepository.findPreInstallSensorListAllModelByMphone(modemnum);
        preinstallNetworkSetModel = networkSetRepository.findAllByPnameAndSid(preInstallSensorListAllModel.getAproject(), preInstallSensorListAllModel.getAsid());
        preinstallDeviceSetModel = deviceSetRepository.findBySn(preInstallSensorListAllModel.getSsn());
        preInstallSensorListModel = preInstallSensorListRepository.findBySerialNumber(preInstallSensorListAllModel.getSsn());


        System.out.println("-------------------------------");
        System.out.println("PREINSTALL[NETWORK] : " + preinstallNetworkSetModel);
        System.out.println("PREINSTALL[DEVICE] : " + preinstallDeviceSetModel);

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

    public boolean insertReport(PreinstallReportModel preinstallReportModel) {

        if (preinstallReportModel != null) {
            reportRepository.save(preinstallReportModel);
            log.info("[INSERT] : SUCCESS ");
            return true;
        } else {
            log.error("[INSERT] : FAIL");
            return false;
        }
    }

}
