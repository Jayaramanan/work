/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.Favorite;
import com.ni3.ag.navigator.client.domain.Folder;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.shared.domain.FavoriteMode;

public class FavoritesModel{
	private ArrayList<Favorite> favorites;
	private ArrayList<Folder> folders;

	private Folder myFolder, groupFolder, rootFolder;

	public FavoritesModel(int schemaID){
		this(schemaID, true);
	}

	public FavoritesModel(int SchemaID, boolean withIcons){
		rootFolder = new Folder();
		rootFolder.setId(Folder.ROOT_FOLDER_ID);
		rootFolder.setParentFolderID(-1);
		rootFolder.setName("Root");
		rootFolder.setSchemaID(SchemaID);
		rootFolder.setParentFolder(null);
		rootFolder.setGroupFolder(false);

		groupFolder = new Folder();
		groupFolder.setId(Folder.GROUP_ROOT_FOLDER_ID);
		groupFolder.setParentFolderID(0);
		groupFolder.setName(UserSettings.getWord("Group favorites"));
		groupFolder.setSchemaID(SchemaID);
		groupFolder.setParentFolder(rootFolder);
		groupFolder.setGroupFolder(true);

		myFolder = new Folder();
		myFolder.setId(Folder.MY_ROOT_FOLDER_ID);
		myFolder.setParentFolderID(0);
		myFolder.setName(UserSettings.getWord("My favorites"));
		myFolder.setSchemaID(SchemaID);
		myFolder.setParentFolder(rootFolder);
		myFolder.setGroupFolder(false);

		if (withIcons){
			final ImageIcon folderIcon = IconCache.getImageIcon(IconCache.MENU_FOLDER);
			groupFolder.setIcon(folderIcon);
			myFolder.setIcon(folderIcon);
		}

		folders = new ArrayList<Folder>();

		folders.add(rootFolder);
		folders.add(myFolder);
		folders.add(groupFolder);

		favorites = new ArrayList<Favorite>();
	}

	public List<Favorite> getFavorites(){
		return favorites;
	}

	public List<Folder> getFolders(){
		return folders;
	}

	public Folder getMyFolder(){
		return myFolder;
	}

	public Folder getGroupFolder(){
		return groupFolder;
	}

	public Folder getRootFolder(){
		return rootFolder;
	}

	public void addFavorite(Favorite favorite){
		favorites.add(favorite);
		if (favorite.getFolderId() == 0){
			if (favorite.isGroupFavorite())
				favorite.setFolder(groupFolder);
			else
				favorite.setFolder(myFolder);
		} else{
			favorite.setFolder(getFolderByID(favorite.getFolderId()));
		}
	}

	public void addFolders(List<Folder> fFolders){
		for (Folder folder : fFolders){
			addFolder(folder);
		}
		Collections.sort(folders);

		for (Folder f : folders){
			if (f.getParentFolderID() > 0){
				f.setParentFolder(getFolderByID(f.getParentFolderID()));
			}
		}
	}

	public Folder addFolder(Folder folder){
		folder.setParentFolder(getFolderByID(folder.getParentFolderID()));
		if (folder.getParentFolderID() == 0){
			if (folder.isGroupFolder())
				folder.setParentFolder(groupFolder);
			else
				folder.setParentFolder(myFolder);
		}

		folders.add(folder);
		Collections.sort(folders);

		return folder;
	}

	public void removeFolder(Folder folder){
		folders.remove(folder);
		List<Folder> foldersCopy = new ArrayList<Folder>(folders);
		for (Folder fld : foldersCopy){
			if (fld.getParentFolderID() == folder.getId()){
				removeFolder(fld);
			}
		}

		List<Favorite> favoritesCopy = new ArrayList<Favorite>(favorites);
		for (Favorite fav : favoritesCopy){
			if (fav.getFolderId() == folder.getId()){
				favorites.remove(fav);
			}
		}
	}

	public void removeFavorite(Favorite f){
		favorites.remove(f);
	}

	public Folder getFolderByID(int ID){
		if (ID == -1)
			return null;

		for (Folder f1 : folders){
			if (f1.getId() == ID)
				return f1;
		}

		return null;
	}

	public Favorite getFavoriteByID(int ID){
		if (ID == -1)
			return null;

		for (Favorite f : favorites){
			if (f.getId() == ID)
				return f;
		}

		return null;
	}

	public Favorite getFavoriteByName(String name, int FolderID){
		for (Favorite f : favorites){
			if (f.getFolderId() == FolderID && name.equals(f.getName()))
				return f;
		}

		return null;
	}

	public Favorite getFavorite(int ID){
		for (Favorite ret : favorites)
			if (ret.getId() == ID)
				return ret;

		return null;
	}

	public Favorite createFavorite(String Name, Folder folder, FavoriteMode mode, int schemaId){
		Favorite ret = new Favorite();
		ret.setName(Name);
		ret.setMode(mode);
		ret.setSchemaId(schemaId);
		ret.setGroupFavorite(folder.isGroupFolder());
		ret.setFolder(folder);
		return ret;
	}

	public int getNewSort(int parentFolderId){
		int sort = 0;
		for (Folder folder : folders){
			if (folder.getParentFolderID() == parentFolderId && folder.getSort() > sort){
				sort = folder.getSort();
			}
		}
		return sort + 1;
	}

	public Folder lastChildForFolder(Folder targetFolder, Folder what){
		Folder last = null;
		for (Folder f : folders){
			if (f.getParentFolderID() == targetFolder.getId() && f.getId() != what.getId())
				last = f;
		}
		return last;
	}

	public Folder getByIndex(Folder targetFolder, int newIndex){
		int index = 0;
		for (Folder f : folders){
			if (f.getParentFolderID() == targetFolder.getId())
				if (index == newIndex)
					return f;
				else
					index++;
		}
		return null;
	}

	public int getCurrentIndexOfFolder(Folder src){
		Folder parent = src.getParentFolder();
		int index = 0;
		for (Folder f : folders){
			if (f.getParentFolderID() == parent.getId())
				if (src.getId() == f.getId())
					return index;
				else
					index++;
		}
		return index;
	}

	public boolean isFolderChildOfFolder(Folder f1, Folder f2){
		while (f2.getId() > 0){
			if (f1.getParentFolderID() == f2.getId())
				return true;
			f2 = f1.getParentFolder();
		}
		return false;
	}

	public boolean isUniqueFavoriteName(String name, int currentFavoriteID, int folderID){
		boolean unique = true;
		for (Favorite f : favorites){
			if (f.getFolderId() == folderID && currentFavoriteID != f.getId() && name.equals(f.getName())){
				unique = false;
				break;
			}
		}

		return unique;
	}

	public boolean isUniqueFavoriteFolderName(String name, int currentFolderID, int parentFolderID){
		boolean unique = true;
		for (Folder folder : folders){
			if (folder.getParentFolderID() == parentFolderID && currentFolderID != folder.getId()
					&& name.equals(folder.getName())){
				unique = false;
				break;
			}
		}

		return unique;
	}

	public boolean isFavoriteInFolder(int favoritesID, int folderID){
		boolean result = false;
		final Favorite fav = getFavoriteByID(favoritesID);
		if (fav != null){
			Folder folder = fav.getFolder();
			while (folder != null){
				if (folder.getId() == folderID){
					result = true;
					break;
				}
				folder = folder.getParentFolder();
			}
		}
		return result;
	}

	public Object[] getFavoritesPath(Favorite favorite){
		List<Object> path = new ArrayList<Object>();
		path.add(favorite);
		Folder folder = favorite.getFolder();
		while (folder != null){
			path.add(0, folder);
			folder = folder.getParentFolder();
		}
		return path.toArray();
	}

	public void sortFavorites(){
		Collections.sort(favorites);
	}

	public void sortFolders(){
		Collections.sort(folders);
	}
}
