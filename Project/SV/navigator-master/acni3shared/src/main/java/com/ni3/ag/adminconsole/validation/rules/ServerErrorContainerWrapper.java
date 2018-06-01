/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;

public class ServerErrorContainerWrapper implements ErrorContainer{
	private ErrorContainer serverErrorContainer;

	public ServerErrorContainerWrapper(ErrorContainer sec){
		serverErrorContainer = sec;
	}

	public List<ErrorEntry> getErrors(){
		if (serverErrorContainer == null)
			return new ArrayList<ErrorEntry>();
		if (serverErrorContainer.getErrors() == null)
			return new ArrayList<ErrorEntry>();
		if (serverErrorContainer.getErrors().isEmpty())
			return new ArrayList<ErrorEntry>();
		ArrayList<ErrorEntry> ar = new ArrayList<ErrorEntry>();
		List<ErrorEntry> errs = serverErrorContainer.getErrors();
		for (ErrorEntry see : errs)
			ar.add(new ErrorEntry(see.getId(), see.getErrors()));
		return ar;
	}

}
