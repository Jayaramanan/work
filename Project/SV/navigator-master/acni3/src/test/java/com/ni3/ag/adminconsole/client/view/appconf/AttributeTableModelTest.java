/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

import junit.framework.TestCase;

public class AttributeTableModelTest extends TestCase{

	private AttributeTableModel model;
	private ObjectDefinition object;

	public void setUp(){
		object = generateObjectDefinition();
		model = new AttributeTableModel();
		model.setCurrentObject(object);
	}

	private ObjectDefinition generateObjectDefinition(){
		ObjectDefinition od = new ObjectDefinition();
		List<ObjectAttribute> oaList = new ArrayList<ObjectAttribute>();
		for (int i = 0; i < 10; i++){
			ObjectAttribute oa = new ObjectAttribute();
			oa.setLabel("attr" + i);
			oa.setDescription("descr" + i);
			oaList.add(oa);
			oa.setPredefined(true);
		}
		od.setObjectAttributes(oaList);
		return od;
	}

	public void testColumnCount(){
		assertEquals(2, model.getColumnCount());
	}

	public void testRowCount(){
		assertEquals(model.getRowCount(), object.getObjectAttributes().size());
	}

	public void testValueAt(){
		List<ObjectAttribute> oaList = object.getObjectAttributes();
		for (int i = 0; i < oaList.size(); i++){
			ObjectAttribute oa = oaList.get(i);
			assertEquals(oa.getLabel(), model.getValueAt(i, 0));
			assertEquals(oa.getDescription(), model.getValueAt(i, 1));
		}
	}

	public void testColumnEditable(){
		for (int i = 0; i < model.getRowCount(); i++){
			assertEquals(false, model.isCellEditable(i, 0));
			assertEquals(false, model.isCellEditable(i, 1));
		}
	}

	public void testSetValueAt(){
		List<ObjectAttribute> oaList = object.getObjectAttributes();
		for (int i = 0; i < oaList.size(); i++){
			model.setValueAt("aaa", i, 0);
			model.setValueAt("bbb", i, 1);

			ObjectAttribute oa = oaList.get(i);
			assertEquals(oa.getLabel(), "attr" + i);
			assertEquals(oa.getDescription(), "descr" + i);
		}
	}

}
