/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.shared.domain.FavoriteMode;

public class Favorite implements Transferable, Comparable<Favorite>{

	private static final Logger log = Logger.getLogger(Favorite.class);
	private int id;
	private FavoriteMode mode;
	private int schemaId;
	private int folderId;
	private Folder folder;
	private String name;
	private String description;
	private boolean groupFavorite;
	private int creatorId;
	private boolean oldFavorite;
	private String data;
	private String layout;

	public Favorite(){
		id = 0;
		schemaId = 0;
		folderId = 0;
		folder = null;
		name = null;
		groupFavorite = false;
		creatorId = 0;
	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getSchemaId(){
		return schemaId;
	}

	public void setSchemaId(int schemaId){
		this.schemaId = schemaId;
	}

	public Folder getFolder(){
		return folder;
	}

	public void setFolder(Folder folder){
		this.folder = folder;
		if (folder != null){
			this.folderId = folder.getId();
		}
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public boolean isGroupFavorite(){
		return groupFavorite;
	}

	public void setGroupFavorite(boolean groupFavorite){
		this.groupFavorite = groupFavorite;
	}

	public int getCreatorId(){
		return creatorId;
	}

	public void setCreatorId(int creatorId){
		this.creatorId = creatorId;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public FavoriteMode getMode(){
		return mode;
	}

	public void setMode(FavoriteMode mode){
		this.mode = mode;
	}

	public int getFolderId(){
		return folderId;
	}

	public void setFolderId(int folderId){
		this.folderId = folderId;
	}

	public boolean isOldFavorite(){
		return oldFavorite;
	}

	public void setOldFavorite(boolean oldFavorite){
		this.oldFavorite = oldFavorite;
	}

	public String toString(){
		return name;
	}

	public String getData(){
		return data;
	}

	public void setData(String data){
		this.data = data;
	}

	public String getLayout(){
		return layout;
	}

	public void setLayout(String layout){
		this.layout = layout;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException{
		if ("ni3/favorite".equals(flavor.getHumanPresentableName()))
			return this;

		throw new UnsupportedFlavorException(flavor);
	}

	public DataFlavor[] getTransferDataFlavors(){
		DataFlavor flv[] = new DataFlavor[1];

		try{
			flv[0] = new DataFlavor("ni3/favorite");
			return flv;
		} catch (ClassNotFoundException e){
			log.error("getTransferDataFlavors", e);
		}
		return null;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor){
		return "ni3/favorite".equals(flavor.getHumanPresentableName());
	}

	public String getCaption(){
		switch (mode){
			case FAVORITE:
			case QUERY:
			default:
				return "";

			case TOPIC:
				return UserSettings.getWord("Topic") + " - " + name;
		}
	}

	@Override
	public int compareTo(Favorite o){
		return UserSettings.getCollator().compare(this.name, o.name);
	}

	public ImageIcon getIcon(){
		ImageIcon icon;
		switch (mode){
			case QUERY:
				icon = IconCache.getImageIcon(IconCache.MENU_QUERY);
				break;

			case TOPIC:
				icon = IconCache.getImageIcon(IconCache.MENU_TOPIC);
				break;

			case FAVORITE:
			default:
				icon = IconCache.getImageIcon(IconCache.MENU_FAVORITE);
				break;
		}
		return icon;
	}

}
