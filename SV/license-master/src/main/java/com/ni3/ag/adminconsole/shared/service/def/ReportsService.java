/** * Copyright (c) 2009-2011 Ni3 AG. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.ReportTemplate;

public interface ReportsService{

	ReportTemplate getReportTemplate(Integer id);

	ReportTemplate saveReportTemplate(ReportTemplate reportTemplate);

	void deleteReportTemplate(ReportTemplate rt);

	List<ReportTemplate> getReportTemplates();

}
