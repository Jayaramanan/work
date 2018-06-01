/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.ReportTemplate;
import com.ni3.ag.adminconsole.server.dao.ReportTemplateDAO;

public class ReportTemplateDAOImpl extends HibernateDaoSupport implements ReportTemplateDAO{

	@Override
	public ReportTemplate getReportTemplate(Integer id){
		return (ReportTemplate) getHibernateTemplate().load(ReportTemplate.class, id);
	}

	@Override
	public ReportTemplate saveOrUpdate(ReportTemplate reportTemplate){
		getHibernateTemplate().saveOrUpdate(reportTemplate);
		return reportTemplate;
	}

	@Override
	public void deleteReportTemplate(ReportTemplate rt){
		getHibernateTemplate().delete(rt);
	}

	@Override
	public void saveOrUpdateAll(List<ReportTemplate> newReportTemplates){
		getHibernateTemplate().saveOrUpdateAll(newReportTemplates);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReportTemplate> getReportTemplates(){
		return (List<ReportTemplate>) getHibernateTemplate().loadAll(ReportTemplate.class);
	}

}
