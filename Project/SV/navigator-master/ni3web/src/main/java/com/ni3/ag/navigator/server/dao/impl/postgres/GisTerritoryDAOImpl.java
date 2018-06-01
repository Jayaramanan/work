package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.GisTerritoryDAO;
import com.ni3.ag.navigator.shared.domain.GisTerritory;

public class GisTerritoryDAOImpl extends JdbcDaoSupport implements GisTerritoryDAO {
    private static final Logger log = Logger.getLogger(GisTerritoryDAOImpl.class);
    private RowMapper territoryMapper = new RowMapper() {
        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            GisTerritory gt = new GisTerritory();
            gt.setId(rs.getInt(1));
            gt.setTerritory(rs.getString(2));
            gt.setLabel(rs.getString(3));
            gt.setTableName(rs.getString("tableName"));
            gt.setDisplayColumn(rs.getString("displayColumn"));
            gt.setVersion(rs.getInt("version"));
            return gt;
        }
    };

    @Override
    public List<GisTerritory> getTerritories() {
        final String sql = "select id, territory, label, tableName, displayColumn, version from gis_territory order by sort";
        return getJdbcTemplate().query(sql, territoryMapper);
    }

    @Override
    public GisTerritory getTerritory(int id) {
        final String sql = "select id, territory, label, tableName, displayColumn, version from gis_territory where id = ?";
        return (GisTerritory) getJdbcTemplate().queryForObject(sql, new Object[]{id}, territoryMapper);
    }
}
