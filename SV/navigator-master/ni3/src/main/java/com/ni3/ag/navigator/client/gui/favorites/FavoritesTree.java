/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.favorites;

import javax.swing.DropMode;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreePath;

import com.ni3.ag.navigator.client.controller.favorites.FavoritesTreeTransferHandler;
import com.ni3.ag.navigator.client.domain.Folder;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.model.FavoritesModel;

public class FavoritesTree extends JTree{
	private static final long serialVersionUID = -5373282173614834341L;

	public FavoritesTree(Ni3Document doc){
		final FavoritesModel favoritesModel = doc.getFavoritesModel();
		setRootVisible(false);

		Folder root = favoritesModel.getRootFolder();
		final FavoritesTreeModel model = new FavoritesTreeModel(doc, root, favoritesModel.getFolders(), favoritesModel
				.getFavorites());
		setModel(model);

		Object path[] = { favoritesModel.getRootFolder(), favoritesModel.getGroupFolder() };
		expandPath(new TreePath(path));

		Object path2[] = { favoritesModel.getRootFolder(), favoritesModel.getMyFolder() };
		expandPath(new TreePath(path2));

		setEditable(true);
		setDragEnabled(true);

		ToolTipManager.sharedInstance().registerComponent(this);

		setCellRenderer(new FavoritesTreeNodeRenderer());

		setTransferHandler(new FavoritesTreeTransferHandler(doc, this));
		setDropMode(DropMode.ON_OR_INSERT);
	}

}
