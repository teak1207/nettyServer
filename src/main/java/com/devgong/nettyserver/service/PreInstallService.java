package com.devgong.nettyserver.service;

import com.devgong.nettyserver.domain.PreInstallModel;
import com.devgong.nettyserver.repository.PreInstallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PreInstallService {

    private final PreInstallRepository preInstallRepository;

    public PreInstallModel findData(PreInstallModel model) {

        PreInstallModel preInstallDeviceInfos; // find 한 data를 담을 Model

        // 만약 ChkSum의 값이 length 60이라면 Pass 아니면 NAK

        if (Objects.equals(model.getChksum(), "AAAA")) {
            //DB에서 원하는 값이 있는지 체크하는 부분
            preInstallDeviceInfos = preInstallRepository.findPreInstallModelByModemNumber(model.getModemNumber());
            System.out.println(preInstallDeviceInfos);
            System.out.println(preInstallDeviceInfos.toString());
            return preInstallDeviceInfos;
        }
        return null;

    }
}
