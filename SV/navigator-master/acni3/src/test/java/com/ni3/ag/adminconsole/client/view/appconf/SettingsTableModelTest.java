/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.util.ArrayList;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;

import junit.framework.TestCase;

public class SettingsTableModelTest extends TestCase{

	private SettingsTableModel model;
	private User user;

	@Override
	public void setUp(){
		user = generateUser();
		model = new SettingsTableModel(user.getSettings());
	}

	private User generateUser(){
		User u = new User();
		u.setSettings(new ArrayList<UserSetting>());
		for (int i = 0; i < 10; i++){
			UserSetting gs = new UserSetting();
			gs.setUser(u);
			gs.setSection("sect" + i);
			gs.setProp("prop" + i);
			gs.setValue("val" + i);
			u.getSettings().add(gs);
		}
		return u;
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
		assertEquals(model.getRowCount(), user.getSettings().size());
	}

	public void testValueAt(){
		for (int i = 0; i < user.getSettings().size(); i++){
			assertEquals(user.getSettings().get(i).getSection(), model.getValueAt(i, 0));
			assertEquals(user.getSettings().get(i).getProp(), model.getValueAt(i, 1));
			assertEquals(user.getSettings().get(i).getValue(), model.getValueAt(i, 2));
		}
	}

	public void testColumnEditable(){
		for (int i = 0; i < model.getRowCount(); i++){
			assertTrue(model.isCellEditable(i, 2));
			assertEquals(user.getSettings().get(i).isNew(), model.isCellEditable(i, 0));
			assertEquals(user.getSettings().get(i).isNew(), model.isCellEditable(i, 1));
			user.getSettings().get(i).setNew(!user.getSettings().get(i).isNew());
			assertEquals(user.getSettings().get(i).isNew(), model.isCellEditable(i, 0));
			assertEquals(user.getSettings().get(i).isNew(), model.isCellEditable(i, 1));
		}
	}

	public void testSetValueAt(){
		for (int i = 0; i < user.getSettings().size(); i++){
			model.setValueAt("zz" + i, i, 0);
			assertEquals(user.getSettings().get(i).getSection(), "zz" + i);
			model.setValueAt("mm" + i, i, 1);
			assertEquals(user.getSettings().get(i).getProp(), "mm" + i);
			model.setValueAt("kk" + i, i, 2);
			assertEquals(user.getSettings().get(i).getValue(), "kk" + i);
		}
	}

}
