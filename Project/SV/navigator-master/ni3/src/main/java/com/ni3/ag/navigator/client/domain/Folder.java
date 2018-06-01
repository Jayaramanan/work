/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.Icon;

public class Folder implements Transferable, Comparable<Folder>{

	public static final int ROOT_FOLDER_ID = 0;
	public static final int MY_ROOT_FOLDER_ID = -3;
	public static final int GROUP_ROOT_FOLDER_ID = -2;
	private int id;
	private int schemaID;

	private String name;

	private int parentFolderID;
	private Folder parentFolder;
	private boolean groupFolder;

	private int sort;
	private Icon icon;

	public Folder(){
		id = 0;
		schemaID = 0;
		name = null;
		parentFolder = null;
		parentFolderID = 0;
	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getSchemaID(){
		return schemaID;
	}

	public void setSchemaID(int schemaID){
		this.schemaID = schemaID;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public int getParentFolderID(){
		return parentFolderID;
	}

	public void setParentFolderID(int parentFolderID){
		this.parentFolderID = parentFolderID;
	}

	public Folder getParentFolder(){
		return parentFolder;
	}

	public void setParentFolder(Folder parentFolder){
		this.parentFolder = parentFolder;
		if (parentFolder != null){
			this.parentFolderID = parentFolder.getId();
		}
	}

	public boolean isGroupFolder(){
		return groupFolder;
	}

	public void setGroupFolder(boolean groupFolder){
		this.groupFolder = groupFolder;
	}

	public int getSort(){
		return sort;
	}

	public void setSort(int sort){
		this.sort = sort;
	}

	public Icon getIcon(){
		return icon;
	}

	public void setIcon(Icon icon){
		this.icon = icon;
	}

	@Override
	public String toString(){
		return name;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException{
		if ("ni3/folder".equals(flavor.getHumanPresentableName()))
			return this;

		throw new UnsupportedFlavorException(flavor);
	}

	public DataFlavor[] getTransferDataFlavors(){
		DataFlavor flv[] = new DataFlavor[1];

		try{
			flv[0] = new DataFlavor("ni3/folder");
			return flv;
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		return null;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor){
		if ("ni3/folder".equals(flavor.getHumanPresentableName()))
			return true;
		return false;
	}

	@Override
	public int compareTo(Folder o){
		return sort - o.sort;
	}
}
