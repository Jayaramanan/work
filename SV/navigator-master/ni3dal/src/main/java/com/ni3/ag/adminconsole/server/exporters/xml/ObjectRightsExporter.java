/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.validation.ACException;

public class ObjectRightsExporter extends XMLSchemaExporter<Element, ObjectGroup>{
	private final static Logger log = Logger.getLogger(ObjectRightsExporter.class);
	private XMLSchemaExporter<Element, AttributeGroup> attributeRightsExporter;

	public void setAttributeRightsExporter(XMLSchemaExporter<Element, AttributeGroup> attributeRightsExporter){
		this.attributeRightsExporter = attributeRightsExporter;
	}

	@Override
	protected void makeObjectExport(Element target, ObjectGroup oug) throws ACException{
		ObjectDefinition od = oug.getObject();
		log.debug("exporting object right for object `" + od.getName() + "`");

		Element objRightElem = document.createElement("objectRight");

		objRightElem.setAttribute("object", od.getName());
		objRightElem.setAttribute("canRead", oug.isCanRead() ? "1" : "0");
		objRightElem.setAttribute("canCreate", oug.isCanCreate() ? "1" : "0");
		objRightElem.setAttribute("canUpdate", oug.isCanUpdate() ? "1" : "0");
		objRightElem.setAttribute("canDelete", oug.isCanDelete() ? "1" : "0");
		target.appendChild(objRightElem);

		for (AttributeGroup ag : oug.getGroup().getAttributeGroups()){
			ObjectDefinition oaObject = ag.getObjectAttribute().getObjectDefinition();
			if (od.equals(oaObject))
				attributeRightsExporter.export(document, objRightElem, ag);
		}

	}

}
