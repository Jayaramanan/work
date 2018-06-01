/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;


public class UserAdminTreeModel extends AbstractTreeModel{

	private Map<DatabaseInstance, List<Group>> groupMap = new HashMap<DatabaseInstance, List<Group>>();
	private List<DatabaseInstance> dbNames;
	private final ACRootNode rootNode;
	private List<String> leafs = Arrays.asList(new String[] { Translation.get(TextID.GroupMembers),
	        Translation.get(TextID.GroupPrivileges), Translation.get(TextID.GroupScope),
	        Translation.get(TextID.OfflineClient), Translation.get(TextID.Charts) });

	public UserAdminTreeModel(Map<DatabaseInstance, List<Group>> groupMap, List<DatabaseInstance> dbNames){
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
			return leafs.get(index);
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
			String gName = ((Group) parent).getName();
			if (gName != null && gName.equals(Translation.get(TextID.Unassigned))){
				return 1;
			}
			return leafs.size();
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return dbNames.indexOf(child);
		} else if (parent instanceof DatabaseInstance){
			List<Group> groups = groupMap.get((DatabaseInstance) parent);
			return groups != null ? groups.indexOf(child) : 0;
		} else if (parent instanceof Group){
			return leafs.indexOf(child);
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
