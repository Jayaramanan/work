/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useractivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.common.treetable.AbstractTreeTableModel;
import com.ni3.ag.adminconsole.domain.UserActivity;
import com.ni3.ag.adminconsole.domain.UserActivityType;

public class UserActivityTreeTableModelActivity extends AbstractTreeTableModel{

	private ACRootNode rootNode = new ACRootNode("");
	private Map<UserActivityType, List<UserActivity>> activities;
	private List<UserActivityType> types;

	public UserActivityTreeTableModelActivity(Map<UserActivityType, List<UserActivity>> map){
		this.activities = map;
		types = new ArrayList<UserActivityType>();
		types.addAll(map.keySet());
	}

	@Override
	public Object getRoot(){
		return rootNode;
	}

	@Override
	public Object getChild(Object parent, int index){
		if (rootNode.equals(parent)){
			return types.get(index);
		} else if (parent instanceof UserActivityType){
			return activities.get(parent).get(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent){
		if (rootNode.equals(parent)){
			return types != null ? types.size() : 0;
		} else if (parent instanceof UserActivityType){
			List<UserActivity> acts = activities.get(parent);
			return acts != null ? acts.size() : 0;
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return types.indexOf(child);
		} else if (parent instanceof UserActivityType){
			List<UserActivity> acts = activities.get(parent);
			if (acts != null)
				return acts.indexOf(child);
		}
		return -1;
	}

	@Override
	public boolean isLeaf(Object node){
		if (rootNode.equals(node)){
			return types == null || types.isEmpty();
		} else if (node instanceof UserActivityType){
			List<UserActivity> acts = activities.get(node);
			return acts == null || acts.isEmpty();
		}
		return true;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue){
	}
}
