package com.devgong.nettyserver.repository;

public interface DataUpdateRepository {

    boolean updateCompleteTime(String fname, String sid, String sn);

    boolean decrementFnum(String fname, String sid, String sn, int fnum);
}
