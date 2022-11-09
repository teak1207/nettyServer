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

/**
 * @author devGong
 * @version 1.0
 * Report 에서 관련 테이블들을 참조.
 */


@Slf4j
@RequiredArgsConstructor
@Repository
public class DataSensorReportRepositoryImpl implements DataSensorReportRepository {

    //report_seq : 가변적인 테이블명 -> JDBC 로 DB 접근.
    //report_seq : sensor_report_(sid)_(serialNumber) 테이블 insert.
    private final JdbcTemplate jdbcTemplate;


    /**
     * @param dataInsertModel - 테이블의 저장 될 값.
     * @param serialNumber    - 테이블 네임 조합을 위함.
     * @param sid             - 테이블 네임 조합을 위함.
     * @param project         - 테이블의 저장 될 값.
     * @author devGong
     * (1) serialNumber 로 DB에서 해당 값을 가져오고, preInstallSensorListAllModel 리턴합니다.
     */

    @Override
    public DataInsertModel save(DataInsertModel dataInsertModel, String sid, String project, String serialNumber) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);

        String convertedSid = "`" + "sensor_report_" + sid;
        String convertedSerialNum = "_" + serialNumber + "`";

        String totalTableName = convertedSid + convertedSerialNum;
        simpleJdbcInsert.withTableName(totalTableName).usingGeneratedKeyColumns("cid");

        Map<String, Object> parameters = new HashMap<>();

        Date now = new Date();

        parameters.put("cid", 1);
        parameters.put("sn", dataInsertModel.getSn());
        parameters.put("date", now);
        parameters.put("id", "admin");
        parameters.put("ip", "-1-1");
        parameters.put("px", dataInsertModel.getPx());
        parameters.put("sid", sid);
        parameters.put("py", dataInsertModel.getPy());
        parameters.put("pname", dataInsertModel.getPname());
        parameters.put("time1", dataInsertModel.getRecordTime1());
        parameters.put("time2", dataInsertModel.getRecordTime2());
        parameters.put("time3", dataInsertModel.getRecordTime3());
        parameters.put("end_record_time", dataInsertModel.getEndRecordTime());
        parameters.put("fm", dataInsertModel.getFmRadio());
        parameters.put("fver", dataInsertModel.getFirmwareVersion());
        parameters.put("rssi", dataInsertModel.getModernRssi());
        parameters.put("status", dataInsertModel.getDeviceStatus());
        parameters.put("sample", dataInsertModel.getSamplingTime());
        parameters.put("period", dataInsertModel.getPeriod());
        parameters.put("batt", dataInsertModel.getBatteryValue());
        parameters.put("project", project);
        parameters.put("server_url", dataInsertModel.getServerUrl());
        parameters.put("server_port", dataInsertModel.getServerPort());
        parameters.put("db_url", dataInsertModel.getDbUrl());
        parameters.put("db_port", dataInsertModel.getDbPort());
        parameters.put("fmtime", dataInsertModel.getRadioTime());
        parameters.put("creg", dataInsertModel.getCregCount());
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
