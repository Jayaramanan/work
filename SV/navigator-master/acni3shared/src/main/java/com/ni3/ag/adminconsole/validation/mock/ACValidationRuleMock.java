/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.mock;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ACValidationRuleMock implements ACValidationRule{

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return new ArrayList<ErrorEntry>();
	}

	@Override
	public boolean performCheck(AbstractModel model){
		return true;
	}

}
