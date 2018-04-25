/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ObjectDefinitionCellRenderer extends DefaultTableCellRenderer{

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		Object toShow = value;
		if (value instanceof ObjectDefinition)
			toShow = ((ObjectDefinition) value).getName();
		return super.getTableCellRendererComponent(table, toShow, isSelected, hasFocus, row, column);
	}

}
