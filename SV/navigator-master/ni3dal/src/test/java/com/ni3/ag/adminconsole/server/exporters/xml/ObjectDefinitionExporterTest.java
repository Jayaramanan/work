/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.server.exporters.mock.ExporterMock;
import com.ni3.ag.adminconsole.validation.ACException;

public class ObjectDefinitionExporterTest extends TestCase{

	private ObjectDefinitionExporter odExporter;
	private Document document;

	@Override
	public void setUp() throws Exception{
		odExporter = new ObjectDefinitionExporter();
		odExporter.setAttributeExporter(new ExporterMock<Element, ObjectAttribute>());
		odExporter.setConnectionExporter(new ExporterMock<Element, ObjectConnection>());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();
	}

	public void testExportOk() throws ACException{
		Element parent = document.createElement("parent");
		ObjectDefinition od = new ObjectDefinition();
		od.setObjectType(ObjectType.NODE);
		od.setName("testod");
		od.setDescription("testod descr");
		od.setSort(1);
		od.setObjectAttributes(new ArrayList<ObjectAttribute>());
		od.setObjectConnections(new ArrayList<ObjectConnection>());

		odExporter.export(document, parent, od);

		NodeList children = parent.getElementsByTagName("objectDefinition");
		assertEquals(1, children.getLength());

		Node odNode = children.item(0);

		Node typeAttr = odNode.getAttributes().getNamedItem("type");
		assertEquals("Node", typeAttr.getNodeValue());

		Node nameAttr = odNode.getAttributes().getNamedItem("name");
		assertEquals("testod", nameAttr.getNodeValue());

		Node descrAttr = odNode.getAttributes().getNamedItem("description");
		assertEquals("testod descr", descrAttr.getNodeValue());

		Node sortAttr = odNode.getAttributes().getNamedItem("sort");
		assertEquals("1", sortAttr.getNodeValue());

		NodeList odAttrs = ((Element) odNode).getElementsByTagName("objectAttributes");
		assertEquals(1, odAttrs.getLength());
		NodeList connections = ((Element) odNode).getElementsByTagName("connections");
		assertEquals(1, connections.getLength());
	}

	public void testExportError() throws ACException{
		Element parent = document.createElement("parent");
		ObjectDefinition od = new ObjectDefinition();
		// no object type - error
		od.setName("testod");
		od.setDescription("testod descr");
		od.setSort(1);
		od.setObjectAttributes(new ArrayList<ObjectAttribute>());
		od.setObjectConnections(new ArrayList<ObjectConnection>());

		boolean ok = true;
		try{
			odExporter.export(document, parent, od);
		} catch (Exception e){
			ok = false;
		}
		assertFalse(ok);
	}
}
