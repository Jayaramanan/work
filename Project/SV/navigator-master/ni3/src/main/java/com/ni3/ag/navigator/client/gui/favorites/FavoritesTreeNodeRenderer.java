/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.favorites;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.ni3.ag.navigator.client.domain.Favorite;
import com.ni3.ag.navigator.client.domain.Folder;

public class FavoritesTreeNodeRenderer implements TreeCellRenderer{

	DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

	public FavoritesTreeNodeRenderer(){
		renderer.setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
	}

	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected,
			final boolean expanded, final boolean leaf, final int row, final boolean hasFocus){

		renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		if (value != null){
			if (value instanceof Favorite){
				final Favorite favorite = (Favorite) value;
				renderer.setIcon(favorite.getIcon());
				renderer.setText(favorite.getName());
				if (favorite.getDescription() != null && !favorite.getDescription().isEmpty()){
					final String cleanDescription = favorite.getDescription().replaceAll("[\r\n]", " ")
							.replaceAll("  ", " ");
					renderer.setToolTipText(cleanDescription);
				} else{
					renderer.setToolTipText("");
				}
			} else if (value instanceof Folder){
				final Folder folder = (Folder) value;
				renderer.setIcon(folder.getIcon());
				renderer.setText(folder.getName());
			}
		}

		return renderer;
	}
}
