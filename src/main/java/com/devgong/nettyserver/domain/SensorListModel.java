package com.devgong.nettyserver.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@Table(name = "sensor_list_all")
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SensorListModel {

    @Id
    @Column(name = "cid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cid;
    @Column(name = "REGDATE")
    private String regdate;

    @Column(name = "SSN")
    private String ssn;

    @Column(name = "ASID")
    private String asid;

    @Column(name = "APROJECT")
    private String aproject;
    @Column(name = "MPHONE")
    private String mphone;


}
