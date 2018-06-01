/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.ni3.ag.navigator.client.controller.favorites.FavoritesController;
import com.ni3.ag.navigator.client.domain.Schema;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.Favorite;
import com.ni3.ag.navigator.client.domain.Folder;
import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.model.FavoritesModel;

@SuppressWarnings("serial")
public class FavoritesMenu extends JMenu implements ActionListener, Ni3ItemListener{

	private final Ni3Document doc;
	private List<FavoritesMenuItem> items;
	private ButtonGroup buttonGroup;

	public FavoritesMenu(final Ni3Document doc){
		super(UserSettings.getWord("Favorites"));
		items = new ArrayList<FavoritesMenuItem>();

		this.doc = doc;
		doc.registerListener(this);

		createComponents();
	}

	@Override
	public void actionPerformed(final ActionEvent e){
		final String command = e.getActionCommand();
		if ("EditFavorites".equals(command)){
			editFavorites();
		} else if (e.getSource() instanceof FavoritesMenuItem){
			final Favorite favorite = ((FavoritesMenuItem) e.getSource()).getFavorite();
			loadDocument(favorite.getId());
		}

	}

	private void createComponents(){
		refreshFavoritesMenu(doc.DB.schema);
	}

	private void editFavorites(){
		final DlgCreateFavoritesFolder dlg = new DlgCreateFavoritesFolder(doc, false, doc.getCurrentFavorite());
		dlg.setVisible(true);
	}

	@Override
	public void event(final int EventCode, final int SourceID, final Object source, final Object param){
		switch (EventCode){
			case MSG_SchemaChanged:
				onSchemaChanged();
				break;

			case MSG_FavoritesUpdated:
				refreshFavoritesMenu(doc.DB.schema);
				break;

			case MSG_LoadFavorite:
				if (param instanceof Favorite){
					setSelectedFavorite(param);
				}
				break;

			case MSG_ClearFavorite:
				buttonGroup.clearSelection();
				break;
		}
	}

	private void setSelectedFavorite(final Object param){
		buttonGroup.clearSelection();

		FavoritesMenuItem item = getMenuItemByFavoriteId((Favorite) param);
		if (item != null){
			item.setSelected(true);
		}
	}

	@Override
	public int getListenerType(){
		return Ni3ItemListener.SRC_FavoritesMenu;
	}

	private void loadDocument(final int ID){
		doc.dispatchEvent(Ni3ItemListener.MSG_DynamicAttributesCleared, Ni3ItemListener.SRC_Unknown, null, null);
		doc.clearThematicData();

		new FavoritesController(doc).loadDocument(ID, doc.SchemaID);
	}

	public void onSchemaChanged(){
		refreshFavoritesMenu(doc.DB.schema);
	}

	private FavoritesMenuItem getMenuItemByFavoriteId(Favorite favorite){
		FavoritesMenuItem result = null;
		for (FavoritesMenuItem item : items){
			if (item.getFavorite() != null && item.getFavorite().getId() == favorite.getId()){
				result = item;
				break;
			}
		}
		return result;
	}

	public void refreshFavorites(){
		FavoritesController controller = new FavoritesController(doc);
		controller.initFavoritesWithFolders(doc.SchemaID);
		refreshFavoritesMenu(doc.DB.schema);
	}

	public void refreshFavoritesMenu(final Schema schema){
		FavoritesModel favorites = doc.getFavoritesModel();

		removeAll();
		items.clear();
		buttonGroup = new ButtonGroup();

		JMenuItem mitem = new JMenuItem(UserSettings.getWord("Organize favorites"));
		mitem.addActionListener(this);
		mitem.setActionCommand("EditFavorites");
		mitem.setName("EditFavorites");
		add(mitem);

		addSeparator();

		favorites.sortFavorites();
		favorites.sortFolders();

		JMenu myMenu = addFolder(favorites.getMyFolder(), favorites.getFolders(), favorites.getFavorites());
		add(myMenu);
		JMenu groupMenu = addFolder(favorites.getGroupFolder(), favorites.getFolders(), favorites.getFavorites());
		add(groupMenu);

		favorites.sortFavorites();
		favorites.sortFolders();

		if (doc.getCurrentFavorite() != null){
			setSelectedFavorite(doc.getCurrentFavorite());
		}
	}

	private JMenu addFolder(Folder folder, List<Folder> folders, List<Favorite> favorites){
		JMenu menu = new JMenu(folder.getName());
		menu.setIcon(folder.getIcon());
		for (Folder f : folders){
			if (folder.getId() == f.getParentFolderID()){
				menu.add(addFolder(f, folders, favorites));
			}
		}

		for (Favorite fav : favorites){
			if (folder.getId() == fav.getFolderId()){
				FavoritesMenuItem menuItem = new FavoritesMenuItem(fav);
				menuItem.addActionListener(this);
				menu.add(menuItem);
				items.add(menuItem);
				buttonGroup.add(menuItem);

				final String description = fav.getDescription();
				if (description != null && !description.isEmpty()){
					menuItem.setToolTipText(description);
				}
			}
		}
		return menu;
	}
}
