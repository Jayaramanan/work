/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.server.dao.mock.GroupDAOMock;
import com.ni3.ag.adminconsole.server.dao.mock.UserDAOMock;
import com.ni3.ag.adminconsole.validation.mock.ACValidationRuleMock;

public class UserImporterTest extends TestCase{

	private final static Logger log = Logger.getLogger(UserImporterTest.class);

	private UserImporter importer;

	private NodeList userNodeList;

	public void setUp() throws SAXException, IOException, ParserConfigurationException{
		importer = new UserImporter();
		importer.setGroupDAO(new GroupDAOMock());
		importer.setUserDAO(new UserDAOMock());
		importer.setUserAdminValidationRule(new ACValidationRuleMock());

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = factory.newDocumentBuilder().parse(new File("./target/test-classes/sample_import.xml"));

		userNodeList = doc.getElementsByTagName("user");
	}

	public void testImportDataValid(){
		ErrorContainer ec = importer.importData(userNodeList);
		for (int i = 0; i < ec.getErrors().size(); i++)
			log.debug(ec.getErrors().get(i));
		assertEquals(0, ec.getErrors().size());
	}

	public void testImportDataInvalidXML(){
		// remove attribute username
		for (int i = 0; i < userNodeList.getLength(); i++){
			Node n = userNodeList.item(i);
			n.getAttributes().removeNamedItem("username");
		}

		ErrorContainer errors = importer.importData(userNodeList);
		assertFalse(errors.getErrors().isEmpty());
	}
}
