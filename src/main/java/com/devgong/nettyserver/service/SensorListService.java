package com.devgong.nettyserver.service;

import com.devgong.nettyserver.domain.DeviceSetModel;
import com.devgong.nettyserver.domain.NetworkSetModel;
import com.devgong.nettyserver.domain.PreInstallSetModel;
import com.devgong.nettyserver.domain.SensorListModel;
import com.devgong.nettyserver.repository.DeviceSetRepository;
import com.devgong.nettyserver.repository.NetworkSetRepository;
import com.devgong.nettyserver.repository.SensorListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SensorListService {

    private final SensorListRepository sensorListRepository;
    private final DeviceSetRepository deviceSetRepository;
    private final NetworkSetRepository networkSetRepository;


    public PreInstallSetModel findData(String totaldata, String modemnum, String flag) {
        /*
         * model -> repository 가서 값을 찾기위한 Object Value.
         * totaldata -> 넘어오는 데이터의 길이로 정확한 값이 넘어왔는지를 판단하기 위한 value
         */
        PreInstallSetModel preinstallSetModel = new PreInstallSetModel();
        SensorListModel sensorListModel;
        DeviceSetModel deviceSetModel;
        NetworkSetModel networkSetModel;
        // 만약 ChkSum의 값이 length 60이라면 Pass 아니면 NAK


        if (flag == "0") {
            /*
            -> model 길이를 체크한 후, 분기처리.
            -> DB  원하는 값이 있는지 체크.
            -> ModemNum를 통해 sensor_list_all 값 가져오기
            */
            sensorListModel = sensorListRepository.findPreInstallModelByMphone(modemnum);
            networkSetModel = networkSetRepository.findAllByPnameAndSid(sensorListModel.getAproject(), sensorListModel.getAsid());
            deviceSetModel = deviceSetRepository.findBySn(sensorListModel.getSsn());
            System.out.println("-------------------------------");
            System.out.println("test(network)-->" + networkSetModel);
            System.out.println("test(device)-->" + deviceSetModel);

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
        }
        return null;


    }
}
