/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.vers;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import com.ni3.ag.adminconsole.client.view.common.ACComboBox;
import com.ni3.ag.adminconsole.client.view.common.ACCustomComboBoxUIFactory;
import com.ni3.ag.adminconsole.domain.Module;

public class UserModuleCellEditor extends DefaultCellEditor{
	private static final long serialVersionUID = 1L;

	public UserModuleCellEditor(){
		super(createMyCombo());
		this.setClickCountToStart(2);
	}

	private static ACComboBox createMyCombo(){
		ACComboBox cb = new ACComboBox();
		cb.setUI(ACCustomComboBoxUIFactory.getNativeCustomComboBoxUI());
		cb.setRenderer(new ModuleComboCellRenderer());
		return cb;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		if (!(table.getModel() instanceof UserModuleTableModel))
			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		column = table.convertColumnIndexToModel(column);
		UserModuleTableModel model = (UserModuleTableModel) table.getModel();
		JComboBox cb = (JComboBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
		cb.removeAllItems();
		addItems(cb, model, column);
		return cb;
	}

	private void addItems(JComboBox cb, UserModuleTableModel model, int column){
		List<Module> modules = model.getModulesForColumn(column);
		for (Module m : modules){
			cb.addItem(m);
		}
	}
}
