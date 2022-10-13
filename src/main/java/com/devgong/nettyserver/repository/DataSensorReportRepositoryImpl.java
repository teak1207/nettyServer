package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.DataInsertModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Repository
public class DataSensorReportRepositoryImpl implements DataSensorReportRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public DataInsertModel save(DataInsertModel dataInsertModel, String sid, String project, String serialNumber) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);

        String convertedSid = "`" + "sensor_report_" + sid;
        String convertedSerialNum = "_" + serialNumber + "`";

//        String mix = convertedSid + convertedSerialNum.toLowerCase();
        String mixTableName = convertedSid + convertedSerialNum;
        simpleJdbcInsert.withTableName(mixTableName).usingGeneratedKeyColumns("cid");

        log.info("dataInsertModel: {}", dataInsertModel);
        log.info("mixTableName : {}", mixTableName);

        Map<String, Object> parameters = new HashMap<>();

        Date now = new Date();

        parameters.put("cid", 1);
        parameters.put("serialNumber", serialNumber);
        parameters.put("date", now);
        parameters.put("id", "admin");
        parameters.put("ip", "-1-1");

        parameters.put("px", dataInsertModel.getPx());

        parameters.put("sid", sid);

        parameters.put("py", dataInsertModel.getPy());

        parameters.put("pname", dataInsertModel.getModemNumber());

        parameters.put("time1", dataInsertModel.getTime1());
        parameters.put("time2", dataInsertModel.getTime2());
        parameters.put("time3", dataInsertModel.getTime3());

        parameters.put("end_record_time", dataInsertModel.getEndRecordTime());

        parameters.put("fm", dataInsertModel.getFmFrequency());

        parameters.put("firmwareVersion", dataInsertModel.getFirmwareVersion());

        parameters.put("rssi", dataInsertModel.getRSSI());

        parameters.put("status", dataInsertModel.getDeviceStatus());

        parameters.put("sample", dataInsertModel.getSamplingTime());

        parameters.put("period", dataInsertModel.getPeriod());
        parameters.put("battery", dataInsertModel.getBatteryVtg());
        parameters.put("project", project);
        parameters.put("server_url", dataInsertModel.getServerUrl());
        parameters.put("server_port", dataInsertModel.getServerPort());
        parameters.put("db_url", dataInsertModel.getDbUrl());
        parameters.put("db_port", dataInsertModel.getDbPort());
        parameters.put("fmtime", dataInsertModel.getRadioTime());
        parameters.put("samplerate", dataInsertModel.getSampleRate());
        parameters.put("sleep", dataInsertModel.getSleep());
        parameters.put("active", dataInsertModel.getActive());
        parameters.put("reset", dataInsertModel.getReset());
        parameters.put("f_reset", dataInsertModel.getFReset());


        Number key = simpleJdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        dataInsertModel.setCid(key.intValue());


        return dataInsertModel;
    }


}
