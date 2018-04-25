/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.w3c.dom.Element;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class ObjectDefinitionExporter extends XMLSchemaExporter<Element, ObjectDefinition>{
	private final static Logger log = Logger.getLogger(ObjectDefinitionExporter.class);
	private XMLSchemaExporter<Element, ObjectAttribute> attributeExporter;
	private XMLSchemaExporter<Element, ObjectConnection> connectionExporter;

	public void setAttributeExporter(XMLSchemaExporter<Element, ObjectAttribute> attributeExporter){
		this.attributeExporter = attributeExporter;
	}

	public void setConnectionExporter(XMLSchemaExporter<Element, ObjectConnection> connectionExporter){
		this.connectionExporter = connectionExporter;
	}

	@Override
	protected boolean validateDataContainer(ObjectDefinition od){
		if (od.getObjectType() == null){
			validationErrors.add(new ErrorEntry(TextID.MsgNoPropertySet, new String[] { od.getName() + " => object type" }));
			return false;
		}
		return true;
	}

	@Override
	protected void makeObjectExport(Element parent, ObjectDefinition od) throws ACException{
		Element odElem = document.createElement("objectDefinition");
		log.debug("exporting object definition `" + od.getName() + "`");

		odElem.setAttribute("type", od.getObjectType().getLabel());
		odElem.setAttribute("name", od.getName());
		odElem.setAttribute("description", od.getDescription());
		odElem.setAttribute("sort", String.valueOf(od.getSort()));
		parent.appendChild(odElem);

		Hibernate.initialize(od.getObjectConnections());
		Hibernate.initialize(od.getObjectAttributes());

		Element oaElem = document.createElement("objectAttributes");
		odElem.appendChild(oaElem);
		for (ObjectAttribute oa : od.getObjectAttributes())
			attributeExporter.export(document, oaElem, oa);

		Element connElem = document.createElement("connections");
		odElem.appendChild(connElem);
		for (ObjectConnection oc : od.getObjectConnections())
			connectionExporter.export(document, connElem, oc);

	}

}
