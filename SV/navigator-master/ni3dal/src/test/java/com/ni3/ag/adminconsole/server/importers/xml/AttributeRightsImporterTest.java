/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.server.dao.mock.AttributeGroupDAOMock;
import com.ni3.ag.adminconsole.server.dao.mock.ObjectAttributeDAOMock;
import com.ni3.ag.adminconsole.server.dao.mock.ObjectDefinitionDAOMock;
import com.ni3.ag.adminconsole.server.dao.mock.SchemaDAOMock;
import com.ni3.ag.adminconsole.server.importers.mock.ImporterMock;
import com.ni3.ag.adminconsole.validation.mock.ACValidationRuleMock;

public class AttributeRightsImporterTest extends TestCase{

	private AttributeRightsImporter importer;

	private NodeList attributeRightNodeList;

	public void setUp() throws SAXException, IOException, ParserConfigurationException{
		importer = new AttributeRightsImporter();
		ObjectDefinitionDAOMock objectDefinitionDAO = new ObjectDefinitionDAOMock();
		objectDefinitionDAO.createObjectDefinitionsFromXML();
		ObjectAttributeDAOMock objectAttributeDAO = new ObjectAttributeDAOMock();
		importer.setAttributeGroupDAO(new AttributeGroupDAOMock());
		importer.setAttributeGroupValidationRule(new ACValidationRuleMock());
		importer.setPredefinedRightsImporter(new ImporterMock());
		Group group = new Group();
		Schema schema = new Schema();
		ObjectDefinition object = new ObjectDefinition();
		object.setName("Account");
		object.setSchema(schema);
		schema.setObjectDefinitions(new ArrayList<ObjectDefinition>());
		schema.getObjectDefinitions().add(object);
		ObjectAttribute oa = objectAttributeDAO.getObjectAttributeByName("col1", 1);
		oa.setObjectDefinition(object);
		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		object.getObjectAttributes().add(oa);

		importer.setCurrentSchema(schema);
		importer.setCurrentGroup(group);
		SchemaDAOMock schemaDAO = new SchemaDAOMock();
		schemaDAO.createSchemaDefinitionsFromXML();
		importer.parent = new ObjectGroup(object, group);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = factory.newDocumentBuilder().parse(new File("./target/test-classes/sample_import.xml"));

		attributeRightNodeList = doc.getElementsByTagName("attributeRight");
	}

	public void testImportDataValid(){
		ErrorContainer ec = importer.importData(attributeRightNodeList);
		assertEquals(0, ec.getErrors().size());
	}

	public void testImportDataInvalidXML(){
		// remove attribute name
		for (int i = 0; i < attributeRightNodeList.getLength(); i++){
			Node n = attributeRightNodeList.item(i);
			n.getAttributes().removeNamedItem("name");
		}

		ErrorContainer errors = importer.importData(attributeRightNodeList);
		assertFalse(errors.getErrors().isEmpty());
	}

}
