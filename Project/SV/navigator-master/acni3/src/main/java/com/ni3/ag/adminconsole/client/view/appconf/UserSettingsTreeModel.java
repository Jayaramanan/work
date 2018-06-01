/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;


public class UserSettingsTreeModel extends AbstractTreeModel{

	private Map<DatabaseInstance, List<Group>> groupMap = new HashMap<DatabaseInstance, List<Group>>();
	private List<DatabaseInstance> dbNames;
	private final ACRootNode rootNode;

	public UserSettingsTreeModel(Map<DatabaseInstance, List<Group>> groupMap, List<DatabaseInstance> dbNames){
		this.groupMap = groupMap;
		this.dbNames = dbNames;
		rootNode = new ACRootNode();
	}

	public Object getChild(Object parent, int index){
		if (rootNode.equals(parent)){
			return dbNames.get(index);
		} else if (parent instanceof DatabaseInstance){
			List<Group> groups = groupMap.get((DatabaseInstance) parent);
			return groups.get(index);
		} else if (parent instanceof Group){
			List<User> users = ((Group) parent).getUsers();
			return users.get(index);
		}
		return null;
	}

	public int getChildCount(Object parent){
		if (rootNode.equals(parent)){
			return dbNames.size();
		} else if (parent instanceof DatabaseInstance){
			List<Group> groups = groupMap.get((DatabaseInstance) parent);
			return groups != null ? groups.size() : 0;
		} else if (parent instanceof Group){
			List<User> users = ((Group) parent).getUsers();
			return users.size();
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return dbNames.indexOf(child);
		} else if (parent instanceof DatabaseInstance){
			List<Group> groups = groupMap.get((DatabaseInstance) parent);
			return groups.indexOf(child);
		} else if (parent instanceof Group){
			List<User> users = ((Group) parent).getUsers();
			return users.indexOf(child);
		}
		return 0;
	}

	public Object getRoot(){
		return rootNode;
	}

	public boolean isLeaf(Object node){
		if (rootNode.equals(node)){
			return dbNames == null || dbNames.isEmpty();
		} else if (node instanceof DatabaseInstance){
			List<Group> groups = groupMap.get((DatabaseInstance) node);
			return groups == null || groups.isEmpty();
		} else if (node instanceof Group){
			return false;
		}
		return true;
	}

	public void valueForPathChanged(TreePath path, Object newValue){
	}
}
