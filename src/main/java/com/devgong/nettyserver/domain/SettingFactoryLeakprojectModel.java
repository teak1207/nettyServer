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
@Table(name = "factory_leak_project")
public class SettingFactoryLeakprojectModel {

    @Id
    @Column(name = "cid")
    int cid;

    @Column(name = "factory_pname")
    private String factoryPname;

    @Column(name = "data_URL")
    private String dataURL;

    @Column(name = "data_PORT")
    private String dataPORT;


    @Column(name = "db_URL")
    private String dbURL;

    @Column(name = "db_PORT")
    private String dbPORT;

}
