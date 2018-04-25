/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class ACNumberCellEditor extends DefaultCellEditor{
	private static final long serialVersionUID = 6563257221231846831L;
	private Class<?>[] argTypes = new Class[] { String.class };
	private java.lang.reflect.Constructor<?> constructor;
	private Object value;

	public ACNumberCellEditor(){
		this(new ACTextField());
		((JTextField) getComponent()).setHorizontalAlignment(JTextField.RIGHT);
	}

	public ACNumberCellEditor(JTextField textField){
		super(textField);
	}

	public boolean stopCellEditing(){
		String s = (String) super.getCellEditorValue();
		if ("".equals(s)){
			if (constructor.getDeclaringClass() == String.class){
				value = s;
			} else
				value = null;
			return super.stopCellEditing();
		}

		try{
			value = constructor.newInstance(new Object[] { s });
		} catch (Exception e){
			((JComponent) getComponent()).setBorder(new LineBorder(Color.red));
			return false;
		}
		return super.stopCellEditing();
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		java.awt.Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
		if (c instanceof JTextField){
			JTextField jtf = ((JTextField) c);
			jtf.selectAll();
		}

		((JComponent) getComponent()).setBorder(new LineBorder(Color.black));
		try{
			Class<?> type = table.getColumnClass(column);
			if (type == Object.class){
				type = String.class;
			}
			constructor = type.getConstructor(argTypes);
		} catch (Exception e){
			return null;
		}
		return c;
	}

	public Object getCellEditorValue(){
		return value;
	}
}
