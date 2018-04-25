/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;

public class AbstractImporterTest extends TestCase{
	public void testGetObjectAttributeByName(){
		AbstractImporter importer = new ObjectAttributeImporter();

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

		assertNull(importer.getObjectAttributeByName(od, null));
		assertNull(importer.getObjectAttributeByName(od, ""));
		assertNull(importer.getObjectAttributeByName(od, "abc"));
		assertNull(importer.getObjectAttributeByName(od, "name2"));

		assertSame(oa2, importer.getObjectAttributeByName(od, "Name2"));
		assertSame(oa3, importer.getObjectAttributeByName(od, "Name3"));
	}

	public void testGetPredefinedAttributeByNameAndLabel(){
		AbstractImporter importer = new PredefinedAttributeImporter();

		ObjectAttribute oa = new ObjectAttribute();
		oa.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		PredefinedAttribute pa1 = new PredefinedAttribute();
		pa1.setValue("Value1");
		pa1.setLabel("Label1");
		PredefinedAttribute pa2 = new PredefinedAttribute();
		pa2.setValue("Value2");
		pa2.setLabel("Label2");
		PredefinedAttribute pa3 = new PredefinedAttribute();
		pa3.setValue("Value3");
		pa3.setLabel("Label3");
		oa.getPredefinedAttributes().add(pa1);
		oa.getPredefinedAttributes().add(pa2);
		oa.getPredefinedAttributes().add(pa3);

		assertNull(importer.getPredefinedAttributeByValueAndLabel(oa, null, null));
		assertNull(importer.getPredefinedAttributeByValueAndLabel(oa, "", ""));
		assertNull(importer.getPredefinedAttributeByValueAndLabel(oa, "Value2", ""));
		assertNull(importer.getPredefinedAttributeByValueAndLabel(oa, "abc", "def"));
		assertNull(importer.getPredefinedAttributeByValueAndLabel(oa, "abc", "Label2"));
		assertNull(importer.getPredefinedAttributeByValueAndLabel(oa, "Value2", "label2"));
		assertNull(importer.getPredefinedAttributeByValueAndLabel(oa, "value2", "Label2"));

		assertSame(pa2, importer.getPredefinedAttributeByValueAndLabel(oa, "Value2", "Label2"));
		assertSame(pa3, importer.getPredefinedAttributeByValueAndLabel(oa, "Value3", "Label3"));
	}

	public void testGetObjectDefinitionByName(){
		AbstractImporter importer = new ObjectAttributeImporter();

		Schema schema = new Schema();
		schema.setObjectDefinitions(new ArrayList<ObjectDefinition>());
		ObjectDefinition od1 = new ObjectDefinition();
		od1.setName("Name1");
		ObjectDefinition od2 = new ObjectDefinition();
		od2.setName("Name2");
		ObjectDefinition od3 = new ObjectDefinition();
		od3.setName("Name3");
		schema.getObjectDefinitions().add(od1);
		schema.getObjectDefinitions().add(od2);
		schema.getObjectDefinitions().add(od3);

		assertNull(importer.getObjectDefinitionByName(schema, null));
		assertNull(importer.getObjectDefinitionByName(schema, ""));
		assertNull(importer.getObjectDefinitionByName(schema, "abc"));
		assertNull(importer.getObjectDefinitionByName(schema, "name2"));

		assertSame(od2, importer.getObjectDefinitionByName(schema, "Name2"));
		assertSame(od3, importer.getObjectDefinitionByName(schema, "Name3"));
	}
}
