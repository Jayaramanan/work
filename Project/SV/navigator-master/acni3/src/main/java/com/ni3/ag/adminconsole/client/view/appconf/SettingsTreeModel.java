/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.domain.Setting;

public class SettingsTreeModel extends AbstractTreeModel{

	private Map<Setting, List<Setting>> settingMap;
	private List<Setting> settingClasses;
	private final ACRootNode rootNode;
	private boolean chagned;

	public SettingsTreeModel(Map<Setting, List<Setting>> settingMap, List<Setting> settingClasses){
		this.settingMap = settingMap;
		this.settingClasses = settingClasses;
		rootNode = new ACRootNode();
	}

	public List<Setting> getAllSettings(){
		List<Setting> all = new ArrayList<Setting>();
		for (Setting us : settingClasses){
			List<Setting> list = settingMap.get(us);
			all.addAll(list);
		}
		return all;
	}

	public Object getChild(Object parent, int index){
		if (rootNode.equals(parent)){
			return settingClasses.get(index);
		} else if (parent instanceof Setting){
			return settingMap.get(parent).get(index);
		}
		return null;
	}

	public int getChildCount(Object parent){
		if (rootNode.equals(parent)){
			return settingClasses.size();
		} else if (parent instanceof Setting){
			List<Setting> settings = settingMap.get(parent);
			return settings != null ? settings.size() : 0;
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return settingClasses.indexOf(child);
		} else if (parent instanceof Setting){
			List<Setting> settings = settingMap.get(parent);
			return settings.indexOf(child);
		}
		return 0;
	}

	public boolean areAllChildrenSelected(Object parent){
		boolean selected = true;
		if (parent instanceof Setting){
			List<Setting> settings = settingMap.get(parent);
			if (settings != null)
				for (Setting gs : settings)
					if (!"1".equals(gs.getValue()) && !"true".equalsIgnoreCase(gs.getValue())){
						selected = false;
						break;
					}
		}
		return selected;
	}

	public Object getRoot(){
		return rootNode;
	}

	public boolean isLeaf(Object node){
		if (rootNode.equals(node)){
			return settingClasses == null || settingClasses.isEmpty();
		} else if (node instanceof Setting){
			List<Setting> settings = settingMap.get(node);
			return settings == null || settings.isEmpty();
		}
		return true;
	}

	public void valueForPathChanged(TreePath path, Object newValue){
		Object pathComp = path.getLastPathComponent();
		if (pathComp instanceof Setting){
			Setting uset = (Setting) path.getLastPathComponent();
			uset.setValue(String.valueOf(newValue).toUpperCase());
			List<Setting> children = settingMap.get(uset);
			if (children != null && !children.isEmpty()){
				for (Setting gs : children)
					gs.setValue(String.valueOf(newValue).toUpperCase());
			} else{
				TreePath parent = path.getParentPath();
				checkParentState(parent);
			}
			chagned = true;
		}
		fireTreeNodesChanged(new TreeModelEvent(this, path));
	}

	private void checkParentState(TreePath parent){
		if (!(parent.getLastPathComponent() instanceof Setting)){
			return;
		}
		Setting category = (Setting) parent.getLastPathComponent();
		List<Setting> children = settingMap.get(category);
		boolean selected = false;
		if (children != null){
			for (Setting child : children){
				if ("1".equals(child.getValue()) || "true".equalsIgnoreCase(child.getValue())){
					selected = true;
					break;
				}
			}
		}
		category.setValue(String.valueOf(selected).toUpperCase());
		fireTreeNodesChanged(new TreeModelEvent(this, parent));
	}

	public boolean isChagned(){
		return chagned;
	}

}
