/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.mock;

import com.ni3.ag.adminconsole.server.exporters.xml.XMLSchemaExporter;
import com.ni3.ag.adminconsole.validation.ACException;

public class ExporterMock<T, DC> extends XMLSchemaExporter<T, DC>{

	@Override
	protected void makeObjectExport(T target, DC dataContainer) throws ACException{
		// TODO Auto-generated method stub

	}

}
