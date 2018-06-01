/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.server.exporters.AbstractExporter;
import com.ni3.ag.adminconsole.validation.ACException;

public abstract class XMLSchemaExporter<T, DC> extends AbstractExporter<T, DC>{

	protected Document document;
	protected List<ErrorEntry> validationErrors;

	public void export(Document document, T target, DC dataContainer) throws ACException{
		this.document = document;
		this.validationErrors = new ArrayList<ErrorEntry>();
		if (validateDataContainer(dataContainer))
			super.export(target, dataContainer);
		else
			throw new ACException(validationErrors);
	}

	protected boolean validateDataContainer(DC dataContainer){
		return true;
	}

}
