/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.ReportTemplate;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ReportsModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ReportTemplateNameValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;
	private static Logger log = Logger.getLogger(ReportTemplateNameValidationRule.class);

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();
		ReportsModel rModel = (ReportsModel) model;
		List<ReportTemplate> reports = rModel.getReports();
		if (reports == null){
			return true;
		}

		for (ReportTemplate c : reports){
			log.debug("COMPARE: " + c.getName() + " == " + rModel.getNewReportTemplateName() + " "
			        + c.getName().equalsIgnoreCase(rModel.getNewReportTemplateName()));
			if (c.getName().equalsIgnoreCase(rModel.getNewReportTemplateName())){
				errors.add(new ErrorEntry(TextID.MsgDuplicateReportTemplateName, new String[] { c.getName() }));
				break;
			}
		}
		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}
}
