/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.reports;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

public abstract class AbstractTreeModel implements TreeModel{
	protected Vector<TreeModelListener> vector = new Vector<TreeModelListener>();

	@Override
	public void addTreeModelListener(TreeModelListener listener){
		if (listener != null && !vector.contains(listener)){
			vector.addElement(listener);
		}
	}

	@Override
	public void removeTreeModelListener(TreeModelListener listener){
		if (listener != null){
			vector.removeElement(listener);
		}
	}

	public void fireTreeNodesChanged(TreeModelEvent e){
		Enumeration<TreeModelListener> listeners = vector.elements();
		while (listeners.hasMoreElements()){
			TreeModelListener listener = (TreeModelListener) listeners.nextElement();
			listener.treeNodesChanged(e);
		}
	}

	public void fireTreeNodesInserted(TreeModelEvent e){
		Enumeration<TreeModelListener> listeners = vector.elements();
		while (listeners.hasMoreElements()){
			TreeModelListener listener = (TreeModelListener) listeners.nextElement();
			listener.treeNodesInserted(e);
		}
	}

	public void fireTreeNodesRemoved(TreeModelEvent e){
		Enumeration<TreeModelListener> listeners = vector.elements();
		while (listeners.hasMoreElements()){
			TreeModelListener listener = (TreeModelListener) listeners.nextElement();
			listener.treeNodesRemoved(e);
		}
	}

	public void fireTreeStructureChanged(TreeModelEvent e){
		Enumeration<TreeModelListener> listeners = vector.elements();
		while (listeners.hasMoreElements()){
			TreeModelListener listener = (TreeModelListener) listeners.nextElement();
			listener.treeStructureChanged(e);
		}
	}

}
