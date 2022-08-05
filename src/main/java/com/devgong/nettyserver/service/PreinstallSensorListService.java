package com.devgong.nettyserver.service;

import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PreinstallSensorListService {

    private final PreInstallSensorListAllRepository preInstallSensorListAllRepository;
    private final DeviceSetRepository deviceSetRepository;
    private final NetworkSetRepository networkSetRepository;
    private final PreInstallSensorListRepository preInstallSensorListRepository;

    private final ReportRepository reportRepository;

    public PreInstallSetModel preInstallfindData(String flag, String modemnum) {
        /*
         * model -> repository 가서 값을 찾기위한 Object Value.
         * totaldata -> 넘어오는 데이터의 길이로 정확한 값이 넘어왔는지를 판단하기 위한 value
         */

        PreInstallSetModel preinstallSetModel = new PreInstallSetModel();

        PreInstallSensorListAllModel preInstallSensorListAllModel;
        PreinstallDeviceSetModel preinstallDeviceSetModel;
        PreinstallNetworkSetModel preinstallNetworkSetModel;
        PreInstallSensorListModel preInstallSensorListModel ;

        if (flag.equals("A")) {   // flag =="0" (x)
            preInstallSensorListAllModel = preInstallSensorListAllRepository.findPreInstallSensorListAllModelByMphone(modemnum);

            System.out.println(preInstallSensorListAllModel.toString());

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
        } else {
            System.out.println("[flag] : 0이 아닙니다. :)");
        }

        return null;
    }

    public boolean insertReport(PreinstallReportModel preinstallReportModel) {

        if (preinstallReportModel != null) {
            reportRepository.save(preinstallReportModel);
            System.out.println("[INSERT] : SUCCESS ");
            return true;
        } else {
            System.out.println("[INSERT] : FAIL");
            return false;
        }
    }

}
