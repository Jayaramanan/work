/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.Formula;
import com.ni3.ag.adminconsole.domain.InMatrixType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.server.dao.mock.PredefinedAttributeDAOMock;
import com.ni3.ag.adminconsole.server.exporters.mock.ExporterMock;
import com.ni3.ag.adminconsole.validation.ACException;

public class ObjectAttributeExporterTest extends TestCase{

	private ObjectAttributeExporter oaExporter;
	private Document document;

	@Override
	public void setUp() throws Exception{
		oaExporter = new ObjectAttributeExporter();
		oaExporter.setPredefinedDAO(new PredefinedAttributeDAOMock());
		oaExporter.setPredefinedExporter(new ExporterMock<Element, PredefinedAttribute>());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();
	}

	public void testExportOk() throws ACException{
		Element parent = document.createElement("parent");
		ObjectAttribute oa = new ObjectAttribute();
		oa.setDataType(DataType.TEXT);
		oa.setName("oatest");
		oa.setLabel("oalabel");
		oa.setDescription("oadescr");
		oa.setInMetaphor(true);
		oa.setPredefined(false);
		oa.setFormula(new Formula());
		oa.setInMatrix(InMatrixType.Displayed.getValue());
		oaExporter.export(document, parent, oa);

		NodeList children = parent.getElementsByTagName("objectAttribute");
		assertEquals(1, children.getLength());

		Node oaNode = children.item(0);
		Node nameAttr = oaNode.getAttributes().getNamedItem("name");
		assertEquals(nameAttr.getNodeValue(), oa.getName());

		Node labelAttr = oaNode.getAttributes().getNamedItem("label");
		assertEquals(labelAttr.getNodeValue(), oa.getLabel());

		Node descrAttr = oaNode.getAttributes().getNamedItem("description");
		assertEquals(descrAttr.getNodeValue(), oa.getDescription());

		Node typeAttr = oaNode.getAttributes().getNamedItem("dataType");
		assertEquals(DataType.TEXT.getTextId().getKey(), typeAttr.getNodeValue());

		Node inMetaphorAttr = oaNode.getAttributes().getNamedItem("inMetaphor");
		assertEquals(oa.isInMetaphor() ? "1" : "0", inMetaphorAttr.getNodeValue());

		NodeList formula = ((Element) oaNode).getElementsByTagName("formula");
		assertEquals(formula.getLength(), 1);

	}

	public void testExportError(){
		Element parent = document.createElement("parent");
		ObjectAttribute oa = new ObjectAttribute();
		// no datatype - error
		boolean ok = true;
		try{
			oaExporter.export(document, parent, oa);
		} catch (Exception e){
			ok = false;
		}

		assertFalse(ok);
	}

}
