package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SettingSensorListService {

    private final SettingSensorListAllRepository settingSensorListAllRepository;
    private final SettingSensorListRepository settingSensorListRepository;
    private final SettingLeaksetRepository settingLeaksetRepository;
    private final SettingFactorySensorListRepository settingFactorySensorListRepository;
    private final SettingFactoryLeakprojectRepository settingFactoryLeakprojectRepository;
    private final SettingLeakProjectRepository settingLeakProjectRepository;

    public SettingSetModel settingFindData(String flag, String sereialNumber) {

        SettingSetModel settingSetModel = new SettingSetModel();
        SettingSensorListAllModel settingSensorListAllModel;


        if (flag.equals("6")) {

            settingSensorListAllModel = settingSensorListAllRepository.findPreInstallModelBySsn(sereialNumber);

            SettingSensorListModel settingSensorListModel = settingSensorListRepository.findAllBySidAndPname(settingSensorListAllModel.getAsid(), settingSensorListAllModel.getAproject());
            SettingLeaksetModel settingLeaksetModel = settingLeaksetRepository.findAllBySidAndPname(settingSensorListAllModel.getAsid(), settingSensorListAllModel.getAproject());

            SettingFactorySensorListModel settingFactorySensorListModel = settingFactorySensorListRepository.findAllBySidAndPnameAndSn(settingSensorListAllModel.getAsid(), settingSensorListAllModel.getAproject(), settingSensorListAllModel.getSsn());


            if (settingFactorySensorListModel.getFactorypname().equals("")) {

                System.out.println("[leak_project URL/PORT 정보를 가져옵니다]");
                SettingLeakProjectModel settingLeakProjectModel = settingLeakProjectRepository.findAllBySidAndFactorypPname(settingSensorListAllModel.getAsid(), settingSensorListAllModel.getAproject());
                System.out.println("[Leak-project] : " + settingLeakProjectModel);

            } else {
                System.out.println("[factory_leak-project URL/PORT 정보를 가져옵니다]");
                SettingFactoryLeakprojectModel settingFactoryLeakprojectModel = settingFactoryLeakprojectRepository.findAllByFactoryPname(settingFactorySensorListModel.getFactorypname());
                System.out.println("[FactoryLeak-project] : " + settingFactoryLeakprojectModel);
            }


            System.out.println("[SensorListAll] : " + settingSensorListAllModel);
            System.out.println("[SensorList] : " + settingSensorListModel);
            System.out.println("[LeakSet] : " + settingLeaksetModel);
            System.out.println("[FactorySensorList] : " + settingFactorySensorListModel);




        }

        return settingSetModel;
    }


}
