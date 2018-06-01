/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.domain.ObjectType;

public class TableObjectTypeRenderer extends DefaultTableCellRenderer{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		String objectTypeName = null;
		if (value instanceof ObjectType){
			objectTypeName = Translation.get(((ObjectType) value).getTextId());
		}
		return super.getTableCellRendererComponent(table, objectTypeName, isSelected, hasFocus, row, column);
	}

}
