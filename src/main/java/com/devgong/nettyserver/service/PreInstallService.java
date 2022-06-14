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

    /*
     * pre-install 에 대한 로직을 구현하는 부분
     */

    private final PreInstallRepository preInstallRepository;

    public PreInstallModel findData(PreInstallModel model) {

        PreInstallModel deviceInfos; // find 한 data를 담을 Model

        if (Objects.equals(model.getChksum(), "bbbb")) {
            //DB에서 원하는 값을 가져올 경우, 클라이언트에게 pre-install 값 전송
            deviceInfos = preInstallRepository.findPreInstallModelByFlagAndChksum(model.getFlag(), model.getChksum());

            System.out.println(deviceInfos);

            return deviceInfos;
        }
        return null;
    }
}
