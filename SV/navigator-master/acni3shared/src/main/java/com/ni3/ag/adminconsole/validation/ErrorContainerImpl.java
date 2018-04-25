/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ErrorContainerImpl implements Serializable, ErrorContainer{
	public static final long serialVersionUID = 1L;
	private List<ErrorEntry> errors = new ArrayList<ErrorEntry>();

	public void addError(TextID id, String[] params){
		errors.add(new ErrorEntry(id, params));
	}

	public void addError(TextID id){
		errors.add(new ErrorEntry(id));
	}

	public List<ErrorEntry> getErrors(){
		return errors;
	}

	public void addAllErrors(List<ErrorEntry> errors){
		this.errors.addAll(errors);
	}
}
