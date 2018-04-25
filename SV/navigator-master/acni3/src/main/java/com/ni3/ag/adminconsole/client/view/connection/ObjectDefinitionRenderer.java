/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.connection;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public class ObjectDefinitionRenderer extends DefaultTableCellRenderer{

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		String objectDefinitionName = value != null ? ((ObjectDefinition) value).getName() : null;
		return super.getTableCellRendererComponent(table, objectDefinitionName, isSelected, hasFocus, row, column);
	}
}