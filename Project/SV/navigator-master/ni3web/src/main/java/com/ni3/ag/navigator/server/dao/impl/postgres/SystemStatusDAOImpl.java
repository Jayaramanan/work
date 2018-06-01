/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao.impl.postgres;


import java.sql.Timestamp;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.SystemStatusDAO;

public class SystemStatusDAOImpl extends JdbcDaoSupport implements SystemStatusDAO{

	@Override
	public Timestamp getServerTime(){
		return (Timestamp) getJdbcTemplate().queryForObject("SELECT NOW()", Timestamp.class);
	}
}
