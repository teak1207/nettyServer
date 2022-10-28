package com.devgong.nettyserver.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


@Slf4j
@RequiredArgsConstructor
@Repository
public class DataUpdateRepositoryImpl implements DataUpdateRepository {

    private final DataSource dataSource;

    @Override
    public boolean updateComleteTime(String fname, String sid, String sn) {

        String mixTableName1 = "`" + "leak_send_data_" + sid;
        String mixTableName2 = "_" + sn + "`";
        String convertedTableName = mixTableName1 + mixTableName2;
        LocalDate now = LocalDate.now();


        log.info("mixTableName update check : {}", convertedTableName);

        String sql = "update " + convertedTableName + " set complete= ? " + "," + "complete_time=?" + " where fname=?";
        log.info("update sql check : {}", sql);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "1");
            pstmt.setString(2, String.valueOf(now));
            pstmt.setString(3, fname);
            pstmt.executeUpdate();
            rs = pstmt.executeQuery();

/*            if (rs.next()) {
                DataLeakSendDataModel dataLeakSendDataModel = new DataLeakSendDataModel();
                dataLeakSendDataModel.setFname(rs.getString("fname"));
                return dataLeakSendDataModel.getFname();
            }*/

        } catch (
                Exception e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, rs);
        }
        return true;
    }


    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                close(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close(Connection conn) throws SQLException {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

}
