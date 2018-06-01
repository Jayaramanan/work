/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.languageadmin;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

public class SimpleStringRenderer extends DefaultTableCellRenderer{
	private JTextField textField;
	private static final long serialVersionUID = 8428594223292666253L;

	public SimpleStringRenderer(){
		textField = new JTextField();
		textField.setBorder(BorderFactory.createEmptyBorder());
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		textField.setText((String) value);
		return textField;
	}
}
