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

    @Column(name = "sn")
    private String sn;

    @Column(name = "id")
    private String id;

    @Column(name = "ip")
    private String ip;

    @Column(name = "sid")
    private String sid;

    @Column(name = "pname")
    private String pname;

    @Column(name = "v_no")
    private String v_no;

    @Column(name = "factory_pname")
    private String factorypname;

}
