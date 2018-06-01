/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.etl;

import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;


public class ETLTreeModel extends AbstractTreeModel{

	private static final long serialVersionUID = 1L;
	private List<DatabaseInstance> dbNames;
	private final ACRootNode rootNode;

	public ETLTreeModel(List<DatabaseInstance> dbNames){
		this.dbNames = dbNames;
		rootNode = new ACRootNode();
	}

	public Object getChild(Object node, int i){
		if (rootNode.equals(node)){
			return dbNames.get(i);
		}
		return null;
	}

	public int getChildCount(Object node){
		if (rootNode.equals(node)){
			return dbNames.size();
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return dbNames.indexOf(child);
		}
		return -1;
	}

	public Object getRoot(){
		return rootNode;
	}

	public boolean isLeaf(Object node){
		if (rootNode.equals(node))
			return false;
		return false;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue){
	}

}