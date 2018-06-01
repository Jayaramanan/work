/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class CustomBoolCellRenderer implements TableCellRenderer{

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		int zrow = table.convertRowIndexToModel(row);
		int zcolumn = table.convertColumnIndexToModel(column);
		boolean b = table.getModel().isCellEditable(zrow, zcolumn);
		JCheckBox cb = new JCheckBox();
		cb.setHorizontalAlignment(JLabel.CENTER);
		cb.setForeground(table.getForeground());
		cb.setBackground(table.getBackground());
		Boolean toSet = false;
		if (value != null && value instanceof Boolean)
			toSet = (Boolean) value;
		cb.setSelected((Boolean) toSet);
		cb.setEnabled(b);
		return cb;
	}
}
