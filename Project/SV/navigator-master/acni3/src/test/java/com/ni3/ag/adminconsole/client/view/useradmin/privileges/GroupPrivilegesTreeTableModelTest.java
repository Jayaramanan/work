/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin.privileges;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;

public class GroupPrivilegesTreeTableModelTest extends ACTestCase{
	private GroupPrivilegesTreeTableModel model;
	private List<Schema> schemas;

	public void setUp(){
		schemas = new ArrayList<Schema>();
		Schema s1 = new Schema();
		s1.setId(1);
		createObjectDefinitions(s1);
		Schema s2 = new Schema();
		s2.setId(2);
		createObjectDefinitions(s2);
		schemas.add(s1);
		schemas.add(s2);
		model = new GroupPrivilegesTreeTableModel(schemas);
	}

	private void createObjectDefinitions(Schema s){
		ObjectDefinition od1 = new ObjectDefinition();
		od1.setId(s.getId() * 2);
		createObjectAttributes(od1);
		ObjectDefinition od2 = new ObjectDefinition();
		od2.setId(s.getId() * 3);
		createObjectAttributes(od2);

		List<ObjectDefinition> odList = new ArrayList<ObjectDefinition>();
		odList.add(od1);
		odList.add(od2);
		s.setObjectDefinitions(odList);
	}

	private void createObjectAttributes(ObjectDefinition od){
		ObjectAttribute oa1 = new ObjectAttribute();
		oa1.setId(od.getId() * 2);
		createPredefinedAttributes(oa1);
		ObjectAttribute oa2 = new ObjectAttribute();
		oa2.setId(od.getId() * 3);
		createPredefinedAttributes(oa2);
		List<ObjectAttribute> oaList = new ArrayList<ObjectAttribute>();
		oaList.add(oa1);
		oaList.add(oa2);
		od.setObjectAttributes(oaList);
	}

	private void createPredefinedAttributes(ObjectAttribute oa){
		List<PredefinedAttribute> paList = new ArrayList<PredefinedAttribute>();
		PredefinedAttribute pa1 = new PredefinedAttribute();
		pa1.setId(oa.getId() * 2);
		paList.add(pa1);
		PredefinedAttribute pa2 = new PredefinedAttribute();
		pa2.setId(oa.getId() * 3);
		paList.add(pa2);
		oa.setPredefinedAttributes(paList);
	}

	public void testGetChild(){
		Object child = model.getChild(null, 34);
		Schema s0 = schemas.get(0);
		ObjectDefinition od1 = s0.getObjectDefinitions().get(1);
		ObjectAttribute oa0 = od1.getObjectAttributes().get(0);
		PredefinedAttribute pa1 = oa0.getPredefinedAttributes().get(1);
		assertNull(child);
		child = model.getChild(s0, 1);
		assertEquals(child, od1);
		child = model.getChild(new ACRootNode(), 1);
		assertEquals(child, schemas.get(1));
		child = model.getChild(od1, 0);
		assertEquals(child, oa0);
		child = model.getChild(oa0, 1);
		assertEquals(child, pa1);
	}

	public void testGetChildCount(){
		int cc = model.getChildCount(null);
		assertEquals(cc, 0);
		cc = model.getChildCount(schemas.get(0));
		assertEquals(cc, 2);
		schemas.remove(0);
		cc = model.getChildCount(new ACRootNode());
		assertEquals(cc, 1);
	}

	public void testGetIndexOfChild(){
		int i = model.getIndexOfChild(null, null);
		assertEquals(i, -1);
		i = model.getIndexOfChild(new ACRootNode(), schemas.get(1));
		assertEquals(i, 1);
		i = model.getIndexOfChild(new ACRootNode(), new Schema());
		assertEquals(i, -1);
	}

	public void testIsLeaf(){
		boolean isLeaf = model.isLeaf(null);
		assertTrue(isLeaf);
		isLeaf = model.isLeaf(schemas.get(0));
		assertFalse(isLeaf);
		schemas.get(0).setObjectDefinitions(null);
		isLeaf = model.isLeaf(schemas.get(0));
		assertTrue(isLeaf);
	}

}
