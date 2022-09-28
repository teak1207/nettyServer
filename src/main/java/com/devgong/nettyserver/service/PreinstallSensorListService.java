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
            int i = 0;
            for (byte a : bytes) {
                log.info("test : {}", (char) a);
                log.info("test : {}", i++);
                log.info("-----------------------");

            }

            preinstallReportModel.setSerialNumber(new String(Arrays.copyOfRange(bytes, 0, 24)));
            preinstallReportModel.setDateTime(LocalDateTime.now());
            preinstallReportModel.setDebugMsg(new String(Arrays.copyOfRange(bytes, 44, 56)).trim());
            preinstallReportModel.setRecordingTime1(new String(Arrays.copyOfRange(bytes, 57, 60)));
            preinstallReportModel.setRecordingTime2(new String(Arrays.copyOfRange(bytes, 61, 64)));
            preinstallReportModel.setRecordingTime3(new String(Arrays.copyOfRange(bytes, 65, 68)));
            preinstallReportModel.setFmRadio(new String(Arrays.copyOfRange(bytes, 69, 72)));
            preinstallReportModel.setFirmWareVersion(new String(Arrays.copyOfRange(bytes, 73, 78)));
            preinstallReportModel.setBatteryVtg(new String(Arrays.copyOfRange(bytes, 79, 84)));
            preinstallReportModel.setRSSI(String.valueOf(bytes[85]));
            preinstallReportModel.setDeviceStatus(new String(Arrays.copyOfRange(bytes, 86, 87)));
            preinstallReportModel.setSamplingTime(String.valueOf(bytes[88]));
            preinstallReportModel.setPx(new String(Arrays.copyOfRange(bytes, 89, 98)));
            preinstallReportModel.setPy(new String(Arrays.copyOfRange(bytes, 99, 108)));
            preinstallReportModel.setModemNumber(new String(Arrays.copyOfRange(bytes, 109, 124)));
            preinstallReportModel.setSid(new String(Arrays.copyOfRange(bytes, 125, 140)));
            preinstallReportModel.setPeriod(String.valueOf(bytes[141]));
            preinstallReportModel.setServerUrl(new String(Arrays.copyOfRange(bytes, 142, 173)));
            preinstallReportModel.setServerPort(new String(Arrays.copyOfRange(bytes, 174, 177)));
            preinstallReportModel.setDbUrl(new String(Arrays.copyOfRange(bytes, 178, 209)));
            preinstallReportModel.setDbPort(new String(Arrays.copyOfRange(bytes, 210, 214)));
            preinstallReportModel.setFmTime(String.valueOf(bytes[215]));
            preinstallReportModel.setBaudrate(String.valueOf(bytes[216]));
            preinstallReportModel.setBaudrate(String.valueOf(bytes[217]));
            preinstallReportModel.setPcbVersion(String.valueOf(bytes[218]));


            baudrateNext = bytes[172];

            log.info("preinstallReportModel : {}", bytes);
            log.info("preinstallReportModel : {}", preinstallReportModel.getRecordingTime1());
            log.info("preinstallReportModel : {}", preinstallReportModel.getRecordingTime2());
            log.info("preinstallReportModel : {}", preinstallReportModel.getRecordingTime3());
            log.info("preinstallReportModel : {}", preinstallReportModel.getServerUrl());
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
