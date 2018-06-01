/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.reports;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.DefaultTreeCellEditor;

import com.ni3.ag.adminconsole.client.controller.reports.UpdateReportTemplateButtonListener;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.common.StringValidator;
import com.ni3.ag.adminconsole.domain.ReportTemplate;

public class SchemaTreeCellEditor extends DefaultTreeCellEditor{

	public SchemaTreeCellEditor(JTree tree){
		super(tree, new ACTreeCellRenderer());
		this.tree = tree;
		this.renderer = new ACTreeCellRenderer();
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, final Object value, boolean isSelected, boolean expanded,
	        boolean leaf, int row){
		Object showValue = value;
		if (value instanceof ReportTemplate)
			showValue = ((ReportTemplate) value).getName();
		Component container = super.getTreeCellEditorComponent(tree, showValue, isSelected, expanded, leaf, row);

		final JTextField tField = (JTextField) super.editingComponent;
		// editor always selected / focused
		ActionListener listener = new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if (value instanceof ReportTemplate){
					ReportTemplate rt = (ReportTemplate) value;
					String oldName = rt.getName();
					String newName = StringValidator.validate(tField.getText());
					if (newName != null && !newName.equals(oldName)){
						rt.setName(newName);
						boolean ok = save(rt);
						if (!ok)
							rt.setName(oldName);
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

	private boolean save(ReportTemplate chrt){
		CellEditorListener[] listeners = getCellEditorListeners();
		for (CellEditorListener listener : listeners){
			if (listener instanceof UpdateReportTemplateButtonListener){
				return ((UpdateReportTemplateButtonListener) listener).saveFromTree(chrt);
			}
		}
		return false;
	}

	public boolean isCellEditable(EventObject event){
		boolean returnValue = super.isCellEditable(event);
		if (returnValue){
			Object node = tree.getLastSelectedPathComponent();
			if ((node != null) && (node instanceof ReportTemplate)){
				returnValue = true;
			} else
				returnValue = false;
		}
		return returnValue;
	}

}
