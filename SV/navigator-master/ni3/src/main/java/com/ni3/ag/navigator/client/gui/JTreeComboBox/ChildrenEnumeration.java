/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.jtreecombobox;

import java.util.Enumeration;

import javax.swing.tree.TreeModel;

class ChildrenEnumeration implements Enumeration<Object>{
	TreeModel treeModel;
	Object node;
	int index = -1;
	int depth;

	public ChildrenEnumeration(TreeModel treeModel, Object node){
		this.treeModel = treeModel;
		this.node = node;
	}

	public boolean hasMoreElements(){
		return index < treeModel.getChildCount(node) - 1;
	}

	public Object nextElement(){
		return treeModel.getChild(node, ++index);
	}
}
