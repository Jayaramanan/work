/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.jtreecombobox;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

import com.ni3.ag.navigator.client.domain.Value;

@SuppressWarnings("serial")
public class TreeListCellRenderer extends JPanel implements ListCellRenderer{
	private static final JTree tree = new JTree();
	TreeModel treeModel;
	TreeCellRenderer treeRenderer;
	IndentBorder indentBorder = new IndentBorder();

	public TreeListCellRenderer(TreeModel treeModel, TreeCellRenderer treeRenderer){
		this.treeModel = treeModel;
		this.treeRenderer = treeRenderer;

		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setBorder(indentBorder);
		setOpaque(false);
		tree.setRootVisible(false);
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		if (value == null){ // if selected value is null
			removeAll();
			return this;
		}

		boolean leaf = treeModel.isLeaf(value);
		Component comp = treeRenderer.getTreeCellRendererComponent(tree, value, isSelected, true, leaf, index, cellHasFocus);

		if (value instanceof DefaultMutableTreeNode){
			Value val = (Value) ((DefaultMutableTreeNode) value).getUserObject();

			if (val != null){
				comp.setEnabled(val.isEnabled());
			}
		}

		removeAll();
		add(comp);

		// compute the depth of value
		PreorderEnumeration enumer = new PreorderEnumeration(treeModel);
		for (int i = 0; i <= index; i++)
			enumer.nextElement();
		indentBorder.setDepth(enumer.getDepth());

		return this;
	}
}
