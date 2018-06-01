/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.IconDAO;
import com.ni3.ag.navigator.server.domain.Icon;

public class IconDAOImpl extends JdbcDaoSupport implements IconDAO{
	private static final Logger log = Logger.getLogger(IconDAOImpl.class);
	private RowMapper iconRowMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
			return rs.getBytes("icon");
		}
	};

	@Override
	public byte[] getImageBytes(String iconName){
		final String sql = "SELECT icon from cht_icons where iconname = ?";
		return (byte[]) getJdbcTemplate().queryForObject(sql, new Object[] { iconName }, iconRowMapper);
	}

	@Override
	public List<String> getIconNames(){
		final String sql = "SELECT iconname from cht_icons";
		return getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				return rs.getString("iconname");
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Icon> getIcons(){
		String sql = "select id, iconname from cht_icons";
		if (log.isDebugEnabled()){
			log.debug("SQL: " + sql);
		}

		List<Icon> icons = (List<Icon>) getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				Icon icon = new Icon(rs.getInt(1), rs.getString(2));
				return icon;
			}
		});
		if (log.isDebugEnabled()){
			log.debug("Got local icons count: " + icons.size());
		}
		return icons;
	}

	@Override
	public boolean saveIcon(Icon i){
		String sql = "insert into cht_icons(id, iconname) values(" + i.getId() + ", '" + i.getIconName() + "')";
		log.debug("execute: " + sql);
		return getJdbcTemplate().update(sql) > 0;
	}
}
