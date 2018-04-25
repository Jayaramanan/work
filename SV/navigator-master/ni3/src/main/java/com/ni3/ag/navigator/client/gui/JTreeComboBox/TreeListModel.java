/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.jtreecombobox;

import java.util.Enumeration;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

@SuppressWarnings({ "unchecked", "serial" })
public class TreeListModel extends AbstractListModel implements ComboBoxModel{
	private TreeModel treeModel;
	private Object selectedObject;

	public TreeListModel(TreeModel treeModel){
		this.treeModel = treeModel;
	}

	public int getSize(){
		int count = 0;
		Enumeration enumer = new PreorderEnumeration(treeModel);
		while (enumer.hasMoreElements()){
			enumer.nextElement();
			count++;
		}
		return count;
	}

	public Object getElementAt(int index){
		Enumeration enumer = new PreorderEnumeration(treeModel);
		for (int i = 0; i < index; i++)
			enumer.nextElement();
		return enumer.nextElement();
	}

	public void setSelectedItem(Object anObject){
		if ((selectedObject != null && !selectedObject.equals(anObject)) || selectedObject == null && anObject != null){
			if (anObject instanceof DefaultMutableTreeNode){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) anObject;
				if (!node.isLeaf())
					return;
			} else{
				DefaultMutableTreeNode node;
				Enumeration enumer = new PreorderEnumeration(treeModel);
				while (enumer.hasMoreElements()){
					node = (DefaultMutableTreeNode) enumer.nextElement();
					if (node.getUserObject() == anObject){
						anObject = node;
						break;
					}
				}
			}

			if (!(anObject instanceof DefaultMutableTreeNode))
				return;

			selectedObject = anObject;
			fireContentsChanged(this, -1, -1);
		}
	}

	public Object getSelectedItem(){
		return selectedObject;
	}
}
