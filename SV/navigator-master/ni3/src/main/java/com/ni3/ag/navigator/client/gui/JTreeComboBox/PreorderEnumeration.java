/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.jtreecombobox;

import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import javax.swing.tree.TreeModel;

@SuppressWarnings("unchecked")
class PreorderEnumeration implements Enumeration<Object>{
	private TreeModel treeModel;
	protected Stack stack;
	private int depth = 0;

	public PreorderEnumeration(TreeModel treeModel){
		this.treeModel = treeModel;
		Vector v = new Vector(1);
		v.addElement(treeModel.getRoot());
		stack = new Stack();
		stack.push(v.elements());
	}

	public boolean hasMoreElements(){
		return (!stack.empty() && ((Enumeration) stack.peek()).hasMoreElements());
	}

	public Object nextElement(){
		Enumeration enumer = (Enumeration) stack.peek();
		Object node = enumer.nextElement();
		depth = enumer instanceof ChildrenEnumeration ? ((ChildrenEnumeration) enumer).depth : 0;
		if (!enumer.hasMoreElements())
			stack.pop();
		ChildrenEnumeration children = new ChildrenEnumeration(treeModel, node);
		children.depth = depth + 1;
		if (children.hasMoreElements()){
			stack.push(children);
		}
		return node;
	}

	public int getDepth(){
		return depth;
	}
}
