package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.GeoCodeErrDAO;
import com.ni3.ag.navigator.server.geocode.data.GeoCodeError;
import com.ni3.ag.navigator.server.geocode.data.Location;

public class GeoCodeErrorDAOImpl extends JdbcDaoSupport implements GeoCodeErrDAO {

    @Override
    public void updateErrorResult(int id, String sAddr, Location adr) {
        final String sql = "update sys_geocode_error set request = ?, status = ?, description = ? where nodeid = ?";
        getJdbcTemplate().update(sql, new Object[]{sAddr == null ? "" : sAddr, adr.status.getCode(),
                adr.status.getDescription(), id});
    }

    @Override
    public void insertErrorResult(int id, String sAddr, Location adr) {
        final String sql = "insert into sys_geocode_error(nodeid, request, status, description) values (?, ?, ?, ?)";
        getJdbcTemplate().update(sql, new Object[]{id, sAddr == null ? "" : sAddr, adr.status.getCode(),
                adr.status.getDescription()});
    }

    @Override
    public Map<Integer, GeoCodeError> getPreviousErrorsMap() {
        final HashMap<Integer, GeoCodeError> nodesToSkip = new HashMap<Integer, GeoCodeError>();
        final String sql = "select nodeid, status, request from sys_geocode_error";
        getJdbcTemplate().query(sql, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                nodesToSkip.put(rs.getInt(1), new GeoCodeError(rs.getInt(2), rs.getString(3)));
                return null;
            }
        });
        return nodesToSkip;
    }

    @Override
    public void removeError(int id) {
        final String sql = "delete from sys_geocode_error where nodeid = ?";
        getJdbcTemplate().update(sql, new Object[]{id});
    }
}
