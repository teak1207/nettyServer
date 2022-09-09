package com.devgong.nettyserver.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@Table(name = "sensor_list_all")
public class SettingSensorListAllModel {


    @Id
    @Column(name = "CID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cid;
    @Column(name = "DATE")
    private String regdate;

    @Column(name = "SSN")
    private String ssn;

    @Column(name = "ASID")
    private String asid;

    @Column(name = "APROJECT")
    private String aproject;

    @Column(name = "MPHONE")
    private String mphone;

    @Column(name = "F_RESET")
    private String freset;



}
