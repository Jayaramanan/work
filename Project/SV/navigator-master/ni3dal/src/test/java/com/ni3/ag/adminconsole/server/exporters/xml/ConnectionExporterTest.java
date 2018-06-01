/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ni3.ag.adminconsole.domain.LineStyle;
import com.ni3.ag.adminconsole.domain.LineWeight;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.server.service.impl.ObjectsConnectionsServiceImpl;
import com.ni3.ag.adminconsole.validation.ACException;

public class ConnectionExporterTest extends TestCase{
	private ConnectionExporter exporter;
	private Document document;

	public void setUp() throws Exception{
		exporter = new ConnectionExporter();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();
	}

	public void testExportOk() throws ACException{
		Element parent = document.createElement("parent");
		ObjectConnection oc = new ObjectConnection();
		PredefinedAttribute connectionType = new PredefinedAttribute();
		connectionType.setLabel("conntype");
		oc.setConnectionType(connectionType);
		ObjectDefinition fromObject = new ObjectDefinition();
		fromObject.setName("fromObject");
		oc.setFromObject(fromObject);
		ObjectDefinition toObject = new ObjectDefinition();
		toObject.setName("toObject");
		oc.setToObject(toObject);
		oc.setLineStyle(LineStyle.FULL);
		oc.setLineWeight(new LineWeight());
		exporter.setObjectsConnectionsService(Mockito.mock(ObjectsConnectionsServiceImpl.class));
		exporter.export(document, parent, oc);

		NodeList children = parent.getElementsByTagName("connection");
		assertEquals(1, children.getLength());

		Node ocNode = children.item(0);
		Node typeAttr = ocNode.getAttributes().getNamedItem("type");
		assertEquals(typeAttr.getNodeValue(), oc.getConnectionType().getLabel());
		Node fromObjAttr = ocNode.getAttributes().getNamedItem("fromObject");
		assertEquals(fromObjAttr.getNodeValue(), oc.getFromObject().getName());
		Node toObjAttr = ocNode.getAttributes().getNamedItem("toObject");
		assertEquals(toObjAttr.getNodeValue(), oc.getToObject().getName());

	}

	public void testExportError() throws ACException{
		Element parent = document.createElement("parent");
		ObjectConnection oc = new ObjectConnection();
		PredefinedAttribute connectionType = new PredefinedAttribute();
		// predefined without value - error
		oc.setConnectionType(connectionType);
		ObjectDefinition fromObject = new ObjectDefinition();
		fromObject.setName("fromObject");
		oc.setFromObject(fromObject);
		ObjectDefinition toObject = new ObjectDefinition();
		toObject.setName("toObject");
		oc.setToObject(toObject);
		oc.setLineStyle(LineStyle.FULL);
		oc.setLineWeight(new LineWeight());
		exporter.setObjectsConnectionsService(Mockito.mock(ObjectsConnectionsServiceImpl.class));
		exporter.export(document, parent, oc);

		NodeList children = parent.getElementsByTagName("connection");
		assertEquals(1, children.getLength());

		Node ocNode = children.item(0);
		Node typeAttr = ocNode.getAttributes().getNamedItem("type");
		assertEquals(typeAttr.getNodeValue(), "");
		assertNull(oc.getConnectionType().getValue());
		Node fromObjAttr = ocNode.getAttributes().getNamedItem("fromObject");
		assertEquals(fromObjAttr.getNodeValue(), oc.getFromObject().getName());
		Node toObjAttr = ocNode.getAttributes().getNamedItem("toObject");
		assertEquals(toObjAttr.getNodeValue(), oc.getToObject().getName());
	}

}
