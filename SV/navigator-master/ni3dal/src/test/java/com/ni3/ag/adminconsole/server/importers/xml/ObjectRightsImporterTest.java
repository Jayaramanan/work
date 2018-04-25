/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.server.dao.mock.GroupDAOMock;
import com.ni3.ag.adminconsole.server.importers.mock.ImporterMock;

public class ObjectRightsImporterTest extends TestCase{
	private static final Logger log = Logger.getLogger(ObjectRightsImporterTest.class);

	private ObjectRightsImporter importer;

	private NodeList objectRightsNodeList;

	public void setUp() throws SAXException, IOException, ParserConfigurationException{
		importer = new ObjectRightsImporter();
		importer.setAttributeRightsImporter(new ImporterMock());
		Group group = new Group();
		importer.setGroupDAO(new GroupDAOMock());
		Schema schema = new Schema();
		ObjectDefinition object = new ObjectDefinition();
		object.setName("Account");
		object.setSchema(schema);
		schema.setObjectDefinitions(new ArrayList<ObjectDefinition>());
		schema.getObjectDefinitions().add(object);
		importer.setCurrentSchema(schema);
		importer.setCurrentGroup(group);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = factory.newDocumentBuilder().parse(new File("./target/test-classes/sample_import.xml"));

		objectRightsNodeList = doc.getElementsByTagName("objectRight");
	}

	public void testImportDataValid(){
		ErrorContainer ec = importer.importData(objectRightsNodeList);
		for (int i = 0; i < ec.getErrors().size(); i++)
			log.debug(ec.getErrors().get(i));
		assertEquals(0, ec.getErrors().size());
	}

	public void testImportDataInvalidXML(){
		// remove attribute object
		for (int i = 0; i < objectRightsNodeList.getLength(); i++){
			Node n = objectRightsNodeList.item(i);
			n.getAttributes().removeNamedItem("object");
		}

		ErrorContainer errors = importer.importData(objectRightsNodeList);
		assertFalse(errors.getErrors().isEmpty());
	}

}
