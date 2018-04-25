/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.geoanalytics;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;

public class AttributeTableCellRenderer extends DefaultTableCellRenderer{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		String userName = value != null ? ((ObjectAttribute) value).getLabel() : null;
		return super.getTableCellRendererComponent(table, userName, isSelected, hasFocus, row, column);
	}

}
