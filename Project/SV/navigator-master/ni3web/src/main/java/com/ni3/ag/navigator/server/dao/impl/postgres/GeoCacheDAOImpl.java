package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.GeoCacheDAO;

public class GeoCacheDAOImpl extends JdbcDaoSupport implements GeoCacheDAO {
    private static final Logger log = Logger.getLogger(GeoCacheDAOImpl.class);

    @Override
    public Set<String> getCacheTables() {
        Set<String> tables = new HashSet<String>();
        final String sql = "select tablename from gis_territory " +
                "where tablename is not null and length(trim(tablename)) > 0";
        tables.addAll(getJdbcTemplate().query(sql, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("tablename");
            }
        }));
        return tables;
    }

    @Override
    public void cleanNodeCache(int id, List<String> cacheTables) {
        log.debug("Clean node " + id + " from geo caches");
        final List<String> sqls = new ArrayList<String>();
        for (String s : cacheTables)
            sqls.add(makeDeleteSQL(id, s));
		if(!sqls.isEmpty())
        	getJdbcTemplate().batchUpdate(sqls.toArray(new String[sqls.size()]));
    }

    @Override
    public void updateCache(List<String> cacheTables) {
        log.debug("Call update cache procedures");
        for (String s : cacheTables)
            getJdbcTemplate().queryForObject(makeCallProcedureSQL(s), new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return null;
                }
            });
    }

    private String makeCallProcedureSQL(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append("select fillGeometryCache('").append(s).append("')");
        String sql = sb.toString();
        log.debug("Generated SQL: " + sql);
        return sql;
    }

    private String makeDeleteSQL(int id, String s) {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ").append(s).append("_mapping").append(" where nodeid = ").append(id);
        String sql = sb.toString();
        log.debug("Generated: " + sql);
        return sql;
    }
}
