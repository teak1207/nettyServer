package com.devgong.nettyserver.service;


import com.devgong.nettyserver.domain.RequestListAllModel;
import com.devgong.nettyserver.repository.RequestSensorListAllRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class RequestSensorListService {


    RequestListAllModel requestListAllModel = null;
    private final RequestSensorListAllRepository requestSensorListAllRepository;

    public RequestListAllModel findDataExistence(String serialNumber) {


        requestListAllModel = requestSensorListAllRepository.findAllBySsn(serialNumber);

        log.info("requestListAllModel : {}", requestListAllModel);


        return requestListAllModel;
    }

    public void confirmPath(RequestListAllModel requestFindResults){

        log.info("pathchk : {}", requestFindResults.getAproject());
        log.info("pathchk : {}", requestFindResults.getFreset());
        log.info("pathchk : {}", requestFindResults.getAsid());
        log.info("pathchk : {}", requestFindResults.getMphone());
        log.info("pathchk : {}", requestFindResults.getSsn());
        log.info("pathchk : {}", requestFindResults.getRegdate());

    }
}
