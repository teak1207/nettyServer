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
@Table(name = "factory_sensor_list")
public class SettingFactorySensorListModel {

    @Id
    @Column(name = "cid")
    int cid;

    @Column(name = "sid")
    private String sid;

    @Column(name = "pname")
    private String pname;

    @Column(name = "sn")
    private String sn;


    @Column(name = "factorypname")
    private String factorypname;

}
