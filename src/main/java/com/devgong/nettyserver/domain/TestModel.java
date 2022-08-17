package com.devgong.nettyserver.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor

@Table(name = "scsol")

public class TestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cid;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @Column(name = "addr")
    private String addr;


    public TestModel(String name, int age, String addr) {
        this.name = name;
        this.age = age;
        this.addr = addr;
    }
}
