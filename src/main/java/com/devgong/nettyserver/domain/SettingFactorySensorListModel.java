package com.devgong.nettyserver.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "factory_sensor_list")
// danger: 다른 모델들도 살펴봐야 함 entity에는 무조건 붙어야 하는 애노테이션
// danger: Set로 붙어야 하는게 웬만해서는 Getter, Entity, Table, NoArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
