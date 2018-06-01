/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import com.ni3.ag.navigator.client.domain.Favorite;
import com.ni3.ag.navigator.client.domain.Folder;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.JInputValuesDialog;
import com.ni3.ag.navigator.client.gui.favorites.FavoritesTree;
import com.ni3.ag.navigator.client.gui.favorites.FavoritesTreeModel;
import com.ni3.ag.navigator.client.model.Ni3Document;

public class FavoritesTreeActionListener implements ActionListener{
	private FavoritesController controller;
	private FavoritesTree tree;

	public FavoritesTreeActionListener(Ni3Document doc, FavoritesTree tree){
		this.controller = new FavoritesController(doc);
		this.tree = tree;
	}

	@Override
	public void actionPerformed(ActionEvent e){
		if ("CreateFolder".equals(e.getActionCommand())){
			onCreateFolder();
		} else if ("DeleteFolder".equals(e.getActionCommand())){
			onDeleteFolder();
		} else if ("DeleteFavorite".equals(e.getActionCommand())){
			onDeleteFavorite();
		} else if ("SetAsDefault".equals(e.getActionCommand())){
			onSetDefault();
		} else if ("SortFoldersByName".equals(e.getActionCommand())){
			onSortFoldersByName();
		}
	}

	private void onSortFoldersByName(){
		Object object = tree.getLastSelectedPathComponent();
		if (object instanceof Folder){
			controller.sortFoldersByName((Folder) object);
			refreshTreeNode(false);
		}
	}

	private void onCreateFolder(){
		Object object = tree.getLastSelectedPathComponent();
		Folder selectedFolder = null;
		if (object instanceof Folder){
			selectedFolder = (Folder) object;
		} else if (object instanceof Favorite){
			selectedFolder = ((Favorite) object).getFolder();
		}

		if (selectedFolder != null){
			Object[] path = tree.getSelectionPath().getPath();
			Object res = JOptionPane.showInputDialog(tree, UserSettings.getWord("FolderName"), UserSettings
					.getWord("CreateFolder"), JOptionPane.PLAIN_MESSAGE, null, null, UserSettings.getWord("New folder"));
			String value = (String) res;
			if (value != null && !value.trim().isEmpty()){
				Folder folder = controller.createFolder(value.trim(), selectedFolder);
				if (object instanceof Folder){
					path = Arrays.copyOf(path, path.length + 1);
				}
				path[path.length - 1] = folder;
				refreshTreeNode(false);
				tree.setSelectionPath(new TreePath(path));
			}
		}
	}

	private void onDeleteFolder(){
		Object object = tree.getLastSelectedPathComponent();
		Folder selectedFolder = null;
		if (object instanceof Folder){
			selectedFolder = (Folder) object;
		}
		if (selectedFolder != null){
			controller.deleteFolder(selectedFolder);
			refreshTreeNode(true);
		}

	}

	private void onDeleteFavorite(){
		Object object = tree.getLastSelectedPathComponent();
		Favorite selectedFavorite = null;
		if (object instanceof Favorite){
			selectedFavorite = (Favorite) object;
		}
		if (selectedFavorite != null){
			controller.deleteFavorite(selectedFavorite);
		}
		refreshTreeNode(true);
	}

	private void onSetDefault(){
		Object object = tree.getLastSelectedPathComponent();
		Favorite selectedFavorite = null;
		if (object instanceof Favorite){
			selectedFavorite = (Favorite) object;
		}
		if (selectedFavorite != null){
			controller.setDefaultFavorite(selectedFavorite);
		}
	}

	private void refreshTreeNode(boolean refreshParent){
		final FavoritesTreeModel model = (FavoritesTreeModel) tree.getModel();
		Object[] path = tree.getSelectionPath().getPath();
		final Object selectedObject = tree.getLastSelectedPathComponent();
		if (refreshParent || selectedObject instanceof Favorite){
			path = Arrays.copyOf(path, path.length - 1);
		}
		model.fireTreeStructureChanged(new TreeModelEvent(tree, path));
	}
}
