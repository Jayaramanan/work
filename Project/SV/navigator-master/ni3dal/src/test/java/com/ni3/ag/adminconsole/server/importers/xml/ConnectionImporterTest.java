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

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.server.dao.ObjectAttributeDAO;
import com.ni3.ag.adminconsole.server.dao.mock.LineWeightDAOMock;
import com.ni3.ag.adminconsole.server.dao.mock.ObjectAttributeDAOMock;
import com.ni3.ag.adminconsole.server.dao.mock.ObjectConnectionDAOMock;
import com.ni3.ag.adminconsole.server.dao.mock.SchemaDAOMock;
import com.ni3.ag.adminconsole.validation.mock.ACValidationRuleMock;

public class ConnectionImporterTest extends TestCase{

	private ConnectionImporter importer;

	private NodeList connectionNodeList;

	public void setUp() throws SAXException, IOException, ParserConfigurationException{
		importer = new ConnectionImporter();
		importer.setObjectConnectionDAO(new ObjectConnectionDAOMock());
		importer.setLineWeightDAO(new LineWeightDAOMock());
		SchemaDAOMock schemaDAO = new SchemaDAOMock();
		schemaDAO.createSchemaDefinitionsFromXML();
		importer.setObjectConnectionImportValidationRule(new ACValidationRuleMock());

		ObjectAttributeDAO objectAttributeDAO = new ObjectAttributeDAOMock();
		Schema schema = new Schema();
		ObjectDefinition object = new ObjectDefinition();
		object.setName("Test");
		object.setSchema(schema);
		schema.setObjectDefinitions(new ArrayList<ObjectDefinition>());
		schema.getObjectDefinitions().add(object);
		ObjectAttribute oa = objectAttributeDAO.getObjectAttributeByName(ObjectAttribute.CONNECTION_TYPE_ATTRIBUTE_NAME, 1);
		oa.setObjectDefinition(object);
		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		object.getObjectAttributes().add(oa);
		PredefinedAttribute pa = new PredefinedAttribute();
		pa.setObjectAttribute(oa);
		pa.setValue("Organizational");
		pa.setLabel("Organizational");
		oa.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		oa.getPredefinedAttributes().add(pa);

		ObjectDefinition object2 = new ObjectDefinition();
		object2.setName("Account");
		object2.setSchema(schema);
		schema.getObjectDefinitions().add(object2);

		importer.parent = schema;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = factory.newDocumentBuilder().parse(new File("./target/test-classes/sample_import.xml"));

		connectionNodeList = doc.getElementsByTagName("connection");
	}

	public void testImportDataValid(){
		ErrorContainer ec = importer.importData(connectionNodeList);
		assertEquals(0, ec.getErrors().size());
	}

	public void testImportDataInvalidXML(){
		// remove attribute type
		for (int i = 0; i < connectionNodeList.getLength(); i++){
			Node n = connectionNodeList.item(i);
			n.getAttributes().removeNamedItem("type");
		}

		ErrorContainer errors = importer.importData(connectionNodeList);
		assertFalse(errors.getErrors().isEmpty());
	}
}
