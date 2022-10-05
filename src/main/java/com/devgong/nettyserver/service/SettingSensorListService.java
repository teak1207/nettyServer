package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@RequiredArgsConstructor
@Service
public class SettingSensorListService {

    private final SettingSensorListAllRepository settingSensorListAllRepository;
    private final SettingSensorListRepository settingSensorListRepository;
    private final SettingLeaksetRepository settingLeaksetRepository;
    private final SettingFactorySensorListRepository settingFactorySensorListRepository;
    private final SettingFactoryLeakprojectRepository settingFactoryLeakprojectRepository;
    private final SettingLeakProjectRepository settingLeakProjectRepository;

    public SettingSetModel settingFindData( String sereialNumber) {


        SettingSetModel settingSetModel = new SettingSetModel();
        SettingSensorListAllModel settingSensorListAllModel;
        SettingLeakProjectModel settingLeakProjectModel = null;
        SettingFactoryLeakprojectModel settingFactoryLeakprojectModel = null;


        settingSensorListAllModel = settingSensorListAllRepository.findPreInstallModelBySsn(sereialNumber);

        log.info("leakset : {}", settingSensorListAllModel);

        SettingSensorListModel settingSensorListModel = settingSensorListRepository.findAllBySidAndPname(settingSensorListAllModel.getAsid(), settingSensorListAllModel.getAproject());
        SettingLeaksetModel settingLeaksetModel = settingLeaksetRepository.findAllBySidAndPnameAndReset(settingSensorListAllModel.getAsid(), settingSensorListAllModel.getAproject(), settingSensorListAllModel.getFreset());

        SettingFactorySensorListModel settingFactorySensorListModel = settingFactorySensorListRepository.findAllBySidAndPnameAndSn(settingSensorListAllModel.getAsid(), settingSensorListAllModel.getAproject(), settingSensorListAllModel.getSsn());


        log.info("여기까지2");

        if (settingFactorySensorListModel.getFactorypname().equals("")) {

//                System.out.println("[leak_project URL/PORT 정보를 가져옵니다]");
            settingLeakProjectModel = settingLeakProjectRepository.findAllBySidAndFactorypPname(settingSensorListAllModel.getAsid(), settingSensorListAllModel.getAproject());
//                System.out.println("[Leak-project] : " + settingLeakProjectModel);

        } else {
//                System.out.println("[factory_leak-project URL/PORT 정보를 가져옵니다]");
            settingFactoryLeakprojectModel = settingFactoryLeakprojectRepository.findAllByFactoryPname(settingFactorySensorListModel.getFactorypname());
//                System.out.println("[FactoryLeak-project] : " + settingFactoryLeakprojectModel);
        }
//            System.out.println("[SensorListAll] : " + settingSensorListAllModel);
//            System.out.println("[SensorList] : " + settingSensorListModel);
//            System.out.println("[LeakSet] : " + settingLeaksetModel);
//            System.out.println("[FactorySensorList] : " + settingFactorySensorListModel);
//            System.out.println("[leak_project] : " + settingLeakProjectModel);

        settingSetModel.setTime1(settingLeaksetModel.getTime1());
        settingSetModel.setTime2(settingLeaksetModel.getTime2());
        settingSetModel.setTime3(settingLeaksetModel.getTime3());
        settingSetModel.setFmFrequency(settingLeaksetModel.getFmFrequency());
        settingSetModel.setSid(settingSensorListAllModel.getAsid());
        settingSetModel.setPname(settingSensorListAllModel.getAproject());
        settingSetModel.setSleep(settingLeaksetModel.getSleep());
        settingSetModel.setReset(settingLeaksetModel.getReset());
        settingSetModel.setPeriod(settingLeaksetModel.getPeriod());
        settingSetModel.setSamplingTime(settingLeaksetModel.getSampletime());
        settingSetModel.setFReset(settingSensorListAllModel.getFreset());
        settingSetModel.setPx(settingSensorListModel.getPx());
        settingSetModel.setPy(settingSensorListModel.getPy());
        settingSetModel.setActive(settingLeaksetModel.getActive());
        settingSetModel.setSampleRate(settingLeaksetModel.getSamplerate());
        settingSetModel.setRadioTime(settingLeaksetModel.getFmtime());

        if (settingFactorySensorListModel.getFactorypname().equals("")) {
            settingSetModel.setServerUrl(settingLeakProjectModel.getData_URL());
            settingSetModel.setServerPort(settingLeakProjectModel.getData_PORT());
            settingSetModel.setDbUrl(settingLeakProjectModel.getDb_URL());
            settingSetModel.setDbPort(settingLeakProjectModel.getDb_PORT());

        } else {
            settingSetModel.setServerUrl(settingFactoryLeakprojectModel.getDataURL());
            settingSetModel.setServerPort(settingFactoryLeakprojectModel.getDataPORT());
            settingSetModel.setDbUrl(settingFactoryLeakprojectModel.getDbURL());
            settingSetModel.setDbPort(settingFactoryLeakprojectModel.getDbPORT());
        }
        return settingSetModel;
    }
}
