/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ACLoginException extends Exception implements ErrorContainer, Serializable{

	private static final long serialVersionUID = -3332610051505040240L;

	public ACLoginException(String message){
		super(message);
	}

	public List<ErrorEntry> getErrors(){
		List<ErrorEntry> ar = new ArrayList<ErrorEntry>();
		ar.add(new ErrorEntry(TextID.MsgEmpty, new String[] { getMessage() }));
		return ar;
	}

}
