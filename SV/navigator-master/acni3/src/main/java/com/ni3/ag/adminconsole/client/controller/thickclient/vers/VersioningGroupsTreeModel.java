/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import java.util.List;
import java.util.Map;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class VersioningGroupsTreeModel implements TreeModel{
	private ACRootNode root;
	private List<DatabaseInstance> dbInstances;
	private Map<DatabaseInstance, List<Group>> groupMap;

	public VersioningGroupsTreeModel(Map<DatabaseInstance, List<Group>> groupMap, List<DatabaseInstance> dbInstances){
		root = new ACRootNode();
		this.dbInstances = dbInstances;
		this.groupMap = groupMap;
	}

	@Override
	public Object getRoot(){
		return root;
	}

	@Override
	public Object getChild(Object parent, int index){
		if (parent == root)
			return dbInstances.get(index);
		if (parent instanceof DatabaseInstance){
			List<Group> groups = groupMap.get(parent);
			return groups != null ? groups.get(index) : null;
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent){
		if (parent == root)
			return dbInstances.size();
		else if (parent instanceof DatabaseInstance){
			List<Group> grs = groupMap.get(parent);
			return grs != null ? grs.size() : 0;
		}
		return 0;
	}

	@Override
	public boolean isLeaf(Object node){
		if (node == root && dbInstances.size() == 0)
			return true;
		else if (node instanceof DatabaseInstance){
			List<Group> grs = groupMap.get(node);
			return grs == null || grs.size() == 0;
		} else if (node instanceof Group)
			return true;
		return false;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue){
	}

	@Override
	public int getIndexOfChild(Object parent, Object child){
		if (parent == root)
			return dbInstances.indexOf(child);
		else if (parent instanceof DatabaseInstance){
			List<Group> grs = groupMap.get(parent);
			return grs != null ? grs.indexOf(child) : null;
		}
		return 0;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l){
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l){
	}

}
