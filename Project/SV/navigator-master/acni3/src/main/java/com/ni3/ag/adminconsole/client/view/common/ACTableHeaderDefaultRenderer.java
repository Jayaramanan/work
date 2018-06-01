/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class ACTableHeaderDefaultRenderer implements TableCellRenderer{
	private static final long serialVersionUID = 2922432048939946612L;
	private TableCellRenderer defaultCellRenderer;

	public ACTableHeaderDefaultRenderer(TableCellRenderer defaultRenderer){
		defaultCellRenderer = defaultRenderer;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		Component c = defaultCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (table.getModel() instanceof ACTableModel){
			ACTableModel tm = (ACTableModel) table.getModel();
			TableColumnModel tcm = table.getColumnModel();
			int index = tcm.getColumn(column).getModelIndex();
			if (tm.isColumnMandatory(index))
				c.setForeground(Color.RED);
		}
		return c;
	}
}
