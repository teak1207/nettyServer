package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.TestModel;

import java.util.List;

public interface TestRepository {

//    TestModel save(TestModel testModel);

    TestModel save(TestModel testModel, String x, String y);

    List<TestModel> findAll();













/*
    @Query(value = "select * from scsol where cid= 3 ", nativeQuery = true)
    public List<TestModel> selectAllJPQL();

    @Modifying // select 문이 아님을 나타낸다
    @Transactional
    @Query(value = "insert into scsol_:mix values (:cid,:name,:age,:addr,:mix)", nativeQuery = true)
    void insertWithQuery(@Param("cid") int cid, @Param("name") String name, @Param("age") int age, @Param("addr") String addr, @Param("mix") String mix);
*/

}
