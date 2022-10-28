package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.DataUpdateModel;

public interface DataUpdateRepository {

    boolean updateComleteTime(String fname,String sid,String sn);



}
