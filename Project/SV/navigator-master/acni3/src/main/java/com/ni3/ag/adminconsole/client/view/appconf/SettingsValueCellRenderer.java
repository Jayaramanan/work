/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

import com.ni3.ag.adminconsole.client.view.common.ColorButton;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.util.SettingsUtil;

public class SettingsValueCellRenderer implements TableCellRenderer{

	private JCheckBox checkBox;
	private JTextField textField;
	private ColorButton colorButton;

	public SettingsValueCellRenderer(){
		checkBox = new SettingsBooleanCheckBox();
		textField = new JTextField();
		textField.setBorder(BorderFactory.createEmptyBorder());
		colorButton = new ColorButton(false);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		SettingsTableModel tableModel = (SettingsTableModel) table.getModel();
		int modelRow = table.convertRowIndexToModel(row);
		final Setting setting = tableModel.getSelected(modelRow);

		Component current = null;
		if (SettingsUtil.isBooleanSetting(setting)){
			checkBox.setSelected(SettingsUtil.isTrueValue(setting.getValue()));
			current = checkBox;
		} else if (SettingsUtil.isColorSetting(setting)){
			colorButton.setColor(value != null ? value.toString() : null);
			current = colorButton;
		} else{
			textField.setText(value != null ? value.toString() : null);
			current = textField;
		}

		return current;
	}

}
