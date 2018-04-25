/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin.privileges;

import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.common.treetable.AbstractTreeTableModel;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;

public class GroupPrivilegesTreeTableModel extends AbstractTreeTableModel{

	private List<Schema> schemas;
	private ACRootNode rootNode = new ACRootNode("");

	public GroupPrivilegesTreeTableModel(List<Schema> schemas){
		this.schemas = schemas;
	}

	@Override
	public Object getChild(Object parent, int index){
		if (rootNode.equals(parent)){
			return schemas.get(index);
		} else if (parent instanceof Schema){
			List<ObjectDefinition> objects = ((Schema) parent).getObjectDefinitions();
			return objects.get(index);
		} else if (parent instanceof ObjectDefinition){
			List<ObjectAttribute> attributes = ((ObjectDefinition) parent).getObjectAttributes();
			return attributes.get(index);
		} else if (parent instanceof ObjectAttribute){
			List<PredefinedAttribute> pAttributes = ((ObjectAttribute) parent).getPredefinedAttributes();
			return pAttributes.get(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent){
		if (rootNode.equals(parent)){
			return schemas != null ? schemas.size() : 0;
		} else if (parent instanceof Schema){
			List<ObjectDefinition> objects = ((Schema) parent).getObjectDefinitions();
			return objects != null ? objects.size() : 0;
		} else if (parent instanceof ObjectDefinition){
			List<ObjectAttribute> attributes = ((ObjectDefinition) parent).getObjectAttributes();
			return attributes != null ? attributes.size() : 0;
		} else if (parent instanceof ObjectAttribute){
			List<PredefinedAttribute> pAttributes = ((ObjectAttribute) parent).getPredefinedAttributes();
			return pAttributes != null ? pAttributes.size() : 0;
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return schemas.indexOf(child);
		} else if (parent instanceof Schema){
			List<ObjectDefinition> objects = ((Schema) parent).getObjectDefinitions();
			return objects.indexOf(child);
		} else if (parent instanceof ObjectDefinition){
			List<ObjectAttribute> attributes = ((ObjectDefinition) parent).getObjectAttributes();
			return attributes.indexOf(child);
		} else if (parent instanceof ObjectAttribute){
			List<PredefinedAttribute> pAttributes = ((ObjectAttribute) parent).getPredefinedAttributes();
			return pAttributes.indexOf(child);
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
			return schemas == null || schemas.isEmpty();
		} else if (node instanceof Schema){
			List<ObjectDefinition> objects = ((Schema) node).getObjectDefinitions();
			return objects == null || objects.isEmpty();
		} else if (node instanceof ObjectDefinition){
			List<ObjectAttribute> attributes = ((ObjectDefinition) node).getObjectAttributes();
			return attributes == null || attributes.isEmpty();
		} else if (node instanceof ObjectAttribute){
			List<PredefinedAttribute> pAttributes = ((ObjectAttribute) node).getPredefinedAttributes();
			return pAttributes == null || pAttributes.isEmpty();
		}
		return true;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue){
	}

	public void setSchemas(List<Schema> schemas){
		this.schemas = schemas;
		super.fireTreeStructureChanged(this, new Object[] { rootNode }, new int[0], new Object[] { rootNode });
//		super.fireTreeNodesChanged(this, new Object[] { rootNode }, new int[0], new Object[] { rootNode });
	}

	public List<Schema> getSchemas(){
		return schemas;
	}

}
