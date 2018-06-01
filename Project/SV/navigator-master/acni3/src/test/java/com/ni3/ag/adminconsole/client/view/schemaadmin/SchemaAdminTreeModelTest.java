/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class SchemaAdminTreeModelTest extends ACTestCase{
	private SchemaAdminTreeModel model;
	private DatabaseInstance dbi;
	private List<Schema> schemas;

	public void setUp(){
		dbi = new DatabaseInstance("test_db");
		Schema s1 = new Schema();
		s1.setId(1);
		Schema s2 = new Schema();
		s2.setId(4);
		ObjectDefinition od1 = new ObjectDefinition();
		od1.setId(2);
		ObjectDefinition od2 = new ObjectDefinition();
		od2.setId(3);
		List<ObjectDefinition> odList = new ArrayList<ObjectDefinition>();
		odList.add(od1);
		odList.add(od2);
		s1.setObjectDefinitions(odList);

		List<DatabaseInstance> dbNames = new ArrayList<DatabaseInstance>();
		dbNames.add(dbi);

		schemas = new ArrayList<Schema>();
		schemas.add(s1);
		schemas.add(s2);

		Map<DatabaseInstance, List<Schema>> schemaMap = new HashMap<DatabaseInstance, List<Schema>>();
		schemaMap.put(dbi, schemas);

		model = new SchemaAdminTreeModel(schemaMap, dbNames);
	}

	public void testGetChild(){
		Object s = model.getChild(dbi, 0);
		assertEquals(schemas.get(0), s);
		Object d = model.getChild(new ACRootNode(), 0);
		assertEquals(dbi, d);
		Object o = model.getChild(schemas.get(0), 1);
		List<ObjectDefinition> odList = schemas.get(0).getObjectDefinitions();
		assertEquals(odList.get(1), o);
	}

	public void testGetChildCount(){
		int c = model.getChildCount(new ACRootNode());
		assertEquals(1, c);
		c = model.getChildCount(dbi);
		assertEquals(schemas.size(), c);
		c = model.getChildCount(schemas.get(0));
		assertEquals(2, c);
		c = model.getChildCount(schemas.get(1));
		assertEquals(0, c);
		c = model.getChildCount("");
		assertEquals(0, c);
	}

	public void testIsLeaf(){
		ACRootNode root = new ACRootNode();
		assertFalse(model.isLeaf(root));
		assertFalse(model.isLeaf(schemas.get(0)));
		assertFalse(model.isLeaf(schemas.get(1)));
		assertTrue(model.isLeaf(schemas.get(0).getObjectDefinitions().get(0)));
		model.setDatabaseInstances(null);
		assertTrue(model.isLeaf(root));
	}

	public void testGetIndexOfChild(){
		int i = model.getIndexOfChild(new ACRootNode(), dbi);
		assertEquals(0, i);
		i = model.getIndexOfChild(dbi, schemas.get(1));
		assertEquals(1, i);
		Schema s = schemas.get(0);
		i = model.getIndexOfChild(s, s.getObjectDefinitions().get(1));
		assertEquals(1, i);
		i = model.getIndexOfChild(s, s.getObjectDefinitions().get(1));
		assertEquals(1, i);
		i = model.getIndexOfChild(s, "");
		assertEquals(-1, i);
		i = model.getIndexOfChild("", s);
		assertEquals(-1, i);
	}

}
