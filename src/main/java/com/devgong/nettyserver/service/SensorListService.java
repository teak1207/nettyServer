package com.devgong.nettyserver.service;

import com.devgong.nettyserver.domain.DeviceSetModel;
import com.devgong.nettyserver.domain.NetworkSetModel;
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


    public SensorListModel findData(String totaldata, String modemnum) {

        /*
         * model -> repository 가서 값을 찾기위한 Object Value.
         * totaldata -> 넘어오는 데이터의 길이로 정확한 값이 넘어왔는지를 판단하기 위한 value
         */

        SensorListModel sensorListInfo;
        DeviceSetModel deviceSetInfo;
        NetworkSetModel networkSetInfo;
        // 만약 ChkSum의 값이 length 60이라면 Pass 아니면 NAK

        System.out.println("totaldata--> " + totaldata);
        System.out.println("modemnum--> " + modemnum);

        if (totaldata.length() == 61) {
            /*
            -> model 길이를 체크한 후, 분기처리.
            -> DB  원하는 값이 있는지 체크.
            -> ModemNum를 통해 sensor_list_all 값 가져오기
            */
            sensorListInfo = sensorListRepository.findPreInstallModelByMphone(modemnum);

            System.out.println("ssn--> "+sensorListInfo.getSsn());
            System.out.println("asid--> "+sensorListInfo.getAsid());
            System.out.println("Aproject--> "+sensorListInfo.getAproject());


            networkSetInfo = networkSetRepository.findBySidAndPname(sensorListInfo.getAsid(), sensorListInfo.getAproject());
            deviceSetInfo = deviceSetRepository.findBySn(sensorListInfo.getSsn());

            System.out.println("test(network)-->" + networkSetInfo);
            System.out.println("test(device)-->" + deviceSetInfo);


            /*
             * preInstallDeviceInfos.getSsn()->leakset_bysensor values 가져오기
             * preInstallDeviceInfos.getAsid()+getAproject()->leak_project values 가져오기
             * */


            return sensorListInfo;
        }
        return null;


    }
}
