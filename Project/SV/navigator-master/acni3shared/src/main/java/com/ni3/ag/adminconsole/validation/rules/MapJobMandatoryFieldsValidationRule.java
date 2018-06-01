/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.jobs.MapJobStatus;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.MapJobModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class MapJobMandatoryFieldsValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		MapJobModel model = (MapJobModel) amodel;
		for (MapJob job : model.getJobs()){
			if (!MapJobStatus.Scheduled.getValue().equals(job.getStatus())){
				continue;
			}
			if (job.getUser() == null || job.getX1() == null || job.getX2() == null || job.getY1() == null
			        || job.getY2() == null || job.getScale() == null || job.getScale().isEmpty()){
				errors.add(new ErrorEntry(TextID.MsgNotAllRequeredFieldsHasValues));
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
