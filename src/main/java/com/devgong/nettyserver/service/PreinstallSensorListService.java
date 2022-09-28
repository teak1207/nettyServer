package com.devgong.nettyserver.service;

import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.protocol.DeviceStatus;
import com.devgong.nettyserver.protocol.Packet;
import com.devgong.nettyserver.protocol.preinstall.PreInstallReportRequest;
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

    public PreInstallSetModel preInstallfindData(String modemnum) {

        PreInstallSetModel preinstallSetModel = new PreInstallSetModel();

        PreInstallSensorListAllModel preInstallSensorListAllModel;
        PreinstallDeviceSetModel preinstallDeviceSetModel;
        PreinstallNetworkSetModel preinstallNetworkSetModel;
        PreInstallSensorListModel preInstallSensorListModel;


//        log.info("modemnum : {}, byte : {}", modemnum, modemnum.getBytes().length);

        preInstallSensorListAllModel = preInstallSensorListAllRepository.findPreInstallSensorListAllModelByMphone(modemnum.trim());
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

    public boolean insertReport(Packet<PreInstallReportRequest> request, byte[] bytes) {

        PreinstallReportModel preinstallReportModel = new PreinstallReportModel();
        if (bytes != null) {

            int baudrateNext; // 1 byte

            for(byte a : bytes){
                log.info("test : {}" , (char)a);
            }

            preinstallReportModel.setSerialNumber(request.getSensorId());
            preinstallReportModel.setDateTime(LocalDateTime.now());
            preinstallReportModel.setDebugMsg(new String(Arrays.copyOfRange(bytes, 0, 13)));

            preinstallReportModel.setRecordingTime1(new String(Arrays.copyOfRange(bytes, 13, 17)).trim());
            preinstallReportModel.setRecordingTime2(new String(Arrays.copyOfRange(bytes, 17, 21)).trim());
            preinstallReportModel.setRecordingTime3(new String(Arrays.copyOfRange(bytes, 21, 25)).trim());

            preinstallReportModel.setFmRadio(new String(Arrays.copyOfRange(bytes, 25, 29)).trim());
            preinstallReportModel.setFirmWareVersion(new String(Arrays.copyOfRange(bytes, 29, 35)).trim());
            preinstallReportModel.setBatteryVtg(new String(Arrays.copyOfRange(bytes, 35, 41)).trim());
            // TODO : casting check1
            preinstallReportModel.setRSSI(String.valueOf(bytes[41]));
            preinstallReportModel.setDeviceStatus(new String(Arrays.copyOfRange(bytes, 42, 44)));

            preinstallReportModel.setSamplingTime(String.valueOf(bytes[44]));
            preinstallReportModel.setPx(new String(Arrays.copyOfRange(bytes, 45, 55)).trim());
            preinstallReportModel.setPy(new String(Arrays.copyOfRange(bytes, 55, 65)).trim());
            preinstallReportModel.setModemNumber(new String(Arrays.copyOfRange(bytes, 65, 81)).trim());
            preinstallReportModel.setSid(new String(Arrays.copyOfRange(bytes, 81, 97)).trim());
            preinstallReportModel.setPeriod(String.valueOf(bytes[97]));
            preinstallReportModel.setServerUrl(new String(Arrays.copyOfRange(bytes, 98, 130)).trim());
            preinstallReportModel.setServerPort(new String(Arrays.copyOfRange(bytes, 130, 134)).trim());
            preinstallReportModel.setDbUrl(new String(Arrays.copyOfRange(bytes, 134, 166)).trim());
            preinstallReportModel.setDbPort(new String(Arrays.copyOfRange(bytes, 166, 170)).trim());
            preinstallReportModel.setFmTime(String.valueOf(bytes[170]));
            preinstallReportModel.setBaudrate(String.valueOf(bytes[171]));
            preinstallReportModel.setBaudrate(String.valueOf(bytes[171]));
            preinstallReportModel.setPcbVersion(String.valueOf(bytes[173]));


            baudrateNext = bytes[172];

            log.info("preinstallReportModel : {}", bytes);
            log.info("preinstallReportModel : {}", preinstallReportModel.getServerUrl());
            log.info("preinstallReportModel : {}", preinstallReportModel.toString());
            log.info("preinstallReportModel : {}", preinstallReportModel);
            reportRepository.save(preinstallReportModel);
            log.info("[INSERT] : SUCCESS ");
            return false;


        } else {
            log.error("[INSERT] : FAIL");
            return false;
        }
    }

}
