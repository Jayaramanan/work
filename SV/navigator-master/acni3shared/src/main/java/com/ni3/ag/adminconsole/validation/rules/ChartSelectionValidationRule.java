/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ChartSelectionValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

	@Override
	public boolean performCheck(AbstractModel aModel){
		errors = new ArrayList<ErrorEntry>();
		ChartModel model = ((ChartModel) aModel);
		if (!model.isChartSelected()){
			errors.add(new ErrorEntry(TextID.MsgSelectChart));
		}
		return errors.isEmpty();
	}

}
