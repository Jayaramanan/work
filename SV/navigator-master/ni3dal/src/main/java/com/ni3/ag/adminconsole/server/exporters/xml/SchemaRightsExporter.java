/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.SchemaGroup;
import com.ni3.ag.adminconsole.validation.ACException;

public class SchemaRightsExporter extends XMLSchemaExporter<Element, SchemaGroup>{
	private final static Logger log = Logger.getLogger(SchemaRightsExporter.class);
	private XMLSchemaExporter<Element, ObjectGroup> objectRightsExporter;

	public void setObjectRightsExporter(XMLSchemaExporter<Element, ObjectGroup> objectRightsExporter){
		this.objectRightsExporter = objectRightsExporter;
	}

	@Override
	protected void makeObjectExport(Element target, SchemaGroup sg) throws ACException{
		Schema schema = sg.getSchema();
		log.debug("exporting schema right for schema `" + schema.getName() + "`");

		Element objRightElem = document.createElement("schemaRight");

		objRightElem.setAttribute("canRead", sg.isCanRead() ? "1" : "0");
		target.appendChild(objRightElem);

		for (ObjectGroup og : sg.getGroup().getObjectGroups()){
			Schema ogSchema = og.getObject().getSchema();
			if (schema.equals(ogSchema))
				objectRightsExporter.export(document, objRightElem, og);
		}
	}

}
