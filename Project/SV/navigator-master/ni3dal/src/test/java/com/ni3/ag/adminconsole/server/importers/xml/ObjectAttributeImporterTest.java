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

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.server.dao.mock.ObjectAttributeDAOMock;
import com.ni3.ag.adminconsole.server.dao.mock.ObjectDefinitionDAOMock;
import com.ni3.ag.adminconsole.server.importers.mock.ImporterMock;
import com.ni3.ag.adminconsole.validation.mock.ACValidationRuleMock;

public class ObjectAttributeImporterTest extends TestCase{

	private final static Logger log = Logger.getLogger(ObjectAttributeImporterTest.class);

	private ObjectAttributeImporter importer;

	private NodeList objectAttributeNodeList;

	public void setUp() throws SAXException, IOException, ParserConfigurationException{
		importer = new ObjectAttributeImporter();
		ObjectDefinitionDAOMock objectDefinitionDAO = new ObjectDefinitionDAOMock();
		objectDefinitionDAO.createObjectDefinitionsFromXML();
		importer.setObjectAttributeDAO(new ObjectAttributeDAOMock());
		importer.setPredefinedAttributeImporter(new ImporterMock());
		importer.setFormulaImporter(new ImporterMock());
		importer.setUserTableNameRule(new ACValidationRuleMock());
		importer.parent = new ObjectDefinition();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = factory.newDocumentBuilder().parse(new File("./target/test-classes/sample_import.xml"));

		objectAttributeNodeList = doc.getElementsByTagName("objectAttribute");
	}

	public void testImportDataValid(){
		ErrorContainer ec = importer.importData(objectAttributeNodeList);
		for (int i = 0; i < ec.getErrors().size(); i++)
			log.debug(ec.getErrors().get(i));
		assertEquals(0, ec.getErrors().size());
	}

	public void testImportDataInvalidXML(){
		// remove attribute datatype
		for (int i = 0; i < objectAttributeNodeList.getLength(); i++){
			Node n = objectAttributeNodeList.item(i);
			n.getAttributes().removeNamedItem("dataType");
		}

		ErrorContainer errors = importer.importData(objectAttributeNodeList);
		assertFalse(errors.getErrors().isEmpty());
	}

}
