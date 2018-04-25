/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.view.useradmin;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;

public class AttributeGroupTableModelTest extends ACTestCase{
	
	public void testColumns(){
		AttributeGroupTableModel model = new AttributeGroupTableModel();
		assertEquals(3, model.getColumnCount());
		Class<?>[] _classes = new Class[] { String.class, Boolean.class, Boolean.class, Boolean.class,
		        Boolean.class };
		for(int i = 0; i < model.getColumnCount(); i++)
			assertEquals(model.getColumnClass(i), _classes[i]);
	}
	
	public void testGetRowCount(){
		List<AttributeGroup> ags = generateAttributeGroups();
		AttributeGroupTableModel model = new AttributeGroupTableModel();
		model.setData(ags);
		assertEquals(ags.size(), model.getRowCount());
		model = new AttributeGroupTableModel(ags);
		assertEquals(ags.size(), model.getRowCount());
	}

	private List<AttributeGroup> generateAttributeGroups(){
		ArrayList<AttributeGroup> ar = new ArrayList<AttributeGroup>();
		for(int i = 1; i <= 10; i++){
			AttributeGroup ag = new AttributeGroup();
			ag.setObjectAttribute(generateObjectAttribute(i));
			ag.setCanRead(i % 2 == 0);
			ag.setCanUpdate(i % 2 == 0);
			ag.setGroup(generateGroup(i));
			ar.add(ag);
		}
	    return ar;
    }

	private Group generateGroup(int i){
		return new Group();
    }

	private ObjectAttribute generateObjectAttribute(int i){
		ObjectAttribute oa = new ObjectAttribute(null);
		oa.setId(i);
		oa.setLabel("label" + i);
		return oa;
    }
	
	public void testGetValueAT(){
		AttributeGroupTableModel model = new AttributeGroupTableModel();
		List<AttributeGroup> ags = generateAttributeGroups();
		model.setData(ags);
		for(int i = 0; i < ags.size(); i++){
			AttributeGroup ag = ags.get(i);
			assertEquals(ag.getObjectAttribute().getLabel(), model.getValueAt(i, 0));
			assertEquals(ag.isCanRead(), model.getValueAt(i, 1));
			assertEquals(ag.isCanUpdate(), model.getValueAt(i, 2));
		}
	}
	
	public void testCellEditable(){
		boolean[] b = new boolean[]{false, true, true, true, true};
		AttributeGroupTableModel model = new AttributeGroupTableModel();
		for(int i = 0; i < model.getColumnCount(); i++)
			assertEquals(b[i], model.isCellEditable(0, i));
	}
	
	public void testSetValueAT(){
		AttributeGroupTableModel model = new AttributeGroupTableModel();
		List<AttributeGroup> ags = generateAttributeGroups();
		model.setData(ags);
		for(int i = 0; i < model.getRowCount(); i++)
			for(int j = 1; j < model.getColumnCount(); j++){
				model.setValueAt(true, i, j);
				assertTrue((Boolean) model.getValueAt(i, j));
				model.setValueAt(false, i, j);
				assertFalse((Boolean)model.getValueAt(i, j));
			}
				
	}
	
	public void testGetSelected(){
		AttributeGroupTableModel model = new AttributeGroupTableModel();
		List<AttributeGroup> ags = generateAttributeGroups();
		model.setData(ags);
		for(int i = 0; i < model.getRowCount(); i++)
			assertEquals(ags.get(i), model.getSelected(i));		
	}
	
	public void testIndexOf(){
		AttributeGroupTableModel model = new AttributeGroupTableModel();
		List<AttributeGroup> ags = generateAttributeGroups();
		model.setData(ags);
		for(int i = 0; i < model.getRowCount(); i++)
			assertEquals(i, model.indexOf(ags.get(i)));
	}
}
