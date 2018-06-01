/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.model;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.schemaadmin.ObjectAttributeTableModel;
import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;

public class ObjectAttributeTableModelTest extends ACTestCase{

	public void testIsEditable(){
		List<Schema> schemas = getSchemas();
		Schema schema = schemas.get(0);
		ObjectDefinition od = schema.getObjectDefinitions().get(0);
		ObjectAttributeTableModel tm = new ObjectAttributeTableModel(od.getObjectAttributes(), null);
		od.getObjectAttributes().get(0).setPredefined(false);
		assertFalse(tm.isCellEditable(0, 6));
		od.getObjectAttributes().get(0).setPredefined(true);
		assertTrue(tm.isCellEditable(0, 6));
	}

	private List<Schema> getSchemas(){
		int id = 1;
		ArrayList<Schema> res = new ArrayList<Schema>();

		Schema objectDefinition1 = new Schema();
		objectDefinition1.setId(id++);
		objectDefinition1.setName("Schema1");

		List<ObjectDefinition> objectDefinitions1 = new ArrayList<ObjectDefinition>();
		ObjectDefinition child1 = new ObjectDefinition();
		child1.setId(id++);
		child1.setName("ObjectDefinition1");
		child1.setSchema(objectDefinition1);
		child1.setObjectAttributes(generateObjectAttributes(child1, 1, 10));
		objectDefinitions1.add(child1);

		ObjectDefinition child2 = new ObjectDefinition();
		child2.setId(id++);
		child2.setName("ObjectDefinition2");
		child2.setSchema(objectDefinition1);
		child2.setObjectAttributes(generateObjectAttributes(child2, 50, 10));
		objectDefinitions1.add(child2);

		ObjectDefinition child3 = new ObjectDefinition();
		child3.setId(id++);
		child3.setName("ObjectDefinition3");
		child3.setSchema(objectDefinition1);
		objectDefinitions1.add(child3);

		objectDefinition1.setObjectDefinitions(objectDefinitions1);
		res.add(objectDefinition1);

		Schema objectDefinition2 = new Schema();
		objectDefinition2.setId(id++);
		objectDefinition2.setName("Schema2");
		List<ObjectDefinition> objectDefinitions2 = new ArrayList<ObjectDefinition>();

		ObjectDefinition child4 = new ObjectDefinition();
		child4.setId(id++);
		child4.setName("ObjectDefinition1");
		child4.setSchema(objectDefinition2);
		child4.setObjectAttributes(generateObjectAttributes(child4, 100, 10));
		objectDefinitions2.add(child4);

		ObjectDefinition child5 = new ObjectDefinition();
		child5.setId(id++);
		child5.setName("ObjectDefinition2");
		child5.setSchema(objectDefinition2);
		objectDefinitions2.add(child5);

		ObjectDefinition child6 = new ObjectDefinition();
		child6.setId(id++);
		child6.setName("ObjectDefinition3");
		child6.setSchema(objectDefinition2);
		objectDefinitions2.add(child6);

		objectDefinition2.setObjectDefinitions(objectDefinitions2);
		res.add(objectDefinition2);

		return res;
	}

	private List<ObjectAttribute> generateObjectAttributes(ObjectDefinition parent, int baseId, int count){
		ArrayList<ObjectAttribute> attrs = new ArrayList<ObjectAttribute>();
		for (int i = 0; i < count; i++){
			ObjectAttribute oa = new ObjectAttribute(parent);
			oa.setId(baseId + i);
			oa.setSort(1);
			oa.setName("attr" + oa.getId());
			oa.setLabel("label" + oa.getId());
			oa.setPredefined(Boolean.TRUE);
			oa.setDescription(oa.getName());
			oa.setDataType(DataType.TEXT);
			oa.setInFilter(true);
			oa.setInLabel(true);
			oa.setInToolTip(true);
			oa.setInSimpleSearch(true);
			oa.setInAdvancedSearch(true);
			oa.setInMetaphor(true);

			attrs.add(oa);
		}
		return attrs;
	}

}
