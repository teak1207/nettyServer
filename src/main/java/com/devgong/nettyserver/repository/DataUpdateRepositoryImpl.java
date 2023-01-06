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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
@RequiredArgsConstructor
@Repository
public class DataUpdateRepositoryImpl implements DataUpdateRepository {

    private final DataSource dataSource;

    @Override
    public void updateCompleteTime(Integer cid, String sid, String sn) {

        String mixTableName1 = "`" + "leak_send_data_" + sid;
        String mixTableName2 = "_" + sn + "`";
        String convertedTableName = mixTableName1 + mixTableName2;


        log.info("mixTableName update check : {}", convertedTableName);

        String sql = "update " + convertedTableName + " set complete= ? " + "," + "complete_time=? " + " where cid=?";
        log.info("update sql check : {}", sql);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = dateFormat.format(new Date());


        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "1");
            pstmt.setString(2, dateTime);
            pstmt.setInt(3, cid);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, rs);
        }
    }
//
//    @Override
//    public boolean decrementFnum(String fname, String sid, String sn, int fnum) {
//        String mixTableName1 = "`" + "leak_send_data_" + sid;
//        String mixTableName2 = "_" + sn + "`";
//        String convertedTableName = mixTableName1 + mixTableName2;
//
//        log.info("mixTableName update check : {}", convertedTableName);
//
//        String sql = "update " + convertedTableName + " set fnum= ? " + " where fname=?";
//        log.info("update sql check : {}", sql);
//
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//
//        try {
//            conn = getConnection();
//            pstmt = conn.prepareStatement(sql);
//            pstmt.setString(1, String.valueOf(fnum - 1));
//            pstmt.setString(2, fname);
//            pstmt.executeUpdate();
//
//        } catch (
//                Exception e) {
//            e.printStackTrace();
//        } finally {
//            close(conn, pstmt, null);
//        }
//        return true;
//    }

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
