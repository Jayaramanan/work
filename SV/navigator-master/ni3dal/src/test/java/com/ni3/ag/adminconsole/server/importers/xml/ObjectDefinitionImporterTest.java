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

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.server.dao.mock.ObjectDefinitionDAOMock;
import com.ni3.ag.adminconsole.server.dao.mock.SchemaDAOMock;
import com.ni3.ag.adminconsole.server.importers.mock.ImporterMock;
import com.ni3.ag.adminconsole.validation.mock.ACValidationRuleMock;

public class ObjectDefinitionImporterTest extends TestCase{

	private final static Logger log = Logger.getLogger(ObjectDefinitionImporterTest.class);

	private ObjectDefinitionImporter importer;

	private NodeList objectDefinitionNodeList;

	public void setUp() throws SAXException, IOException, ParserConfigurationException{
		importer = new ObjectDefinitionImporter();
		SchemaDAOMock schemaDAO = new SchemaDAOMock();
		schemaDAO.createSchemaDefinitionsFromXML();
		importer.setObjectDefinitionDAO(new ObjectDefinitionDAOMock());
		importer.setObjectAttributeImporter(new ImporterMock());
		Schema s = new Schema();
		s.setName("Imported Schema");
		s.setId(new Integer(3));
		importer.parent = s;
		importer.setSchemaAdminFieldValidationRule(new ACValidationRuleMock());
		importer.setUserTableNameRule(new ACValidationRuleMock());

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = factory.newDocumentBuilder().parse(new File("./target/test-classes/sample_import.xml"));

		objectDefinitionNodeList = doc.getElementsByTagName("objectDefinition");
	}

	public void testImportDataValid(){
		ErrorContainer ec = importer.importData(objectDefinitionNodeList);
		for (int i = 0; i < ec.getErrors().size(); i++)
			log.debug(ec.getErrors().get(i));
		assertEquals(0, ec.getErrors().size());
	}

	public void testImportDataInvalidXML(){
		// remove attribute type
		for (int i = 0; i < objectDefinitionNodeList.getLength(); i++){
			Node n = objectDefinitionNodeList.item(i);
			n.getAttributes().removeNamedItem("type");
		}

		ErrorContainer errors = importer.importData(objectDefinitionNodeList);

		assertFalse(errors.getErrors().isEmpty());
	}

}
