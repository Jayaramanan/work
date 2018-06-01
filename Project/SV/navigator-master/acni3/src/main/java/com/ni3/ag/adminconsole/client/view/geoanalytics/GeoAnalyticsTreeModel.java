/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.geoanalytics;

import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;


public class GeoAnalyticsTreeModel extends AbstractTreeModel{

	private static final long serialVersionUID = 1L;
	private List<DatabaseInstance> dbNames;
	private final ACRootNode rootNode;
	private Map<DatabaseInstance, List<Schema>> schemasMap;

	public GeoAnalyticsTreeModel(List<DatabaseInstance> dbNames, Map<DatabaseInstance, List<Schema>> map){
		this.dbNames = dbNames;
		rootNode = new ACRootNode();
		schemasMap = map;
	}

	public Object getChild(Object node, int i){
		if (rootNode.equals(node)){
			return dbNames.get(i);
		} else if (node instanceof DatabaseInstance){
			DatabaseInstance db = (DatabaseInstance) node;
			List<Schema> ss = schemasMap.get(db);
			return ss == null ? null : ss.get(i);
		}
		return null;
	}

	public int getChildCount(Object node){
		if (rootNode.equals(node)){
			return dbNames.size();
		} else if (node instanceof DatabaseInstance){
			DatabaseInstance db = (DatabaseInstance) node;
			List<Schema> ss = schemasMap.get(db);
			return ss == null ? 0 : ss.size();
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return dbNames.indexOf(child);
		} else if (parent instanceof DatabaseInstance){
			DatabaseInstance db = (DatabaseInstance) parent;
			List<Schema> ss = schemasMap.get(db);
			return ss == null ? 0 : ss.indexOf(child);
		}
		return -1;
	}

	public Object getRoot(){
		return rootNode;
	}

	public boolean isLeaf(Object node){
		if (rootNode.equals(node))
			return false;
		if (node instanceof DatabaseInstance){
			List<Schema> ss = schemasMap.get(node);
			return ss == null || ss.size() == 0;
		} else if (node instanceof Schema)
			return true;
		return false;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue){
	}

}