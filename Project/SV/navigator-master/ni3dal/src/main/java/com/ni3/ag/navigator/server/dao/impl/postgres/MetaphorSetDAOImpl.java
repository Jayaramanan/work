package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.MetaphorSetDAO;

public class MetaphorSetDAOImpl extends JdbcDaoSupport implements MetaphorSetDAO{
	private static final Logger log = Logger.getLogger(MetaphorSetDAOImpl.class);

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getMetaphorSets(int schemaId){
		String sql = "SELECT DISTINCT MetaphorSet FROM SYS_Metaphor WHERE SchemaID=? ORDER BY " +
				"MetaphorSet";
		return getJdbcTemplate().queryForList(sql, new Object[]{schemaId}, String.class);
	}
}
