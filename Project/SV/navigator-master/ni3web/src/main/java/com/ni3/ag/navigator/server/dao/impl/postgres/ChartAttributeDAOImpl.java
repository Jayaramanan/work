package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.ChartAttributeDAO;
import com.ni3.ag.navigator.shared.domain.ChartAttribute;

public class ChartAttributeDAOImpl extends JdbcDaoSupport implements ChartAttributeDAO{
	@Override
	public List<ChartAttribute> getChartAttributes(int objectChartId){
		final String sql = "select id, objectchartid, attributeid, rgb "
		        + "from sys_chart_attribute where objectchartid = ? order by id";
		return getJdbcTemplate().query(sql, new Object[] { objectChartId }, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				ChartAttribute ca = new ChartAttribute();
				ca.setId(rs.getInt(1));
				ca.setObjectChartId(rs.getInt(2));
				ca.setAttributeId(rs.getInt(3));
				ca.setRgb(rs.getString(4));
				return ca;
			}
		});
	}
}
