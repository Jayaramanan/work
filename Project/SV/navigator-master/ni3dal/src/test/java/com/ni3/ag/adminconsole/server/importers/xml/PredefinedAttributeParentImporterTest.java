/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.util.ArrayList;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

import junit.framework.TestCase;

public class PredefinedAttributeParentImporterTest extends TestCase{

	public void testGetPredefinedAttribute(){
		PredefinedAttributeParentImporter importer = new PredefinedAttributeParentImporter();

		ObjectDefinition od = new ObjectDefinition();
		od.setObjectAttributes(new ArrayList<ObjectAttribute>());
		ObjectAttribute oa1 = new ObjectAttribute();
		oa1.setName("Name1");
		ObjectAttribute oa2 = new ObjectAttribute();
		oa2.setName("Name2");
		ObjectAttribute oa3 = new ObjectAttribute();
		oa3.setName("Name3");
		od.getObjectAttributes().add(oa1);
		od.getObjectAttributes().add(oa2);
		od.getObjectAttributes().add(oa3);

		oa1.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		oa2.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		oa3.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());

		PredefinedAttribute pa1 = new PredefinedAttribute();
		pa1.setValue("Value1");
		pa1.setLabel("Label1");
		PredefinedAttribute pa2 = new PredefinedAttribute();
		pa2.setValue("Value2");
		pa2.setLabel("Label2");
		PredefinedAttribute pa3 = new PredefinedAttribute();
		pa3.setValue("Value3");
		pa3.setLabel("Label3");
		oa1.getPredefinedAttributes().add(pa1);
		oa2.getPredefinedAttributes().add(pa2);
		oa2.getPredefinedAttributes().add(pa3);

		assertNull(importer.getPredefinedAttribute(od, "abc", "Value1", "Label1"));
		assertNull(importer.getPredefinedAttribute(new ObjectDefinition(), "Name1", "Value1", "Label1"));
		assertNull(importer.getPredefinedAttribute(od, "Name2", "Value1", "Label1"));
		assertNull(importer.getPredefinedAttribute(od, "Name1", "Value2", "Label1"));
		assertNull(importer.getPredefinedAttribute(od, "Name1", "Value1", "Label2"));

		assertSame(pa1, importer.getPredefinedAttribute(od, "Name1", "Value1", "Label1"));
		assertSame(pa2, importer.getPredefinedAttribute(od, "Name2", "Value2", "Label2"));
		assertSame(pa3, importer.getPredefinedAttribute(od, "Name2", "Value3", "Label3"));
	}
}
