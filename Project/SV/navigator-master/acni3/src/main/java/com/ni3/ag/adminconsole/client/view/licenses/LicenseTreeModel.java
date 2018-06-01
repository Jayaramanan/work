/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses;

import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;


public class LicenseTreeModel extends AbstractTreeModel{

	private static final long serialVersionUID = 1L;
	private Map<DatabaseInstance, List<LicenseData>> licenseMap;
	private List<DatabaseInstance> dbNames;
	private final ACRootNode rootNode;

	public LicenseTreeModel(Map<DatabaseInstance, List<LicenseData>> map, List<DatabaseInstance> dbNames){
		this.licenseMap = map;
		this.dbNames = dbNames;
		rootNode = new ACRootNode();
	}

	public void setLicenseMap(Map<DatabaseInstance, List<LicenseData>> map){
		this.licenseMap = map;
	}

	@Override
	public Object getChild(Object parent, int index){
		if (rootNode.equals(parent)){
			return dbNames.get(index);
		} else if (parent instanceof DatabaseInstance){
			List<LicenseData> licenses = licenseMap.get((DatabaseInstance) parent);
			return licenses.get(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent){
		if (rootNode.equals(parent)){
			return dbNames.size();
		} else if (parent instanceof DatabaseInstance){
			DatabaseInstance db = (DatabaseInstance) parent;
			if (!db.isConnected())
				return 0;
			List<LicenseData> licenses = licenseMap.get((DatabaseInstance) parent);
			return licenses != null ? licenses.size() : 0;
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return dbNames.indexOf(child);
		} else if (parent instanceof DatabaseInstance){
			List<LicenseData> licenses = licenseMap.get((DatabaseInstance) parent);
			return licenses.indexOf(child);
		}
		return -1;
	}

	@Override
	public Object getRoot(){
		return rootNode;
	}

	@Override
	public boolean isLeaf(Object node){
		if (rootNode.equals(node)){
			return dbNames == null || dbNames.isEmpty();
		} else if (node instanceof DatabaseInstance){
			DatabaseInstance db = (DatabaseInstance) node;
			if (!db.isConnected())
				return true;
			List<LicenseData> licenses = licenseMap.get((DatabaseInstance) node);
			return licenses == null || licenses.isEmpty();
		}
		return true;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue){
	}

}
