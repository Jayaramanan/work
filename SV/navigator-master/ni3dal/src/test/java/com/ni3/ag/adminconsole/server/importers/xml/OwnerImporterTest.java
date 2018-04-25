/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.server.dao.mock.UserDAOMock;
import com.ni3.ag.adminconsole.server.importers.xml.OwnerImporter;

import junit.framework.TestCase;

public class OwnerImporterTest extends TestCase{
	private OwnerImporter importer;

	private NodeList ownerNodeList;

	public void setUp() throws SAXException, IOException, ParserConfigurationException{
		importer = new OwnerImporter();
		UserDAO userDAO = new UserDAOMock();
		User user = new User();
		user.setUserName("def");
		user.setPassword("4ed9407630eb1000c0f6b63842defa7d");
		userDAO.saveOrUpdate(user);
		importer.setUserDAO(userDAO);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = factory.newDocumentBuilder().parse(new File("./target/test-classes/sample_import.xml"));

		ownerNodeList = doc.getElementsByTagName("owner");
	}

	public void testImportDataValid(){
		ErrorContainer ec = importer.importData(ownerNodeList);
		assertEquals(0, ec.getErrors().size());
	}

	public void testImportDataInvalid(){
		for (int i = 0; i < ownerNodeList.getLength(); i++){
			Node n = ownerNodeList.item(i);
			n.getAttributes().removeNamedItem("name");
		}

		ErrorContainer ec = importer.importData(ownerNodeList);
		assertEquals(1, ec.getErrors().size());
	}

	public void testImportDataInvalidXML(){
		// remove owner
		ownerNodeList = new NodeList(){
			@Override
			public Node item(int index){
				return null;
			}

			@Override
			public int getLength(){
				return 0;
			}
		};
		ErrorContainer ec = importer.importData(ownerNodeList);
		assertEquals(1, ec.getErrors().size());
	}
}
