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
import com.ni3.ag.adminconsole.server.dao.mock.SchemaDAOMock;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.dbservice.mock.UserTableStructureServiceMock;
import com.ni3.ag.adminconsole.server.importers.mock.ImporterMock;
import com.ni3.ag.adminconsole.validation.mock.ACValidationRuleMock;

public class SchemaObjectImporterTest extends TestCase{

	private final static Logger log = Logger.getLogger(SchemaObjectImporterTest.class);

	private SchemaObjectImporter importer;

	private NodeList schemaDefinitionNodeList;

	public void setUp() throws SAXException, IOException, ParserConfigurationException{
		importer = new SchemaObjectImporter();
		importer.setObjectDefinitionImporter(new ImporterMock());
		importer.setGroupImporter(new ImporterMock());
		importer.setConnectionImporter(new ImporterMock());
		SchemaDAOMock schemaDAO = new SchemaDAOMock();
		importer.setSchemaDAO(schemaDAO);
		importer.setSchemaNameValidationRule(new ACValidationRuleMock());
		importer.setUserTableStructureService(new UserTableStructureServiceMock());
		importer.setDataSource(new ACRoutingDataSource());

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = factory.newDocumentBuilder().parse(new File("./target/test-classes/sample_import.xml"));

		schemaDefinitionNodeList = doc.getElementsByTagName("schemaDefinition");
	}

	public void testImportDataValid(){
		ErrorContainer ec = importer.importData(schemaDefinitionNodeList);
		for (int i = 0; i < ec.getErrors().size(); i++)
			log.debug(ec.getErrors().get(i));
		assertEquals(0, ec.getErrors().size());
	}

	public void testImportDataInvalidXML(){
		// remove attribute name
		for (int i = 0; i < schemaDefinitionNodeList.getLength(); i++){
			Node n = schemaDefinitionNodeList.item(i);
			n.getAttributes().removeNamedItem("name");
		}

		ErrorContainer errors = importer.importData(schemaDefinitionNodeList);
		assertFalse(errors.getErrors().isEmpty());
	}
}
