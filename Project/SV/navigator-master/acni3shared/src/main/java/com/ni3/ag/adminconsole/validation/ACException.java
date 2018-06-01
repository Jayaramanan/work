/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ACException extends Exception implements ErrorContainer{

	private static final long serialVersionUID = 932940340291338524L;
	private List<ErrorEntry> errors = new ArrayList<ErrorEntry>();

	public ACException(){
	}

	public ACException(TextID id){
		this(id, new String[] {});
	};

	public ACException(TextID id, String[] params){
		errors.add(new ErrorEntry(id, params));
	}

	public ACException(List<ErrorEntry> errors){
		this.errors = errors;
	}

	public List<ErrorEntry> getErrors(){
		return errors;
	}

}
