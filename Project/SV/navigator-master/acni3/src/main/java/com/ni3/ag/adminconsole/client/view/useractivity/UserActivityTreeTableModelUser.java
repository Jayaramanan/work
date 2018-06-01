/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useractivity;

import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.common.treetable.AbstractTreeTableModel;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserActivity;

public class UserActivityTreeTableModelUser extends AbstractTreeTableModel{

	private List<User> users;
	private ACRootNode rootNode = new ACRootNode("");

	public UserActivityTreeTableModelUser(List<User> users){
		this.users = users;
	}

	@Override
	public Object getChild(Object parent, int index){
		if (rootNode.equals(parent)){
			return users.get(index);
		} else if (parent instanceof User){
			List<UserActivity> activities = ((User) parent).getActivities();
			return activities.get(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent){
		if (rootNode.equals(parent)){
			return users != null ? users.size() : 0;
		} else if (parent instanceof User){
			List<UserActivity> activities = ((User) parent).getActivities();
			return activities != null ? activities.size() : 0;
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return users.indexOf(child);
		} else if (parent instanceof User){
			List<UserActivity> activities = ((User) parent).getActivities();
			return activities.indexOf(child);
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
			return users == null || users.isEmpty();
		} else if (node instanceof User){
			List<UserActivity> activities = ((User) node).getActivities();
			return activities == null || activities.isEmpty();
		}
		return true;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue){
	}
}