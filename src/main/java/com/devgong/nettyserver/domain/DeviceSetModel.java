package com.devgong.nettyserver.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@Table(name = "leakset_bysensor")
public class DeviceSetModel {

    @Id
    @Column(name = "CID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cid;
    @Column(name = "SN")
    private String sn;
    @Column(name = "TIMEOPT1")
    private String time1;
    @Column(name = "TIMEOPT2")
    private String time2;
    @Column(name = "TIMEOPT3")
    private String time3;
    @Column(name = "PERIOD")
    private String preiod;
    @Column(name = "SAMPLE")
    private String sample;
    @Column(name = "SAMPLERATE")
    private String samplerate;

}
