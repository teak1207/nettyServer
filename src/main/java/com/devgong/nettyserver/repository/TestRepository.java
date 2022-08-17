package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.TestModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<TestModel, Integer> {


    @Query(value = "select * from scsol where cid= 3 ", nativeQuery = true)
    public List<TestModel> selectAllJPQL();

    @Modifying // select 문이 아님을 나타낸다
    @Transactional
    @Query(value = "insert into scsol  values (:cid,:name,:age,:addr)", nativeQuery = true)
    void insertWithQuery(@Param("cid") int cid, @Param("name") String name, @Param("age") int age, @Param("addr") String addr);

}
