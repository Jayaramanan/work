/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.List;

import org.hibernate.Hibernate;

import com.ni3.ag.adminconsole.domain.ReportTemplate;
import com.ni3.ag.adminconsole.server.dao.ReportTemplateDAO;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.shared.service.def.ReportsService;

public class ReportsServiceImpl implements ReportsService{

	private SchemaDAO schemaDAO;
	private ReportTemplateDAO reportTemplateDAO;

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public ReportTemplateDAO getReportTemplateDAO(){
		return reportTemplateDAO;
	}

	public void setReportTemplateDAO(ReportTemplateDAO reportTemplateDAO){
		this.reportTemplateDAO = reportTemplateDAO;
	}

	@Override
	public List<ReportTemplate> getReportTemplates(){
		List<ReportTemplate> reportTemplates = reportTemplateDAO.getReportTemplates();
		return reportTemplates;
	}

	@Override
	public ReportTemplate getReportTemplate(Integer id){
		ReportTemplate reportTemplate = reportTemplateDAO.getReportTemplate(id);
		Hibernate.initialize(reportTemplate);
		return reportTemplate;
	}

	@Override
	public ReportTemplate saveReportTemplate(ReportTemplate reportTemplate){
		return reportTemplateDAO.saveOrUpdate(reportTemplate);
	}

	@Override
	public void deleteReportTemplate(ReportTemplate rt){
		reportTemplateDAO.deleteReportTemplate(rt);
	}

}
