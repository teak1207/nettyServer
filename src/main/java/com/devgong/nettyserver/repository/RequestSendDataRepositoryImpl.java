package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.RequestLeakDataModel;
import com.devgong.nettyserver.protocol.NewPacket;
import com.devgong.nettyserver.protocol.request.ReqRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RequestSendDataRepositoryImpl implements RequestSendDataRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean save(NewPacket<ReqRequest> request, RequestLeakDataModel requestLeakDataModel) {

        log.info("repository : {}", request);
        log.info("repository : {}", requestLeakDataModel);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);

        String convertedSid = "`" + "leak_send_data_" + requestLeakDataModel.getSid();
        String convertedSerialNum = "_" + request.getSensorId() + "`";

        String totalTableName = convertedSid + convertedSerialNum;
        simpleJdbcInsert.withTableName(totalTableName).usingGeneratedKeyColumns("cid");

        simpleJdbcInsert.withTableName(totalTableName).usingGeneratedKeyColumns("cid");
        Map<String, Object> parameters = new HashMap<>();

        log.info("complete : {}", requestLeakDataModel.getComplete());
        log.info("fnum : {}", requestLeakDataModel.getFnum());


        parameters.put("cid", 1);
        parameters.put("pname", requestLeakDataModel.getPname());
        parameters.put("date", requestLeakDataModel.getDate());
        parameters.put("id", requestLeakDataModel.getId());
        parameters.put("ip", requestLeakDataModel.getIp());
        parameters.put("sid", requestLeakDataModel.getSid());
        parameters.put("valid", requestLeakDataModel.getValid());
        parameters.put("request_time", requestLeakDataModel.getRequestTime());
        parameters.put("fname", requestLeakDataModel.getFname());
        parameters.put("sn", request.getSensorId());
        parameters.put("complete", requestLeakDataModel.getComplete());
        parameters.put("complete_time", requestLeakDataModel.getCompleteTime());
        parameters.put("fnum", requestLeakDataModel.getFnum());
        parameters.put("inference", requestLeakDataModel.getInference());

        log.info("parameters : {}",parameters);

        Number key = simpleJdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        requestLeakDataModel.setCid(key.intValue());

        return true;
    }
}
