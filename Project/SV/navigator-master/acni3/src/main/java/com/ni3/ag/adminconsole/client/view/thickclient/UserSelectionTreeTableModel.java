/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient;

import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.common.treetable.AbstractTreeTableModel;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;

public class UserSelectionTreeTableModel extends AbstractTreeTableModel{

	private List<Group> groups;
	private ACRootNode rootNode = new ACRootNode("");

	public UserSelectionTreeTableModel(List<Group> groups){
		this.groups = groups;
	}

	@Override
	public Object getChild(Object parent, int index){
		if (rootNode.equals(parent)){
			return groups.get(index);
		} else if (parent instanceof Group){
			List<User> users = ((Group) parent).getUsers();
			return users.get(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent){
		if (rootNode.equals(parent)){
			return groups != null ? groups.size() : 0;
		} else if (parent instanceof Group){
			List<User> users = ((Group) parent).getUsers();
			return users != null ? users.size() : 0;
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return groups.indexOf(child);
		} else if (parent instanceof Group){
			List<User> users = ((Group) parent).getUsers();
			return users.indexOf(child);
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
			return groups == null || groups.isEmpty();
		} else if (node instanceof Group){
			List<User> users = ((Group) node).getUsers();
			return users == null || users.isEmpty();
		}
		return true;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue){
	}

}
