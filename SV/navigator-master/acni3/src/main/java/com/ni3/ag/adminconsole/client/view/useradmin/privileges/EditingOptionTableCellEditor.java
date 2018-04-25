/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin.privileges;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

import com.ni3.ag.adminconsole.client.view.appconf.SettingsBooleanCheckBox;
import com.ni3.ag.adminconsole.domain.EditingOption;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;

public class EditingOptionTableCellEditor extends AbstractCellEditor implements TableCellEditor{
	private static final long serialVersionUID = 3362213004588488760L;

	private JCheckBox checkBox;
	private JComboBox comboBox;
	private Component current;

	public EditingOptionTableCellEditor(){
		checkBox = new SettingsBooleanCheckBox();
		checkBox.setHorizontalAlignment(SwingConstants.CENTER);
		checkBox.setBorderPainted(false);
		checkBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e){
				fireEditingStopped();
			}
		});

		comboBox = new JComboBox();
		comboBox.setBorder(BorderFactory.createEmptyBorder());
		comboBox.setRenderer(new EditingOptionListCellRenderer());
		for (EditingOption opt : EditingOption.values()){
			comboBox.addItem(opt);
		}
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		GroupPrivilegesTableModel tableModel = (GroupPrivilegesTableModel) table.getModel();
		int modelRow = table.convertRowIndexToModel(row);
		int modelColumn = table.convertColumnIndexToModel(column);
		Object node = tableModel.nodeForRow(modelRow);

		if (node instanceof ObjectAttribute){
			comboBox.setSelectedItem(value);
			current = comboBox;
		} else if (modelColumn == GroupPrivilegesTableModel.CAN_UPDATE_INDEX){
			checkBox.setSelected((Boolean) value);
			current = checkBox;
		}
		return current;
	}

	@Override
	public Object getCellEditorValue(){
		Object value = null;
		if (current instanceof JCheckBox){
			value = ((JCheckBox) current).isSelected();
		} else if (current instanceof JComboBox){
			value = ((JComboBox) current).getSelectedItem();
		}
		return value;
	}

	@Override
	public boolean isCellEditable(EventObject e){
		if (e instanceof MouseEvent && current instanceof JComboBox){
			return ((MouseEvent) e).getClickCount() >= 2;
		}
		return true;
	}
}
