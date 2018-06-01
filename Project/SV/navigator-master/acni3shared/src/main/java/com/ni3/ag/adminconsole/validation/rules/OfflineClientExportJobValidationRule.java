/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.OfflineJob;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ThickClientModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class OfflineClientExportJobValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();

		ThickClientModel tcModel = (ThickClientModel) model;
		List<OfflineJob> jobs = tcModel.getJobs();
		if (jobs == null)
			return true;
		for (int i = 0; i < jobs.size(); i++){
			String u = jobs.get(i).getUserIds();
			if (u == null || u.isEmpty()){
				errors.add(new ErrorEntry(TextID.MsgSpecifyUser));
				break;
			}
		}

		return errors.isEmpty();
	}

}
