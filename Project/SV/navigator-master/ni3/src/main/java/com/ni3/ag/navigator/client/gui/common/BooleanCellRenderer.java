package com.ni3.ag.navigator.client.gui.common;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class BooleanCellRenderer extends DefaultTableCellRenderer{
	private static final long serialVersionUID = -3637817359360845249L;
	private TableCellRenderer defaultRenderer;

	public BooleanCellRenderer(TableCellRenderer boolCellRenderer){
		defaultRenderer = boolCellRenderer;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		Component c = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (row < table.getRowCount() && column < table.getColumnCount()){
			int zrow = table.convertRowIndexToModel(row);
			int zcolumn = table.convertColumnIndexToModel(column);
			boolean b = table.getModel().isCellEditable(zrow, zcolumn);
			c.setEnabled(b);
		}
		return c;
	}

}
