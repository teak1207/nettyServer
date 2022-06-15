package com.devgong.nettyserver.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@ToString
@Table(name = "sensor_list_all")
public class PreInstallSettingModel {


    @Id
    @Column(name = "CID")
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
