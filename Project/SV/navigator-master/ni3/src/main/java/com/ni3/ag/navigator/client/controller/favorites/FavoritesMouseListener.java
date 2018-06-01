/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller.favorites;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.Favorite;
import com.ni3.ag.navigator.client.domain.Folder;
import com.ni3.ag.navigator.client.gui.favorites.FavoritesTree;

public class FavoritesMouseListener extends MouseAdapter{
	private FavoritesTree tree;
	private ActionListener favoritesActionListener;

	public FavoritesMouseListener(FavoritesTree tree, ActionListener l){
		this.tree = tree;
		this.favoritesActionListener = l;
	}

	public void mouseClicked(MouseEvent e){
		if (e.getButton() == MouseEvent.BUTTON3){
			TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
			if (path != null){
				Object obj = path.getLastPathComponent();

				tree.setSelectionPath(path);
				JPopupMenu popup = null;
				if (obj instanceof Folder){
					Folder selectedFolder = (com.ni3.ag.navigator.client.domain.Folder) obj;
					if (selectedFolder.getId() != 0){
						popup = getFolderPopupMenu(selectedFolder);
					}
				} else if (obj instanceof Favorite){
					popup = getFavoritePopupMenu();
				}
				if (popup != null){
					popup.show(tree, e.getX(), e.getY());
				}
			}
		}
	}

	private JPopupMenu getFavoritePopupMenu(){
		JPopupMenu popup = new JPopupMenu();
		JMenuItem item = new JMenuItem(UserSettings.getWord("Delete favorite"));
		item.setActionCommand("DeleteFavorite");
		item.addActionListener(favoritesActionListener);
		popup.add(item);

		item = new JMenuItem(UserSettings.getWord("Set as default"));
		item.setActionCommand("SetAsDefault");
		item.addActionListener(favoritesActionListener);
		popup.add(item);
		return popup;
	}

	private JPopupMenu getFolderPopupMenu(Folder folder){
		JPopupMenu popup = new JPopupMenu();

		JMenuItem item = new JMenuItem(UserSettings.getWord("Create folder"));
		item.setActionCommand("CreateFolder");
		item.addActionListener(favoritesActionListener);
		popup.add(item);

		item = new JMenuItem(UserSettings.getWord("Sort folders by name"));
		item.setActionCommand("SortFoldersByName");
		item.addActionListener(favoritesActionListener);
		popup.add(item);

		if (folder.getId() > 0){
			item = new JMenuItem(UserSettings.getWord("Delete folder"));
			item.setActionCommand("DeleteFolder");
			item.addActionListener(favoritesActionListener);
			popup.add(item);
		}
		return popup;
	}
}
