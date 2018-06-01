/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.server.dao.mock.ObjectDefinitionDAOMock;
import com.ni3.ag.adminconsole.validation.mock.ACValidationRuleMock;

public class PredefinedAttributeImporterTest extends TestCase{

	private final static Logger log = Logger.getLogger(PredefinedAttributeImporterTest.class);

	private PredefinedAttributeImporter importer;

	private NodeList predefinedAttributeNodeList;

	public void setUp() throws SAXException, IOException, ParserConfigurationException{
		importer = new PredefinedAttributeImporter();
		ObjectDefinitionDAOMock objectDefinitionDAO = new ObjectDefinitionDAOMock();
		objectDefinitionDAO.createObjectDefinitionsFromXML();
		importer.setPredefAttributeValidationRule(new ACValidationRuleMock());

		ObjectAttribute parent = new ObjectAttribute();
		parent.setName(ObjectAttribute.CONNECTION_TYPE_ATTRIBUTE_NAME);
		importer.parent = parent;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = factory.newDocumentBuilder().parse(new File("./target/test-classes/sample_import.xml"));

		predefinedAttributeNodeList = doc.getElementsByTagName("predefined");
	}

	public void testImportDataValid(){

		ErrorContainer ec = importer.importData(predefinedAttributeNodeList);
		for (int i = 0; i < ec.getErrors().size(); i++)
			log.debug(ec.getErrors().get(i));
		assertEquals(0, ec.getErrors().size());
	}

}
