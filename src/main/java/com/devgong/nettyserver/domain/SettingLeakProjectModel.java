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
@ToString
@Entity
@NoArgsConstructor
@Table(name = "leak_project ")
public class SettingLeakProjectModel {

    @Id
    @Column(name = "cid")
    private int cid;

    @Column(name = "SID")
    private String sid;

    @Column(name = "PNAME")
    private String factorypPname;

    @Column(name = "data_server")
    private String data_URL;

    @Column(name = "data_port")
    private String data_PORT;

    @Column(name = "db_server")
    private String db_URL;

    @Column(name = "db_port")
    private String db_PORT;


}
