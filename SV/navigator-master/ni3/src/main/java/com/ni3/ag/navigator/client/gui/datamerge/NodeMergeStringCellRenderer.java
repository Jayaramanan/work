/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.datamerge;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class NodeMergeStringCellRenderer extends DefaultTableCellRenderer{

	private static final long serialVersionUID = 1L;
	private static final Color selectedFg = new Color(200, 230, 180);
	// new Color(30, 160, 30);// new Color(100, 180, 0);
	private static final Color notSelectedFg = Color.BLACK;
	private static final Color notEditableFg = new Color(150, 150, 150);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		int zrow = table.convertRowIndexToModel(row);
		int zcolumn = table.convertColumnIndexToModel(column);
		final TableModel model = table.getModel();

		Color fg = notSelectedFg;
		Color bg = table.getBackground();
		if (!model.isCellEditable(row, NodeMergeTableModel.SELECTION_COLUMN_INDEX)){
			fg = notEditableFg;
		} else if (zcolumn == NodeMergeTableModel.VALUE_FROM_COLUMN_INDEX
		        || zcolumn == NodeMergeTableModel.VALUE_TO_COLUMN_INDEX){
			boolean newSelected = (Boolean) model.getValueAt(zrow, NodeMergeTableModel.SELECTION_COLUMN_INDEX);

			if ((zcolumn == NodeMergeTableModel.VALUE_FROM_COLUMN_INDEX && newSelected)
			        || (zcolumn == NodeMergeTableModel.VALUE_TO_COLUMN_INDEX && !newSelected)){
				bg = selectedFg;
			}
		}

		c.setForeground(fg);
		c.setBackground(bg);

		return c;
	}
}
