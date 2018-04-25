/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.languageadmin;

import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.languageadmin.UpdatePropertyButtonListener;
import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class LanguageTreeModel extends AbstractTreeModel{

	private static final long serialVersionUID = 1L;
	private Map<DatabaseInstance, List<Language>> languageMap;
	private List<DatabaseInstance> dbNames;
	private final ACRootNode rootNode;
	private UpdatePropertyButtonListener listener;

	public LanguageTreeModel(Map<DatabaseInstance, List<Language>> schemaMap, List<DatabaseInstance> dbNames){
		this.languageMap = schemaMap;
		this.dbNames = dbNames;
		rootNode = new ACRootNode();
	}

	public Object getChild(Object node, int i){
		if (rootNode.equals(node)){
			return dbNames.get(i);
		} else if (node instanceof DatabaseInstance){
			List<Language> languages = languageMap.get((DatabaseInstance) node);
			return languages.get(i);
		}
		return null;
	}

	public int getChildCount(Object node){
		if (rootNode.equals(node)){
			return dbNames.size();
		} else if (node instanceof DatabaseInstance){
			List<Language> languages = languageMap.get((DatabaseInstance) node);
			return languages != null ? languages.size() : 0;
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return dbNames.indexOf(child);
		} else if (parent instanceof DatabaseInstance){
			List<Language> languages = languageMap.get((DatabaseInstance) parent);
			return languages.indexOf(child);
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
			List<Language> languages = languageMap.get((DatabaseInstance) node);
			return languages == null || languages.isEmpty();
		}
		return true;
	}

	public void setUpdateListener(UpdatePropertyButtonListener listener){
		this.listener = listener;
	}

	public void valueForPathChanged(TreePath path, Object newValue){
		Object obj = path.getLastPathComponent();
		if (obj instanceof Language && newValue instanceof String){
			Language lang = (Language) obj;
			String newVal = ((String) newValue).trim();
			if (listener != null && !newVal.isEmpty() && !newValue.equals(lang.getLanguage())){
				String oldValue = lang.getLanguage();
				lang.setLanguage(newVal);
				if (listener.save()){
					listener.refreshTable();
				} else{
					lang.setLanguage(oldValue);
				}
			}
		}
	}
}
