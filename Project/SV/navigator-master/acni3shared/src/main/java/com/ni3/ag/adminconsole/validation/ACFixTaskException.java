/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ACFixTaskException extends Exception implements ErrorContainer, Serializable{
	private static final long serialVersionUID = -1710228272387176625L;
	private String className;
	private String message;

	public ACFixTaskException(String exClass, String message){
		this.className = exClass;
		this.message = message;
	}

	public List<ErrorEntry> getErrors(){
		List<ErrorEntry> ar = new ArrayList<ErrorEntry>();
		ar.add(new ErrorEntry(TextID.MsgEmpty, new String[] { getMessage() }));
		return ar;
	}

	public String getExceptionClassName(){
		return className;
	}

	@Override
	public String getMessage(){
		return message;
	}
}