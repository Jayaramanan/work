/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import com.ni3.ag.navigator.server.dao.ThematicClusterDAO;
import com.ni3.ag.navigator.shared.domain.ThematicCluster;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ThematicClusterDAOImpl extends JdbcDaoSupport implements ThematicClusterDAO {
    @Override
    public void insertThematicCluster(ThematicCluster cluster) {
        final String sql = "INSERT INTO geo_thematiccluster (thematicmapid, fromvalue, tovalue, color, gIds, description) VALUES (?, ?, ?, ?, ?, ?);";
        getJdbcTemplate().update(sql, new Object[]{cluster.getThematicMapId(), cluster.getFromValue(),
                cluster.getToValue(), cluster.getColor(), cluster.getGisIds(), cluster.getDescription()});
    }

    @Override
    public void deleteClustersByThematicMapId(int thematicMapId) {
        final String sql = "delete from geo_thematiccluster where thematicmapid = ?";
        getJdbcTemplate().update(sql, new Object[]{thematicMapId});
    }

    @Override
    public List<ThematicCluster> getClustersByThematicMapId(int thematicMapId) {
        final String selectSql = "SELECT id, fromvalue, tovalue, color, gids, description from geo_thematiccluster " +
                "where thematicmapid = ? order by id";
        return getJdbcTemplate().query(selectSql, new Object[]{thematicMapId}, new RowMapper() {
            @Override
            public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                ThematicCluster cluster = new ThematicCluster();
                cluster.setId(resultSet.getInt("id"));
                cluster.setFromValue(resultSet.getDouble("fromvalue"));
                cluster.setToValue(resultSet.getDouble("tovalue"));
                cluster.setColor(resultSet.getString("color"));
                cluster.setGisIds(resultSet.getString("gids"));
                cluster.setDescription(resultSet.getString("description"));
                return cluster;
            }
        });
    }

	@Override
	public void insertThematicClustersWithIds(final List<ThematicCluster> clusters){
		final String sql = "INSERT INTO geo_thematiccluster (id, thematicmapid, fromvalue, tovalue, " +
				"color, gIds, description) VALUES (?, ?, ?, ?, ?, ?, ?);";
		BatchPreparedStatementSetter batchSetter = new BatchPreparedStatementSetter(){
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException{
				ThematicCluster tc = clusters.get(i);
				ps.setInt(1, tc.getId());
				ps.setInt(2, tc.getThematicMapId());
				ps.setDouble(3, tc.getFromValue());
				ps.setDouble(4, tc.getToValue());
				ps.setString(5, tc.getColor());
				ps.setString(6, tc.getGisIds());
				if(tc.getDescription() == null)
					ps.setNull(7, Types.VARCHAR);
				else
					ps.setString(7, tc.getDescription());
			}

			@Override
			public int getBatchSize(){
				return clusters.size();
			}
		};
		getJdbcTemplate().batchUpdate(sql, batchSetter);
	}
}
