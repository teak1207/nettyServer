package com.devgong.nettyserver.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "leakset")
public class SettingLeaksetModel {

    @Id
    @Column(name = "cid")
    private int cid;
    @Column(name = "time1")
    private String time1;

    @Column(name = "time2")
    private String time2;

    @Column(name = "time3")
    private String time3;

    @Column(name = "fm")
    private String fmFrequency;

    @Column(name = "sid")
    private String sid;

    @Column(name = "pname")
    private String pname;

    @Column(name = "sleep")
    private String sleep;

    @Column(name = "reset")
    private String reset;

    @Column(name = "period")
    private String period;

    @Column(name = "sample")
    private String sampletime;

    @Column(name = "active")
    private String active;

    @Column(name = "samplerate")
    private String samplerate;

    @Column(name = "fmtime")
    private String fmtime;

    @Column(name = "tdate")
    private String tdate;

    @Column(name = "sn")
    private String sn;



}
