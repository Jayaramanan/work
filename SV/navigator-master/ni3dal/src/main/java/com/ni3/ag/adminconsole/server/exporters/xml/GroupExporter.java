/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupScope;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.SchemaGroup;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.validation.ACException;

public class GroupExporter extends XMLSchemaExporter<Element, Group>{
	private final static Logger log = Logger.getLogger(GroupExporter.class);
	private XMLSchemaExporter<Element, User> userExporter;
	private XMLSchemaExporter<Element, SchemaGroup> rightsExporter;
	private XMLSchemaExporter<Element, GroupScope> scopeExporter;

	private Schema schema;

	@Override
	protected void makeObjectExport(Element target, Group group) throws ACException{
		Element groupElem = document.createElement("group");
		log.debug("exporting group `" + group.getName() + "`");

		groupElem.setAttribute("name", group.getName());
		target.appendChild(groupElem);

		if (group.getUsers() != null && !group.getUsers().isEmpty()){
			Element userElem = document.createElement("users");
			for (User user : group.getUsers())
				userExporter.export(document, userElem, user);
			groupElem.appendChild(userElem);
		}
		Element rightsElem = document.createElement("accessRights");
		for (SchemaGroup sg : group.getSchemaGroups()){
			if (schema != null && schema.equals(sg.getSchema())){
				rightsExporter.export(document, rightsElem, sg);
			}
		}
		groupElem.appendChild(rightsElem);

		exportScopes(groupElem, group);
	}

	private void exportScopes(Element target, Group group) throws ACException{
		GroupScope gs = group.getGroupScope();
		if (gs != null){
			Element scopesElem = document.createElement("scopes");
			target.appendChild(scopesElem);
			scopeExporter.export(document, scopesElem, gs);
		}
	}

	public void setScopeExporter(XMLSchemaExporter<Element, GroupScope> scopeExporter){
		this.scopeExporter = scopeExporter;
	}

	public void setUserExporter(XMLSchemaExporter<Element, User> userExporter){
		this.userExporter = userExporter;
	}

	public void setRightsExporter(XMLSchemaExporter<Element, SchemaGroup> rightsExporter){
		this.rightsExporter = rightsExporter;
	}

	public void setSchema(Schema schema){
		this.schema = schema;
	}
}
