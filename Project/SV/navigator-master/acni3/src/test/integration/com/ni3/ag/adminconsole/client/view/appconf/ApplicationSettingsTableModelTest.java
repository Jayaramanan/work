/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.view.appconf;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.ApplicationSetting;

public class ApplicationSettingsTableModelTest extends ACTestCase{

	private ApplicationSettingsTableModel model;
	private List<ApplicationSetting> settings;
	@Override
	public void setUp(){
		settings = generateSettings();
		model = new ApplicationSettingsTableModel(settings);
	}

	private List<ApplicationSetting> generateSettings(){
		ArrayList<ApplicationSetting> ar = new ArrayList<ApplicationSetting>();		
		for(int i = 0; i < 10; i++){
			int id = i +  1;
			ApplicationSetting as = new ApplicationSetting();
			as.setProp("prop" + id);
			as.setSection("sect" + id);
			as.setValue("val" + id);
			ar.add(as);
		}
	    return ar;
    }
	
	public void testColumnCount(){
		assertEquals(3, model.getColumnCount());
	}
	
	public void testColumnClass(){
		for(int i = 0; i < model.getColumnCount(); i++)
			assertEquals(String.class, model.getColumnClass(i));
	}
	
	public void testColumnName(){
		String[] names = new String[]{"Section", "Property", "Value"};
		for(int i = 0; i < model.getColumnCount(); i++)
			assertEquals(names[i], model.getColumnName(i));
	}
	
	public void testRowCount(){
		assertEquals(model.getRowCount(), settings.size());
	}
	
	public void testValueAt(){
		for(int i = 0; i < settings.size(); i++){
			assertEquals(settings.get(i).getSection(), model.getValueAt(i, 0));
			assertEquals(settings.get(i).getProp(), model.getValueAt(i, 1));
			assertEquals(settings.get(i).getValue(), model.getValueAt(i, 2));
		}
	}
	
	public void testColumnEditable(){
		for(int i = 0; i < model.getRowCount(); i++){
			assertTrue(model.isCellEditable(i, 2));
			assertEquals(settings.get(i).isNew(), model.isCellEditable(i, 0));
			assertEquals(settings.get(i).isNew(), model.isCellEditable(i, 1));
			settings.get(i).setNew(!settings.get(i).isNew());
			assertEquals(settings.get(i).isNew(), model.isCellEditable(i, 0));
			assertEquals(settings.get(i).isNew(), model.isCellEditable(i, 1));
		}
	}
	
	public void testSetValueAt(){
		for(int i = 0; i < settings.size(); i++){
			model.setValueAt("zz" + i, i, 0);
			assertEquals(settings.get(i).getSection(), "zz" + i);
			model.setValueAt("mm" + i, i, 1);
			assertEquals(settings.get(i).getProp(), "mm" + i);
			model.setValueAt("kk" + i, i, 2);
			assertEquals(settings.get(i).getValue(), "kk" + i);			
		}		
	}
}
