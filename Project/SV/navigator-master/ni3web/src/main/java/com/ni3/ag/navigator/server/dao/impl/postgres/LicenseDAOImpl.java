/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.util.List;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.navigator.server.dao.LicenseDAO;

public class LicenseDAOImpl extends JdbcDaoSupport implements LicenseDAO{
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getNavigatorLicenses(){
		String sql = "SELECT license from sys_licenses where product = ?";
		final List<String> licenses = getJdbcTemplate().queryForList(sql, new Object[] { LicenseData.NAVIGATOR_PRODUCT },
		        String.class);
		return licenses;
	}
}
