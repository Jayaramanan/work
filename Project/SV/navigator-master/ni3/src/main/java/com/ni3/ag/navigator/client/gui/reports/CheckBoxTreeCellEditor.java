/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.reports;

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

public class CheckBoxTreeCellEditor extends AbstractCellEditor implements TreeCellEditor{

	private static final long serialVersionUID = 1L;

	// internal renderer for this editor
	private CheckBoxTreeCellRenderer renderer;

	private JTree tree;

	public CheckBoxTreeCellEditor(JTree tree){
		super();
		this.tree = tree;
		this.renderer = new CheckBoxTreeCellRenderer();
		ItemListener itemListener = new ItemListener(){
			public void itemStateChanged(ItemEvent itemEvent){
				stopCellEditing();
			}
		};
		renderer.getNodeRenderer().addItemListener(itemListener);
	}

	public Object getCellEditorValue(){
		JCheckBox cb = renderer.getNodeRenderer();
		return new Boolean(cb.isSelected());
	}

	public boolean isCellEditable(EventObject event){
		boolean returnValue = true;
		if (event instanceof MouseEvent){
			returnValue = false;
			MouseEvent mouseEvent = (MouseEvent) event;
			TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
			if (path != null){
				Object node = path.getLastPathComponent();
				if (!(node instanceof RootNode))
					returnValue = true;
			}
		}

		return returnValue;
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
	        int row){
		Component editor = renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);
		return editor;
	}
}