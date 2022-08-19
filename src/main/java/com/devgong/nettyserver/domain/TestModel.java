package com.devgong.nettyserver.domain;

import lombok.*;

import javax.persistence.*;

@Data
public class TestModel {

    private int cid;
    private String name;
    private int age;
    private String addr;

    public TestModel(String name, int age, String addr) {
        this.name = name;
        this.age = age;
        this.addr = addr;
    }
/*    public TestModel(String name, int age, String addr, String mix) {
        this.name = name;
        this.age = age;
        this.addr = addr;
        this.mix = mix;
    }*/

}
