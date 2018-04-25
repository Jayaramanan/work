package com.ni3.ag.navigator.server.dao.impl.postgres;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.IAmDAO;

public class IAmDAOImpl extends JdbcDaoSupport implements IAmDAO{

	@Override
	public String getVersion(){
        final String sql = "SELECT version FROM sys_iam";
        return (String) getJdbcTemplate().queryForObject(sql, String.class);
	}

}
