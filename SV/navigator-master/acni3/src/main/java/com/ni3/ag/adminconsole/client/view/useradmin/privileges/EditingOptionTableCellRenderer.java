/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin.privileges;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.domain.EditingOption;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public class EditingOptionTableCellRenderer implements TableCellRenderer{
	private static final long serialVersionUID = -4116466379646699874L;

	private JCheckBox checkBox;
	private JTextField textField;

	public EditingOptionTableCellRenderer(){
		checkBox = new JCheckBox();
		checkBox.setHorizontalAlignment(SwingConstants.CENTER);
		textField = new JTextField();
		textField.setBorder(BorderFactory.createEmptyBorder());
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		Component current = null;

		GroupPrivilegesTableModel tableModel = (GroupPrivilegesTableModel) table.getModel();
		int modelRow = table.convertRowIndexToModel(row);
		Object node = tableModel.nodeForRow(modelRow);
		int modelColumn = table.convertColumnIndexToModel(column);

		if (node instanceof ObjectDefinition && modelColumn == GroupPrivilegesTableModel.CAN_UPDATE_INDEX){
			checkBox.setSelected(value != null && (Boolean) value);
			current = checkBox;
		} else{
			Object toShow = null;
			if (node instanceof ObjectAttribute){
				toShow = getDisplayValue(value);
			}
			textField.setText(toShow != null ? toShow.toString() : null);
			current = textField;
		}

		boolean editable = table.getModel().isCellEditable(modelRow, modelColumn);
		current.setEnabled(editable);

		return current;
	}

	private Object getDisplayValue(Object value){
		Object toShow = value;
		if (value instanceof EditingOption)
			toShow = Translation.get(((EditingOption) value).getLabelID());
		return toShow;
	}

}
