package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.DataInsertModel;

public interface DataSensorReportRepository {

    DataInsertModel save(DataInsertModel dataInsertModel, String sid, String project, String serialNumber);

}
