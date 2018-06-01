/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class AdvancedStringCellRenderer implements TableCellRenderer{

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		JTextField tb = new JTextField();
		tb.setBorder(new EmptyBorder(0, 0, 0, 0));
		tb.setForeground(table.getForeground());
		tb.setBackground(table.getBackground());
		tb.setText((String) value);
		TableModel model = table.getModel();
		tb.setEditable(model.isCellEditable(row, column));
		if (isSelected){
			tb.setForeground(table.getSelectionForeground());
			tb.setBackground(table.getSelectionBackground());
		}
		return tb;
	}

}
