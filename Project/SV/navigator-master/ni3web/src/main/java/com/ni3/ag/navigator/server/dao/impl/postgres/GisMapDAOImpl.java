package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.GisMapDAO;
import com.ni3.ag.navigator.shared.domain.GisMap;

public class GisMapDAOImpl extends JdbcDaoSupport implements GisMapDAO {
    private RowMapper gisMapMapper = new RowMapper() {
        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            GisMap map = new GisMap();
            map.setId(rs.getInt(1));
            map.setName(rs.getString(2));
            return map;

        }
    };

    @Override
    public GisMap get(int mapId) {
        final String sql = "select id, name from gis_map where id = ?";
        return (GisMap) getJdbcTemplate().queryForObject(sql, new Object[]{mapId}, gisMapMapper);
    }

    @Override
    public List<GisMap> getMaps() {
        final String sql = "SELECT id, name FROM GIS_Map ORDER BY ID";
        return getJdbcTemplate().query(sql, gisMapMapper);
    }
}
