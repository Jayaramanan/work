/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.jobs.MapJobStatus;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.MapJobModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class MapJobDeleteRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

	@Override
	public boolean performCheck(AbstractModel m){
		errors = new ArrayList<ErrorEntry>();
		MapJobModel model = (MapJobModel) m;
		if (model.getCurrentJob() == null)
			return true;
		MapJob job = model.getCurrentJob();
		if (MapJobStatus.Compressing.getValue().equals(job.getStatus())
		        || MapJobStatus.CopyingToMapPath.getValue().equals(job.getStatus())
		        || MapJobStatus.CopyingToModulesPath.getValue().equals(job.getStatus())
		        || MapJobStatus.ProcessingMaps.getValue().equals(job.getStatus())){
			errors.add(new ErrorEntry(TextID.MsgCannotDeleteJobInProgress));
		}

		return errors.isEmpty();
	}

}
