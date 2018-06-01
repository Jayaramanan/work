/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;

public class BasicErrorContainer implements ErrorContainer, Serializable{
	private static final long serialVersionUID = -3301694527490996525L;

	private List<ErrorEntry> errs = new ArrayList<ErrorEntry>();

	public BasicErrorContainer(){
	}

	public BasicErrorContainer(ErrorEntry err){
		errs.add(err);
	}

	public BasicErrorContainer(ErrorEntry[] err){
		for (int i = 0; i < err.length; i++)
			errs.add(err[i]);
	}

	public BasicErrorContainer(List<ErrorEntry> err){
		errs.addAll(err);
	}

	public void addError(ErrorEntry er){
		errs.add(er);
	}

	public List<ErrorEntry> getErrors(){
		return errs;
	}

}
