/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

public class ACTextCellEditor extends DefaultCellEditor{

	private static final long serialVersionUID = 6563257221231846831L;

	public ACTextCellEditor(){
		super(new ACTextField());
	}

	public ACTextCellEditor(JTextField textField){
		super(textField);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		java.awt.Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
		if (c instanceof JTextField){
			JTextField jtf = ((JTextField) c);
			jtf.selectAll();
		}
		return c;
	}
}
