/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.view.appconf;

import java.util.ArrayList;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupSetting;

import junit.framework.TestCase;

public class GroupSettingsTableModelTest extends TestCase{

	private GroupSettingsTableModel model;
	private Group group;

	@Override
	public void setUp(){
		group = generateGroup();
		model = new GroupSettingsTableModel(group.getGroupSettings());
	}

	private Group generateGroup(){
		Group g = new Group();
		g.setGroupSettings(new ArrayList<GroupSetting>());
		for(int i = 0; i < 10; i++){
			GroupSetting gs = new GroupSetting();
			gs.setGroup(g);
			gs.setSection("sect" + i);
			gs.setProp("prop" + i);
			gs.setValue("val" + i);
			g.getGroupSettings().add(gs);
		}
		return g;
    }

	public void testColumnCount(){
		assertEquals(3, model.getColumnCount());
	}

	public void testColumnClass(){
		for (int i = 0; i < model.getColumnCount(); i++)
			assertEquals(String.class, model.getColumnClass(i));
	}

	public void testColumnName(){
		String[] names = new String[] { "Section", "Property", "Value" };
		for (int i = 0; i < model.getColumnCount(); i++)
			assertEquals(names[i], model.getColumnName(i));
	}

	public void testRowCount(){
		assertEquals(model.getRowCount(), group.getGroupSettings().size());
	}

	public void testValueAt(){
		for (int i = 0; i < group.getGroupSettings().size(); i++){
			assertEquals(group.getGroupSettings().get(i).getSection(), model.getValueAt(i, 0));
			assertEquals(group.getGroupSettings().get(i).getProp(), model.getValueAt(i, 1));
			assertEquals(group.getGroupSettings().get(i).getValue(), model.getValueAt(i, 2));
		}
	}

	public void testColumnEditable(){
		for(int i = 0; i < model.getRowCount(); i++){
			assertTrue(model.isCellEditable(i, 2));
			assertEquals(group.getGroupSettings().get(i).isNew(), model.isCellEditable(i, 0));
			assertEquals(group.getGroupSettings().get(i).isNew(), model.isCellEditable(i, 1));
			group.getGroupSettings().get(i).setNew(!group.getGroupSettings().get(i).isNew());
			assertEquals(group.getGroupSettings().get(i).isNew(), model.isCellEditable(i, 0));
			assertEquals(group.getGroupSettings().get(i).isNew(), model.isCellEditable(i, 1));
		}
	}

	public void testSetValueAt(){
		for (int i = 0; i < group.getGroupSettings().size(); i++){
			model.setValueAt("zz" + i, i, 0);
			assertEquals(group.getGroupSettings().get(i).getSection(), "zz" + i);
			model.setValueAt("mm" + i, i, 1);
			assertEquals(group.getGroupSettings().get(i).getProp(), "mm" + i);
			model.setValueAt("kk" + i, i, 2);
			assertEquals(group.getGroupSettings().get(i).getValue(), "kk" + i);
		}
	}
}
