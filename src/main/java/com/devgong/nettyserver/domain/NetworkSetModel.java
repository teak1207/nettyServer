package com.devgong.nettyserver.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@Table(name = "leak_project")
public class NetworkSetModel {

    @Id
    @Column(name = "CID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cid;
    @Column(name = "SID")
    private String sid;
    @Column(name = "PNAME")
    private String pname;
    @Column(name = "DATA_SERVER")
    private String dataServer;
    @Column(name = "DATA_PORT")
    private String dataPort;


}
