/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.reports;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.ni3.ag.navigator.client.domain.cache.IconCache;

public class CheckBoxTreeCellRenderer extends DefaultTreeCellRenderer{

	private static final long serialVersionUID = 1L;

	private final ThreeStateCheckBox nodeRenderer = new ThreeStateCheckBox();

	private Color selectionForeground, selectionBackground, textForeground, textBackground;

	private boolean colorsSet = false;

	protected JCheckBox getNodeRenderer(){
		return nodeRenderer;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
	        boolean leaf, int row, boolean hasFocus){
		Component renderer = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		if (!colorsSet){
			selectionForeground = UIManager.getColor("Tree.selectionForeground");
			selectionBackground = UIManager.getColor("Tree.selectionBackground");
			textForeground = tree.getForeground();
			textBackground = tree.getBackground();
			colorsSet = true;
		}
		if (value instanceof RootNode){
			setIcon(getRootIcon());
		}

		if ((value != null) && (value instanceof TreeEntity || value instanceof TreeAttribute)){
			if (selected){
				nodeRenderer.setForeground(selectionForeground);
				nodeRenderer.setBackground(selectionBackground);
			} else{
				nodeRenderer.setForeground(textForeground);
				nodeRenderer.setBackground(textBackground);
			}

			nodeRenderer.setEnabled(tree.isEnabled());
			nodeRenderer.setFocusPainted(false);
			if (value instanceof TreeEntity){
				TreeEntity entity = (TreeEntity) value;
				nodeRenderer.setText(value.toString());
				nodeRenderer.setSelected(entity.isSelected());
				ReportColumnSelectionTreeModel treeModel = (ReportColumnSelectionTreeModel) tree.getModel();
				if (entity.isSelected()){
					nodeRenderer.setState(treeModel.areAllChildrenSelected(entity) ? ThreeStateCheckBox.SELECTED
					        : ThreeStateCheckBox.PARTIAL);
				}
			} else if (value instanceof TreeAttribute){
				TreeAttribute entity = (TreeAttribute) value;
				nodeRenderer.setText(value.toString());
				nodeRenderer.setSelected(entity.isSelected());
			}

			renderer = nodeRenderer;
		}
		return renderer;
	}

	private ImageIcon getRootIcon(){
		return IconCache.getImageIcon(IconCache.FILTER_NI3_LOGO);
	}

}
