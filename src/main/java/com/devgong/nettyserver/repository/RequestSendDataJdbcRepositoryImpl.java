package com.devgong.nettyserver.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException ;


@Slf4j
@RequiredArgsConstructor
@Repository
public class RequestSendDataJdbcRepositoryImpl implements RequestSendDataJdbcRepository {

    //    final private JdbcTemplate template;
    private final DataSource dataSource;


    @Override
    public Pair<String, String> findSnAndFnameBySnAndSid(String sn, String sid) {

        String mixTableName1 = "`" + "leak_send_data_" + sid;
        String mixTableName2 = "_" + sn + "`";

        String convertedTableName = mixTableName1 + mixTableName2;

//        log.info("mixTableName check : {}", convertedTableName);

        String sql = "select cid, fname from " + convertedTableName + " where sid=? and sn =? " + " order by cid desc limit 1";

//        log.info("sql check : {}", sql);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sid);
            pstmt.setString(2, sn);
            rs = pstmt.executeQuery();

            if (rs.next()) {
//                DataLeakSendDataModel dataLeakSendDataModel = new DataLeakSendDataModel();
//                dataLeakSendDataModel.setFname(rs.getString("fname"));
//                return dataLeakSendDataModel.getFname();
                return Pair.of(rs.getString("sn"), rs.getString("fname"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, rs);
        }


        return null;
    }

//    @Override
//    public String getFnumOfReceivingSensorBySnAndSid(String fname, String sn, String sid) {
//        String mixTableName1 = "`" + "leak_send_data_" + sid;
//        String mixTableName2 = "_" + sn + "`";
//
//        String convertedTableName = mixTableName1 + mixTableName2;
//
//        log.info("mixTableName check : {}", convertedTableName);
//
//        String sql = "select fnum from " + convertedTableName + " where fname=? ";
//
//        log.info("sql check : {}", sql);
//
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//
//        try {
//            conn = getConnection();
//            pstmt = conn.prepareStatement(sql);
//            pstmt.setString(1, fname);
//            rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                return rs.getString("fnum");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            close(conn, pstmt, rs);
//        }
//
//        return null;
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

//    @Override
//    public Boolean updateFnum(String fname, String sn, String sid) {
//
//        String mixTableName1 = "`" + "leak_send_data_" + sid;
//        String mixTableName2 = "_" + sn + "`";
//        String convertedTableName = mixTableName1 + mixTableName2;
//
//
//        log.info("mixTableName update check : {}", convertedTableName);
//
//        String sql = "update " + convertedTableName + " set fnum= ? " + " where fname=?";
//        log.info("update sql check : {}", sql);
//
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//
//        try {
//            conn = getConnection();
//            pstmt = conn.prepareStatement(sql);
//            pstmt.setString(1, "0");
//            pstmt.setString(2, fname);
//            pstmt.executeUpdate();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            close(conn, pstmt, rs);
//        }
//        return true;
//
//
//    }


    private void close(Connection conn) throws SQLException {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

}
