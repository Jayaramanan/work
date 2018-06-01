/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.EditingOption;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.validation.ACException;

public class AttributeRightsExporterTest extends TestCase{

	private AttributeRightsExporter exporter;
	private Document document;

	public void setUp() throws Exception{
		exporter = new AttributeRightsExporter();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();
	}

	public void testExportOk() throws ACException{
		Element target = document.createElement("target");

		ObjectAttribute oa = new ObjectAttribute();
		oa.setName("oa");
		Group g = new Group();
		AttributeGroup ag = new AttributeGroup(oa, g);
		ag.setCanRead(true);
		ag.setEditingOption(EditingOption.NotVisible);
		ag.setEditingOptionLocked(EditingOption.NotVisible);
		exporter.export(document, target, ag);

		NodeList children = target.getElementsByTagName("attributeRight");
		assertEquals(1, children.getLength());

		Node ocNode = children.item(0);
		Node nameAttr = ocNode.getAttributes().getNamedItem("name");
		assertEquals(nameAttr.getNodeValue(), oa.getName());
		Node canReadAttr = ocNode.getAttributes().getNamedItem("canRead");
		assertEquals(canReadAttr.getNodeValue(), "1");
		Node canWriteAttr = ocNode.getAttributes().getNamedItem("editingLocked");
		assertEquals(canWriteAttr.getNodeValue(), "NotVisible");

	}

	public void testExportError() throws ACException{
		Element target = document.createElement("target");

		ObjectAttribute oa = new ObjectAttribute();
		// no attribute name - error
		Group g = new Group();
		AttributeGroup ag = new AttributeGroup(oa, g);
		ag.setEditingOptionLocked(EditingOption.Editable);
		exporter.export(document, target, ag);

		NodeList children = target.getElementsByTagName("attributeRight");
		assertEquals(1, children.getLength());

		Node ocNode = children.item(0);
		Node nameAttr = ocNode.getAttributes().getNamedItem("name");
		assertEquals(nameAttr.getNodeValue(), "");
		assertNull(oa.getName());
		Node canReadAttr = ocNode.getAttributes().getNamedItem("canRead");
		assertEquals(canReadAttr.getNodeValue(), "0");
		Node canWriteAttr = ocNode.getAttributes().getNamedItem("editingLocked");
		assertEquals(canWriteAttr.getNodeValue(), "Editable");

	}
}
