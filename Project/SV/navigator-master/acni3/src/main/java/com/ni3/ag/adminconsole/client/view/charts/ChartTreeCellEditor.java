/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.ni3.ag.adminconsole.client.controller.charts.UpdateObjectChartsButtonListener;
import com.ni3.ag.adminconsole.client.view.common.StringValidator;
import com.ni3.ag.adminconsole.domain.Chart;

public class ChartTreeCellEditor extends DefaultTreeCellEditor{

	public ChartTreeCellEditor(JTree tree, DefaultTreeCellRenderer tce){
		super(tree, tce);
		this.tree = tree;
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, final Object value, boolean isSelected, boolean expanded,
	        boolean leaf, int row){
		Object showValue = value;
		if (value instanceof Chart)
			showValue = ((Chart) value).getName();
		Component container = super.getTreeCellEditorComponent(tree, showValue, isSelected, expanded, leaf, row);

		final JTextField tField = (JTextField) super.editingComponent;
		// editor always selected / focused
		ActionListener listener = new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if (value instanceof Chart){
					Chart chrt = (Chart) value;
					String oldName = chrt.getName();
					String newName = StringValidator.validate(tField.getText());
					if (newName != null && !newName.equals(oldName)){
						chrt.setName(newName);
						boolean ok = save(chrt);
						if (!ok)
							chrt.setName(oldName);
						else
							stopCellEditing();
					} else{
						tField.setText(oldName);
						stopCellEditing();
					}
				}
			}
		};
		ActionListener[] listeners = tField.getActionListeners();
		for (ActionListener lr : listeners)
			tField.removeActionListener(lr);
		tField.addActionListener(listener);

		return container;
	}

	private boolean save(Chart chrt){
		CellEditorListener[] listeners = getCellEditorListeners();
		for (CellEditorListener listener : listeners){
			if (listener instanceof UpdateObjectChartsButtonListener){
				return ((UpdateObjectChartsButtonListener) listener).saveFromTree(chrt);
			}
		}
		return false;
	}

	public boolean isCellEditable(EventObject event){
		boolean returnValue = super.isCellEditable(event);
		if (returnValue){
			Object node = tree.getLastSelectedPathComponent();
			if ((node != null) && (node instanceof Chart)){
				returnValue = true;
			} else
				returnValue = false;
		}
		return returnValue;
	}

}
