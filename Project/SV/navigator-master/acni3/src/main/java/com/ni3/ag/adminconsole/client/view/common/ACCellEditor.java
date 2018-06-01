/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.*;
import javax.swing.*;

public class ACCellEditor extends DefaultCellEditor{

	private static final long serialVersionUID = 1L;
	private ACComboBox combo;

	public ACCellEditor(final ACComboBox comboBox){
		super(comboBox);
		this.combo = comboBox;
		this.setClickCountToStart(2);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		combo.setInitialSelectedItem(value);
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	public boolean contains(Object item){
		for (int i = 0; i < combo.getItemCount(); i++){
			if (combo.getItemAt(i) != null && combo.getItemAt(i).equals(item)){
				return true;
			}
		}
		return false;
	}
}
