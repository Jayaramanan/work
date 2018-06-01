/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.ReportDAO;
import com.ni3.ag.navigator.server.domain.ReportTemplate;
import com.ni3.ag.navigator.server.domain.ReportType;

public class ReportDAOImpl extends JdbcDaoSupport implements ReportDAO{
	private static final Logger log = Logger.getLogger(ReportDAOImpl.class);

	private RowMapper reportRowMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
			ReportTemplate report;
			report = new ReportTemplate();
			report.setId(resultSet.getInt("id"));
			report.setName(resultSet.getString("name"));
			final String xml = resultSet.getString("xml");
			if (xml != null){
				try{
					report.setTemplate(xml.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e){
					log.error("Error get xml content");
					throw new SQLException("Error get xml content", e);
				}
			}
			int type = resultSet.getInt("type");
			report.setType(ReportType.fromValue(type));
			report.setPreviewIcon(resultSet.getBytes("preview"));
			return report;
		}
	};

	@Override
	public ReportTemplate getReportTemplate(int reportId){
		final String sql = "SELECT id, name, xml, preview, coalesce(type,1) as type from sys_report_template where id = ?";
		return (ReportTemplate) getJdbcTemplate().queryForObject(sql, new Object[] { reportId }, reportRowMapper);
	}

	@Override
	public List<ReportTemplate> getReportTemplates(){
		final String sql = "SELECT id, name, xml, preview, coalesce(type,1) as type from sys_report_template";
		return getJdbcTemplate().query(sql, reportRowMapper);
	}
}
