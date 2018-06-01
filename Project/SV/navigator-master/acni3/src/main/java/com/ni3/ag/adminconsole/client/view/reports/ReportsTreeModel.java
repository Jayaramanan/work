/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.reports;

import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.domain.ReportTemplate;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class ReportsTreeModel extends AbstractTreeModel{
	private Map<DatabaseInstance, List<ReportTemplate>> reportMap;
	private List<DatabaseInstance> dbNames;
	private final ACRootNode rootNode;

	public ReportsTreeModel(Map<DatabaseInstance, List<ReportTemplate>> reportMap, List<DatabaseInstance> dbNames){
		this.reportMap = reportMap;
		this.dbNames = dbNames;
		rootNode = new ACRootNode();
	}

	@Override
	public Object getChild(Object parent, int i){
		if (rootNode.equals(parent)){
			return dbNames.get(i);
		} else if (parent instanceof DatabaseInstance){
			List<ReportTemplate> reports = reportMap.get((DatabaseInstance) parent);
			return reports.get(i);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent){
		if (rootNode.equals(parent)){
			return dbNames.size();
		} else if (parent instanceof DatabaseInstance){
			List<ReportTemplate> reports = reportMap.get((DatabaseInstance) parent);
			return reports != null ? reports.size() : 0;
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return dbNames.indexOf(child);
		} else if (parent instanceof DatabaseInstance){
			List<ReportTemplate> reports = reportMap.get((DatabaseInstance) parent);
			return reports.indexOf(child);
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
			return dbNames == null || dbNames.isEmpty();
		} else if (node instanceof DatabaseInstance){
			List<ReportTemplate> reports = reportMap.get((DatabaseInstance) node);
			return reports == null || reports.isEmpty();
		}
		return true;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue){

	}

	public void setReportMap(Map<DatabaseInstance, List<ReportTemplate>> reportMap){
		this.reportMap = reportMap;
	}

}
