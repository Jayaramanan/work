/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.server.dao.mock.GroupDAOMock;
import com.ni3.ag.adminconsole.server.importers.mock.ImporterMock;
import com.ni3.ag.adminconsole.server.importers.xml.GroupImporter;

import junit.framework.TestCase;

public class GroupImporterTest extends TestCase{

	private final static Logger log = Logger.getLogger(GroupImporterTest.class);

	private GroupImporter importer;

	private NodeList groupNodeList;

	public void setUp() throws SAXException, IOException, ParserConfigurationException{
		importer = new GroupImporter();
		importer.setGroupDAO(new GroupDAOMock());
		importer.setUserImporter(new ImporterMock());
		importer.setSchemaRightsImporter(new ImporterMock());
		importer.setScopeImporter(new ImporterMock());

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = factory.newDocumentBuilder().parse(new File("./target/test-classes/sample_import.xml"));

		groupNodeList = doc.getElementsByTagName("group");
	}

	public void testImportDataValid(){
		ErrorContainer ec = importer.importData(groupNodeList);
		for (int i = 0; i < ec.getErrors().size(); i++)
			log.debug(ec.getErrors().get(i));
		assertEquals(0, ec.getErrors().size());
	}

	public void testImportDataInvalidXML(){
		// remove attribute name
		for (int i = 0; i < groupNodeList.getLength(); i++){
			Node n = groupNodeList.item(i);
			n.getAttributes().removeNamedItem("name");
		}

		ErrorContainer errors = importer.importData(groupNodeList);
		assertFalse(errors.getErrors().isEmpty());
	}
}
