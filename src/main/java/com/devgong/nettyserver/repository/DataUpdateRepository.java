package com.devgong.nettyserver.repository;

public interface DataUpdateRepository {

    void updateCompleteTime(Integer cid, String sid, String sn);

//    boolean decrementFnum(String fname, String sid, String sn, int fnum);
}
