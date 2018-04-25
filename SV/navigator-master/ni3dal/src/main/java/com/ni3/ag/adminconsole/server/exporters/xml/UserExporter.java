/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.validation.ACException;

public class UserExporter extends XMLSchemaExporter<Element, User>{
	private final static Logger log = Logger.getLogger(UserExporter.class);

	@Override
	protected void makeObjectExport(Element target, User user) throws ACException{
		Element userElem = document.createElement("user");
		log.debug("exporting user `" + user.getUserName() + "`");
		userElem.setAttribute("firstname", user.getFirstName());
		userElem.setAttribute("lastname", user.getLastName());
		userElem.setAttribute("username", user.getUserName());
		userElem.setAttribute("password", user.getPassword());
		userElem.setAttribute("email", user.geteMail());
		userElem.setAttribute("active", user.getActive() ? "1" : "0");
		userElem.setAttribute("sid", user.getSID());
		userElem.setAttribute("hasOfflineClient", user.getHasOfflineClient() ? "1" : "0");
		userElem.setAttribute("ETLUser", user.getEtlUser());
		userElem.setAttribute("ETLPassword", user.getEtlPassword());

		target.appendChild(userElem);
	}

}
