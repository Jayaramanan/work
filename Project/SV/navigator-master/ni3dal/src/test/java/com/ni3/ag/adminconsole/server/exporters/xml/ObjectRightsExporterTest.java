/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.server.exporters.mock.ExporterMock;
import com.ni3.ag.adminconsole.validation.ACException;

import junit.framework.TestCase;

public class ObjectRightsExporterTest extends TestCase{

	private ObjectRightsExporter exporter;
	private Document document;

	public void setUp() throws Exception{
		exporter = new ObjectRightsExporter();
		exporter.setAttributeRightsExporter(new ExporterMock<Element, AttributeGroup>());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();
	}

	public void testExportOk() throws ACException{
		Element parent = document.createElement("parent");
		Group g = new Group();
		ObjectDefinition od = new ObjectDefinition();
		od.setName("od1");
		ObjectGroup oug = new ObjectGroup(od, g);
		g.setAttributeGroups(new ArrayList<AttributeGroup>());
		exporter.export(document, parent, oug);

		NodeList children = parent.getElementsByTagName("objectRight");
		assertEquals(1, children.getLength());
		Node rightNode = children.item(0);
		Node objectAttr = rightNode.getAttributes().getNamedItem("object");
		assertEquals(od.getName(), objectAttr.getNodeValue());
		Node canReadAttr = rightNode.getAttributes().getNamedItem("canRead");
		assertEquals(oug.isCanRead() ? "1" : "0", canReadAttr.getNodeValue());
		Node canWriteAttr = rightNode.getAttributes().getNamedItem("canCreate");
		assertEquals(oug.isCanCreate() ? "1" : "0", canWriteAttr.getNodeValue());
		Node canUpdateAttr = rightNode.getAttributes().getNamedItem("canUpdate");
		assertEquals(oug.isCanUpdate() ? "1" : "0", canUpdateAttr.getNodeValue());
		Node canDeleteAttr = rightNode.getAttributes().getNamedItem("canDelete");
		assertEquals(oug.isCanDelete() ? "1" : "0", canDeleteAttr.getNodeValue());
	}

	public void testExportNoObjectName() throws ACException{
		Element parent = document.createElement("parent");
		Group g = new Group();
		ObjectDefinition od = new ObjectDefinition();
		ObjectGroup oug = new ObjectGroup(od, g);
		g.setAttributeGroups(new ArrayList<AttributeGroup>());
		exporter.export(document, parent, oug);

		NodeList children = parent.getElementsByTagName("objectRight");
		assertEquals(1, children.getLength());
		Node rightNode = children.item(0);
		Node objectAttr = rightNode.getAttributes().getNamedItem("object");
		assertNull(od.getName());
		assertEquals("", objectAttr.getNodeValue());

	}

	public void testExportNoAttributeRights(){
		Element parent = document.createElement("parent");
		Group g = new Group();
		ObjectDefinition od = new ObjectDefinition();
		od.setName("od1");
		ObjectGroup oug = new ObjectGroup(od, g);
		// no attribute groups
		boolean ok = true;
		try{
			exporter.export(document, parent, oug);
		} catch (Exception e){
			ok = false;
		}
		assertFalse(ok);

	}
}
