package com.devgong.nettyserver.service;

import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.repository.DeviceSetRepository;
import com.devgong.nettyserver.repository.NetworkSetRepository;
import com.devgong.nettyserver.repository.ReportRepository;
import com.devgong.nettyserver.repository.SensorListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SensorListService {

    private final SensorListRepository sensorListRepository;
    private final DeviceSetRepository deviceSetRepository;
    private final NetworkSetRepository networkSetRepository;

    private final ReportRepository reportRepository;

    public PreInstallSetModel findData(String flag, String modemnum) {
        /*
         * model -> repository 가서 값을 찾기위한 Object Value.
         * totaldata -> 넘어오는 데이터의 길이로 정확한 값이 넘어왔는지를 판단하기 위한 value
         */
        PreInstallSetModel preinstallSetModel = new PreInstallSetModel();
        SensorListModel sensorListModel;
        DeviceSetModel deviceSetModel;
        NetworkSetModel networkSetModel;

        if (flag.equals("0")) {   // flag =="0" (x)
            sensorListModel = sensorListRepository.findPreInstallModelByMphone(modemnum);
            networkSetModel = networkSetRepository.findAllByPnameAndSid(sensorListModel.getAproject(), sensorListModel.getAsid());
            deviceSetModel = deviceSetRepository.findBySn(sensorListModel.getSsn());

            System.out.println("-------------------------------");
            System.out.println("PREINSTALL[NETWORK] : " + networkSetModel);
            System.out.println("PREINSTALL[DEVICE] : " + deviceSetModel);

            preinstallSetModel.setTime1(deviceSetModel.getTime1());
            preinstallSetModel.setTime2(deviceSetModel.getTime2());
            preinstallSetModel.setTime3(deviceSetModel.getTime3());
            preinstallSetModel.setSerialNumber(deviceSetModel.getSn());
            preinstallSetModel.setPeriod(deviceSetModel.getPreiod());
            preinstallSetModel.setSamplingTime(deviceSetModel.getSampletime());
            preinstallSetModel.setSampleRate(deviceSetModel.getSamplerate());
            preinstallSetModel.setServerUrl(networkSetModel.getDataServer());
            preinstallSetModel.setServerPort(networkSetModel.getDataPort());
            return preinstallSetModel;
        }else{
            System.out.println("[flag] : 0이 아닙니다. :)");
        }

        return null;
    }

    public boolean insertReport(PreinstallReportModel preinstallReportModel) {

        if (preinstallReportModel != null) {
            reportRepository.save(preinstallReportModel);
            System.out.println("[INSERT] : SUCCESS ");
        } else {
            System.out.println("[INSERT] : FAIL");
        }


        return true;
    }

}
