/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.validation.ACException;

import junit.framework.TestCase;

public class UserExporterTest extends TestCase{
	private UserExporter userExporter;
	private Document document;

	public void setUp() throws Exception{
		userExporter = new UserExporter();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();
	}

	public void testExportOk() throws ACException{
		Element target = document.createElement("target");
		User user = new User();
		user.setFirstName("fname");
		user.setLastName("lname");
		user.setUserName("uname");
		user.setPassword("upass");
		user.seteMail("email");
		user.setActive(true);
		userExporter.export(document, target, user);

		NodeList children = target.getElementsByTagName("user");
		assertEquals(1, children.getLength());
		Node userNode = children.item(0);
		Node fNameAttr = userNode.getAttributes().getNamedItem("firstname");
		assertEquals(user.getFirstName(), fNameAttr.getNodeValue());
		Node lNameAttr = userNode.getAttributes().getNamedItem("lastname");
		assertEquals(user.getLastName(), lNameAttr.getNodeValue());
		Node uNameAttr = userNode.getAttributes().getNamedItem("username");
		assertEquals(user.getUserName(), uNameAttr.getNodeValue());
		Node passAttr = userNode.getAttributes().getNamedItem("password");
		assertEquals(user.getPassword(), passAttr.getNodeValue());
		Node emailAttr = userNode.getAttributes().getNamedItem("email");
		assertEquals(user.geteMail(), emailAttr.getNodeValue());
		Node activeAttr = userNode.getAttributes().getNamedItem("active");
		assertEquals(user.getActive() ? "1" : "0", activeAttr.getNodeValue());
	}
}
