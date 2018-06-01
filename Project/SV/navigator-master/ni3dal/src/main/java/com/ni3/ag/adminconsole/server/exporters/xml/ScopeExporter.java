/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupScope;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.validation.ACException;

public class ScopeExporter extends XMLSchemaExporter<Element, GroupScope>{
	private final static Logger log = Logger.getLogger(ScopeExporter.class);
	private GroupDAO groupDAO;

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	@Override
	protected void makeObjectExport(Element target, GroupScope groupScope) throws ACException{
		Group g = groupDAO.getGroup(groupScope.getGroup());
		log.debug("exporting scopes for group `" + groupScope.getGroup() + "`");
		Element nodeScopeElem = document.createElement("nodeScope");
		target.appendChild(nodeScopeElem);
		boolean isUsed = GroupScope.SCOPE_USED_CHAR == g.getNodeScope();
		nodeScopeElem.setAttribute("isUsed", isUsed ? "1" : "0");
		nodeScopeElem.setTextContent(groupScope.getNodeScope());

		Element edgeScopeElem = document.createElement("edgeScope");
		target.appendChild(edgeScopeElem);
		isUsed = GroupScope.SCOPE_USED_CHAR == g.getEdgeScope();
		edgeScopeElem.setAttribute("isUsed", isUsed ? "1" : "0");
		edgeScopeElem.setTextContent(groupScope.getEdgeScope());
	}

}
