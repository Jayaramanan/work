/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.domain.DataType;

public class DataTypeRenderer extends DefaultTableCellRenderer{

	private static final long serialVersionUID = 1081934332327316327L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		Object dataType = value;
		if (value instanceof DataType)
			dataType = Translation.get(((DataType) value).getTextId());
		return super.getTableCellRendererComponent(table, dataType, isSelected, hasFocus, row, column);
	}
}
