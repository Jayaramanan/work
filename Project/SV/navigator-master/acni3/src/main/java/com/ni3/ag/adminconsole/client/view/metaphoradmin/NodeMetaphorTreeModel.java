/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.metaphoradmin;

import java.util.List;
import java.util.Map;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;


public class NodeMetaphorTreeModel extends AbstractTreeModel{

	private static final long serialVersionUID = 1L;
	private Map<DatabaseInstance, List<Schema>> schemaMap;
	private List<DatabaseInstance> dbNames;
	private final ACRootNode rootNode;

	public NodeMetaphorTreeModel(Map<DatabaseInstance, List<Schema>> schemaMap, List<DatabaseInstance> dbNames){
		this.schemaMap = schemaMap;
		this.dbNames = dbNames;
		rootNode = new ACRootNode();
	}

	public Object getChild(Object node, int i){
		if (rootNode.equals(node)){
			return dbNames.get(i);
		} else if (node instanceof DatabaseInstance){
			List<Schema> schemas = schemaMap.get((DatabaseInstance) node);
			return schemas.get(i);
		} else if (node instanceof Schema){
			Schema schema = (Schema) node;
			List<ObjectDefinition> objects = schema.getObjectDefinitions();
			return objects.get(i);
		}
		return null;
	}

	public int getChildCount(Object node){
		if (rootNode.equals(node)){
			return dbNames.size();
		} else if (node instanceof DatabaseInstance){
			List<Schema> schemas = schemaMap.get((DatabaseInstance) node);
			return schemas != null ? schemas.size() : 0;
		} else if (node instanceof Schema){
			Schema schema = (Schema) node;
			List<ObjectDefinition> objects = schema.getObjectDefinitions();
			return objects != null ? objects.size() : 0;
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return dbNames.indexOf(child);
		} else if (parent instanceof DatabaseInstance){
			List<Schema> schemas = schemaMap.get(parent);
			return schemas.indexOf(child);
		} else if (parent instanceof Schema){
			Schema schema = (Schema) parent;
			List<ObjectDefinition> objects = schema.getObjectDefinitions();
			return objects.indexOf(child);
		}
		return -1;
	}

	public Object getRoot(){
		return rootNode;
	}

	public boolean isLeaf(Object node){
		if (rootNode.equals(node)){
			return false;
		} else if (node instanceof DatabaseInstance){
			return false;
		} else if (node instanceof Schema){
			return false;
		}
		return true;
	}

	public void valueForPathChanged(TreePath path, Object newValue){
	}

	public void setData(Map<DatabaseInstance, List<Schema>> schemaMap, List<DatabaseInstance> dbNames){
		this.schemaMap = schemaMap;
		this.dbNames = dbNames;
	}

}
