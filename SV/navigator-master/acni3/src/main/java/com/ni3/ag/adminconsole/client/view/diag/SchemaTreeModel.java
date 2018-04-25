/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.diag;

import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class SchemaTreeModel extends AbstractTreeModel{
	private ACRootNode rootNode;
	private List<DatabaseInstance> instances;
	private Map<DatabaseInstance, List<Schema>> schemasMap;

	public SchemaTreeModel(List<DatabaseInstance> databases, Map<DatabaseInstance, List<Schema>> map){
		rootNode = new ACRootNode();
		this.instances = databases;
		this.schemasMap = map;
	}

	@Override
	public Object getRoot(){
		return rootNode;
	}

	@Override
	public Object getChild(Object parent, int index){
		if (rootNode.equals(parent))
			return instances.get(index);
		else if (parent instanceof DatabaseInstance){
			DatabaseInstance db = (DatabaseInstance) parent;
			if (!db.isConnected())
				return false;
			List<Schema> schemas = schemasMap.get(db);
			return schemas.get(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent){
		if (rootNode.equals(parent)){
			return instances.size();
		} else if (parent instanceof DatabaseInstance){
			DatabaseInstance db = (DatabaseInstance) parent;
			if (!db.isConnected())
				return 0;
			List<Schema> schs = schemasMap.get(db);
			return schs.size();
		} else
			return 0;
	}

	@Override
	public boolean isLeaf(Object node){
		if (node instanceof Schema)
			return true;
		if (node instanceof DatabaseInstance){
			DatabaseInstance db = (DatabaseInstance) node;
			if (db.isConnected()){
				List<Schema> schs = schemasMap.get(db);
				return schs.isEmpty();
			} else
				return true;
		}
		return false;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue){
	}

	@Override
	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent))
			return instances.indexOf(child);
		else if (parent instanceof DatabaseInstance){
			DatabaseInstance db = (DatabaseInstance) parent;
			if (!db.isConnected())
				return -1;
			List<Schema> schemas = schemasMap.get(parent);
			return schemas.indexOf(child);
		}
		return 0;
	}
}
