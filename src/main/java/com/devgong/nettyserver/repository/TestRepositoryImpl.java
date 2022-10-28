package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.DataLeakSendDataModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Repository
public class TestRepositoryImpl implements TestRepository {

    final private JdbcTemplate template;
    private final DataSource dataSource;

    @Override
    public String selectBySnAndSid(String sn, String sid) {

        String mixTableName = "leak_send_data"+"_" + sid + "_" + sn;

        String sql = "select fname from " + mixTableName + "where sid=? and sn =?"+"order by cid desc limit 1";

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
                DataLeakSendDataModel dataLeakSendDataModel = new DataLeakSendDataModel();
                dataLeakSendDataModel.setFname("fname");
                return dataLeakSendDataModel.getFname();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, rs);
        }


        return null;
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
