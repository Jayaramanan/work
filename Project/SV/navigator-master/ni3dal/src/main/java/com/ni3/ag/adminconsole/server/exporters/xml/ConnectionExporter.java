/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.ObjectsConnectionsService;
import com.ni3.ag.adminconsole.validation.ACException;

public class ConnectionExporter extends XMLSchemaExporter<Element, ObjectConnection>{
	private final static Logger log = Logger.getLogger(ConnectionExporter.class);

	private ObjectsConnectionsService objectsConnectionsService;

	public void setObjectsConnectionsService(ObjectsConnectionsService objectsConnectionsService){
		this.objectsConnectionsService = objectsConnectionsService;
	}

	@Override
	protected boolean validateDataContainer(ObjectConnection oc){
		PredefinedAttribute pa = oc.getConnectionType();
		if (oc.getFromObject() == null)
			validationErrors.add(new ErrorEntry(TextID.MsgNoPropertySet, new String[] { pa.getLabel()
					+ " => from object (connection)" }));
		else if (oc.getToObject() == null)
			validationErrors.add(new ErrorEntry(TextID.MsgNoPropertySet, new String[] { pa.getLabel()
					+ " => to object (connection)" }));
		else if (oc.getLineStyle() == null)
			validationErrors.add(new ErrorEntry(TextID.MsgNoPropertySet, new String[] { pa.getLabel()
					+ " => line style (connection)" }));
		else if (oc.getLineWeight() == null)
			validationErrors.add(new ErrorEntry(TextID.MsgNoPropertySet, new String[] { pa.getLabel()
					+ " => line weight (connection)" }));
		return validationErrors.isEmpty();
	}

	@Override
	protected void makeObjectExport(Element target, ObjectConnection oc) throws ACException{
		Element connElem = document.createElement("connection");
		log.debug("exporting connection `" + oc.getConnectionType().getLabel() + "`");
		connElem.setAttribute("type", oc.getConnectionType().getLabel());
		connElem.setAttribute("fromObject", oc.getFromObject().getName());
		connElem.setAttribute("toObject", oc.getToObject().getName());
		connElem.setAttribute("lineColor", oc.getRgb());
		connElem.setAttribute("lineStyle", oc.getLineStyle().getTextId().getKey());
		connElem.setAttribute("lineWeight", oc.getLineWeight().getLabel());
		connElem.setAttribute("hierarchical", String.valueOf(objectsConnectionsService.isHierarchicalConnection(oc)));

		target.appendChild(connElem);
	}

}
