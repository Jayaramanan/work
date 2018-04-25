/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.view.schemaadmin;

import javax.swing.JTree;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

/**
 *
 * @author user
 */
public class SchemaAdminTreeCellRendererTest extends ACTestCase{
	public void testCellRenderer(){
		SchemaAdminTreeCellRenderer renderer = new SchemaAdminTreeCellRenderer();		
		SchemaAdminTreeCellRenderer component = (SchemaAdminTreeCellRenderer) renderer.getTreeCellRendererComponent(new JTree(), "root", false, true, true, 0, false);
		assertEquals(component.getText(), "root");
		ObjectDefinition od = createObjectDefinition();
		component = (SchemaAdminTreeCellRenderer) renderer.getTreeCellRendererComponent(new JTree(), od, false, true, true, 0, false);
		assertEquals(od.getName(), component.getText());
	}

	private ObjectDefinition createObjectDefinition() {
		ObjectDefinition od = new ObjectDefinition();
		od.setName("hello");
		return od;
	}
}
