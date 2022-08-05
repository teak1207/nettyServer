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
@Table(name = "sensor_list")
public class SettingSensorListModel {
    @Id
    @Column(name = "cid")
    private int cid;

    @Column(name = "sn")
    private String serialNumber;

    @Column(name = "px")
    private String px;

    @Column(name = "py")
    private String py;

    @Column(name = "pname")
    private String pname;

    @Column(name = "sid")
    private String sid;

}
