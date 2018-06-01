/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.SchemaGroup;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.exporters.mock.ExporterMock;
import com.ni3.ag.adminconsole.validation.ACException;

public class GroupExporterTest extends TestCase{

	private GroupExporter exporter;
	private Document document;

	public void setUp() throws Exception{
		exporter = new GroupExporter();
		exporter.setRightsExporter(new ExporterMock<Element, SchemaGroup>());
		exporter.setUserExporter(new ExporterMock<Element, User>());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();
	}

	public void testExportOk() throws ACException{
		Element target = document.createElement("target");
		Group g = new Group();
		g.setName("group1");
		List<User> users = new ArrayList<User>();
		users.add(new User());
		g.setUsers(users);
		List<ObjectGroup> oug = new ArrayList<ObjectGroup>();
		ObjectDefinition od = new ObjectDefinition();
		oug.add(new ObjectGroup(od, g));
		g.setObjectGroups(oug);
		Schema schema = new Schema();
		g.setSchemaGroups(new ArrayList<SchemaGroup>());
		g.getSchemaGroups().add(new SchemaGroup(schema, g));

		exporter.export(document, target, g);

		NodeList children = target.getElementsByTagName("group");
		assertEquals(1, children.getLength());

		Node groupNode = children.item(0);
		Node nameAttr = groupNode.getAttributes().getNamedItem("name");
		assertEquals(nameAttr.getNodeValue(), g.getName());
		NodeList usersNodeList = ((Element) groupNode).getElementsByTagName("users");
		assertTrue(usersNodeList.getLength() > 0);
		NodeList accessRights = ((Element) groupNode).getElementsByTagName("accessRights");
		assertTrue(accessRights.getLength() > 0);
	}

	public void testExportNoUsers() throws ACException{
		Element target = document.createElement("target");
		Group g = new Group();
		g.setName("group1");
		Schema schema = new Schema();
		g.setSchemaGroups(new ArrayList<SchemaGroup>());
		g.getSchemaGroups().add(new SchemaGroup(schema, g));

		exporter.export(document, target, g);

		NodeList children = target.getElementsByTagName("group");
		assertEquals(1, children.getLength());

		Node groupNode = children.item(0);
		Node nameAttr = groupNode.getAttributes().getNamedItem("name");
		assertEquals(nameAttr.getNodeValue(), g.getName());
		NodeList usersNodeList = ((Element) groupNode).getElementsByTagName("users");
		assertTrue(usersNodeList.getLength() == 0);
		NodeList accessRights = ((Element) groupNode).getElementsByTagName("accessRights");
		assertTrue(accessRights.getLength() == 1);
	}

	public void testExportNoObjectGroups() throws ACException{
		Element target = document.createElement("target");
		Group g = new Group();
		g.setName("group1");
		List<User> users = new ArrayList<User>();
		users.add(new User());
		g.setUsers(users);
		// no object user groups - error
		boolean ok = true;
		try{
			exporter.export(document, target, g);
		} catch (Exception e){
			ok = false;
		}
		assertFalse(ok);
	}

}
