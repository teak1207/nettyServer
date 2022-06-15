package com.devgong.nettyserver.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@Table(name = "preinstall")
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreInstallModel {

    @Id
    @Column(name = "cid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cid;
    @Column(name = "MODEMNUM")
    private String modemNumber;

    @Column(name = "FLAG")
    private char flag;

    @Column(name = "SERIALNUM")
    private String serialNumber;
    @Column(name = "SIGNALTIME")
    private String dateTime;
    @Column(name = "PARALEN")
    private String paraLen;
    @Column(name = "DEBUGMSG")
    private String debugMsg;
    @Column(name = "CHKSUM")
    private String chksum;




}
