/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.dto.ErrorEntry;

public class ViewMock extends Component implements AbstractView, ErrorRenderer{

	private static final Logger log = Logger.getLogger(ViewMock.class);
	private static final long serialVersionUID = 1L;

	private List<ErrorEntry> errors = new ArrayList<ErrorEntry>();

	@Override
	public void renderErrors(List<ErrorEntry> errors){
		for (ErrorEntry ee : errors){
			log.debug(ee.getId() + ":");
			for (String s : ee.getErrors())
				log.debug("   " + s);
			this.errors.add(ee);
		}
	}

	public List<ErrorEntry> getErrors(){
		return errors;
	}

	@Override
	public void initializeComponents(){

	}

	@Override
	public void resetEditedFields(){

	}

	@Override
	public boolean isChanged(){
		return false;
	}

	@Override
	public void restoreSelection(){

	}

}
