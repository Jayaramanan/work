/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.ni3.ag.adminconsole.client.view.common.ColorButton;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.util.SettingsUtil;

public class SettingsValueCellEditor extends AbstractCellEditor implements TableCellEditor{
	private static final long serialVersionUID = 7224192443267068282L;

	private JCheckBox checkBox;
	private JTextField textField;
	private ColorButton colorButton;
	private Component current;

	public SettingsValueCellEditor(){
		checkBox = new SettingsBooleanCheckBox();

		textField = new JTextField();
		textField.setBorder(BorderFactory.createEmptyBorder());

		colorButton = new ColorButton(true);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){

		SettingsTableModel tableModel = (SettingsTableModel) table.getModel();
		int modelRow = table.convertRowIndexToModel(row);
		final Setting setting = tableModel.getSelected(modelRow);

		if (SettingsUtil.isBooleanSetting(setting)){
			checkBox.setSelected(SettingsUtil.isTrueValue(setting.getValue()));
			current = checkBox;
		} else if (SettingsUtil.isColorSetting(setting)){
			colorButton.setColor(setting.getValue());
			current = colorButton;
		} else{
			textField.setText(value != null ? value.toString() : null);
			current = textField;
		}

		return current;
	}

	@Override
	public Object getCellEditorValue(){
		String value = null;
		if (current instanceof JCheckBox){
			Object selected = ((JCheckBox) current).isSelected();
			value = selected.toString().toUpperCase();
		} else if (current instanceof ColorButton){
			value = ((ColorButton) current).getText();
		} else if (current instanceof JTextField){
			value = ((JTextField) current).getText();
		}
		return value;
	}

	@Override
	public boolean isCellEditable(EventObject e){
		if (e instanceof MouseEvent){
			return ((MouseEvent) e).getClickCount() >= 2;
		}
		return true;
	}

}
