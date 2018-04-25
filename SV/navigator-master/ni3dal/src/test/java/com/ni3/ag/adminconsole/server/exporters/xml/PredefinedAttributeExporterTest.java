/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.validation.ACException;

import junit.framework.TestCase;

public class PredefinedAttributeExporterTest extends TestCase{

	private PredefinedAttributeExporter exporter;
	private Document document;

	public void setUp() throws Exception{
		exporter = new PredefinedAttributeExporter();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();
	}

	public void testExportOk() throws ACException{
		Element parent = document.createElement("parent");
		PredefinedAttribute pa = new PredefinedAttribute();
		pa.setValue("val");
		pa.setLabel("lab");
		pa.setSort(1);
		pa.setToUse(true);
		pa.setHaloColor("#aaaaff");
		exporter.export(document, parent, pa);

		NodeList children = parent.getElementsByTagName("predefined");
		assertEquals(1, children.getLength());
		Node odNode = children.item(0);
		Node valueAttr = odNode.getAttributes().getNamedItem("value");
		assertEquals(pa.getValue(), valueAttr.getNodeValue());
		Node labelAttr = odNode.getAttributes().getNamedItem("label");
		assertEquals(pa.getLabel(), labelAttr.getNodeValue());
		Node sortAttr = odNode.getAttributes().getNamedItem("sort");
		assertEquals(pa.getSort().toString(), sortAttr.getNodeValue());
		Node toUseAttr = odNode.getAttributes().getNamedItem("toUse");
		assertEquals(pa.getToUse() ? "1" : "0", toUseAttr.getNodeValue());
		Node haloAttr = odNode.getAttributes().getNamedItem("halocolor");
		assertEquals(pa.getHaloColor(), haloAttr.getNodeValue());
	}

}
