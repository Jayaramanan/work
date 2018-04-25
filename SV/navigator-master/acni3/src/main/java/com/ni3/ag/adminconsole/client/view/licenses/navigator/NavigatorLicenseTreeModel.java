/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses.navigator;

import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;


public class NavigatorLicenseTreeModel extends AbstractTreeModel{

	private static final long serialVersionUID = 1L;
	private Map<DatabaseInstance, List<Group>> groupMap;
	private List<DatabaseInstance> dbNames;
	private final ACRootNode rootNode;

	public NavigatorLicenseTreeModel(Map<DatabaseInstance, List<Group>> map, List<DatabaseInstance> dbNames){
		this.groupMap = map;
		this.dbNames = dbNames;
		this.rootNode = new ACRootNode();
	}

	@Override
	public Object getChild(Object parent, int index){
		if (rootNode.equals(parent)){
			return dbNames.get(index);
		} else if (parent instanceof DatabaseInstance){
			List<Group> groups = groupMap.get((DatabaseInstance) parent);
			return groups.get(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent){
		if (rootNode.equals(parent)){
			return dbNames.size();
		} else if (parent instanceof DatabaseInstance){
			List<Group> groups = groupMap.get((DatabaseInstance) parent);
			return groups != null ? groups.size() : 0;
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return dbNames.indexOf(child);
		} else if (parent instanceof DatabaseInstance){
			List<Group> groups = groupMap.get((DatabaseInstance) parent);
			return groups.indexOf(child);
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
			List<Group> groups = groupMap.get((DatabaseInstance) node);
			return groups == null || groups.isEmpty();
		}
		return true;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue){
	}

	public void setData(Map<DatabaseInstance, List<Group>> map, List<DatabaseInstance> dbNames){
		this.groupMap = map;
		this.dbNames = dbNames;
	}

}
