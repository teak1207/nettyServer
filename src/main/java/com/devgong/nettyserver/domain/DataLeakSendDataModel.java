package com.devgong.nettyserver.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@Table(name = "test")
public class DataLeakSendDataModel {
    @Id
    @Column(name = "CID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cid;

    @Column(name = "FNAME")
    private String fname;


}
