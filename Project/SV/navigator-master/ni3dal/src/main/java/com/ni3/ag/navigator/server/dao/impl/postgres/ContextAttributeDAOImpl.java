package com.ni3.ag.navigator.server.dao.impl.postgres;

import com.ni3.ag.navigator.server.dao.ContextAttributeDAO;
import com.ni3.ag.navigator.server.domain.ContextAttribute;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ContextAttributeDAOImpl extends JdbcDaoSupport implements ContextAttributeDAO{

	@Override
	public List<ContextAttribute> findByContextId(Integer id){
		// @formatter:off
		final String query = "SELECT " + "id,contextid,attributeid " + "FROM " + "SYS_CONTEXT_ATTRIBUTES " + "WHERE "
				+ "contextid=?";
		// @formatter:on
		return getJdbcTemplate().query(query, new Object[]{id}, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				final ContextAttribute attribute = new ContextAttribute();
				attribute.setId(rs.getInt("id"));
				attribute.setContextId(rs.getInt("contextid"));
				attribute.setAttributeId(rs.getInt("attributeid"));
				return attribute;
			}
		});
	}

}
