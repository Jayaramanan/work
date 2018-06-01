/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;

public class CheckBoxTreeCellEditor extends AbstractCellEditor implements TreeCellEditor{

	private static final long serialVersionUID = 1L;

	// internal renderer for this editor
	private CheckBoxTreeCellRenderer renderer;

	private JTree tree;

	public CheckBoxTreeCellEditor(JTree tree){
		super();
		this.tree = tree;
		this.renderer = new CheckBoxTreeCellRenderer();
	}

	public Object getCellEditorValue(){
		JCheckBox cb = renderer.getNodeRenderer();
		return cb.isSelected();
	}

	public boolean isCellEditable(EventObject event){
		boolean returnValue = true;
		if (event instanceof MouseEvent){
			returnValue = false;
			MouseEvent mouseEvent = (MouseEvent) event;
			TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
			if (path != null){
				Object node = path.getLastPathComponent();
				if (!(node instanceof ACRootNode))
					returnValue = true;
			}
		}

		return returnValue;
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
	        int row){
		Component editor = renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);
		// editor always selected / focused
		ItemListener itemListener = new ItemListener(){
			public void itemStateChanged(ItemEvent itemEvent){
				stopCellEditing();
			}
		};
		if (editor instanceof JCheckBox)
			((JCheckBox) editor).addItemListener(itemListener);

		return editor;
	}
}