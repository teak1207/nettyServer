package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.TestModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class TestRepositoryImpl implements TestRepository {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public TestModel save(TestModel testModel,String x ,String y) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName(x+y).usingGeneratedKeyColumns("cid");

        Map<String,Object> parameters = new HashMap<>();
        parameters.put("name",testModel.getName());
        parameters.put("age",testModel.getAge());
        parameters.put("addr",testModel.getAddr());


        Number key = simpleJdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        testModel.setCid(key.intValue());
        return testModel;
    }

/*    @Override
    public TestModel save(TestModel testModel) {
        return null;
    }*/

    @Override
    public List<TestModel> findAll() {
        return null;
    }
}
