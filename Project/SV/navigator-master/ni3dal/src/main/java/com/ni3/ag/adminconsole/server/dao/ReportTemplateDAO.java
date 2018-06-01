/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.ReportTemplate;

public interface ReportTemplateDAO{

	ReportTemplate getReportTemplate(Integer id);

	ReportTemplate saveOrUpdate(ReportTemplate reportTemplate);

	void deleteReportTemplate(ReportTemplate rt);

	void saveOrUpdateAll(List<ReportTemplate> newReportTemplates);

	List<ReportTemplate> getReportTemplates();

}
