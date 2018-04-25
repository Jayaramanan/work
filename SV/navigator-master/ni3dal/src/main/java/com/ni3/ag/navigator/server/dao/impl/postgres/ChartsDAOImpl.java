package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.ChartsDAO;
import com.ni3.ag.navigator.shared.domain.Chart;

public class ChartsDAOImpl extends JdbcDaoSupport implements ChartsDAO{
	private static RowMapper ChartRowMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
			Chart ch = new Chart();
			ch.setId(rs.getInt(1));
			ch.setName(rs.getString(2));
			ch.setComment(rs.getString(3));
			ch.setSchemaId(rs.getInt(4));
			return ch;
		}
	};

	@Override
	public List<Chart> getChartsForGroup(int groupId, int schemaId){
		String sql = "SELECT id, name, comment, schemaid FROM SYS_CHART "
		        + "WHERE ID IN (SELECT ChartID FROM SYS_Chart_Group " + "WHERE GroupID IN (?)) and SchemaID=? ORDER BY id";
		return getJdbcTemplate().query(sql, new Object[] { groupId, schemaId }, ChartRowMapper);
	}

	@Override
	public Chart getChart(int chartId){
		String sql = "SELECT id, name, comment, schemaid FROM SYS_CHART WHERE ID = ?";
		return (Chart) getJdbcTemplate().queryForObject(sql, new Object[] { chartId }, ChartRowMapper);
	}

}
