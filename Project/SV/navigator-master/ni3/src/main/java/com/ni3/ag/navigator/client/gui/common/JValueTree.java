/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.common;

import java.util.Enumeration;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.tree.*;

import com.ni3.ag.navigator.client.domain.DataFilter;
import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.Value;
import com.ni3.ag.navigator.client.gui.CheckValueIntegrity;
import com.ni3.ag.navigator.client.gui.Models;
import com.ni3.ag.navigator.client.gui.search.CheckNode;
import com.ni3.ag.navigator.client.gui.search.CheckRenderer;

@SuppressWarnings("serial")
public class JValueTree extends JTree{

	public JValueTree(Attribute a, CheckValueIntegrity check, DataFilter filter){
		TreeModel treeModel = Models.getPredefinedCheckTree(a, check, filter,
				false);
		setModel(treeModel);

		setCellRenderer(new CheckRenderer(Color.white));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setShowsRootHandles(true);
		putClientProperty("JTree.lineStyle", "Angled");
		addMouseListener(new NodeSelectionListener(this));

		setRootVisible(false);
		expandValues();
	}

	public void expandValues(){
		int size = getChildCount((DefaultMutableTreeNode) getModel().getRoot());

		for (int i = 0; i < size; i++)
			expandRow(i);
	}

	@SuppressWarnings("unchecked")
	public void clearValues(){
		CheckNode nd;

		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) getModel().getRoot();

		for (Enumeration<CheckNode> en = parent.children(); en.hasMoreElements();){
			nd = en.nextElement();
			setSelected(nd, false, false);
		}

		((DefaultTreeModel) getModel()).nodeChanged(parent);
	}

	@SuppressWarnings("unchecked")
	public int getChildCount(DefaultMutableTreeNode node){
		int ret = 0;
		DefaultMutableTreeNode nd;

		for (Enumeration<DefaultMutableTreeNode> en = node.children(); en.hasMoreElements();){
			nd = en.nextElement();
			ret += getChildCount(nd);
		}

		return ret + node.getChildCount();
	}

	public String getValues(){
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) getModel().getRoot();
		return getValues(parent, "");
	}

	@SuppressWarnings("unchecked")
	String getValues(DefaultMutableTreeNode node, String ret){
		CheckNode nd;

		for (Enumeration<CheckNode> en = node.children(); en.hasMoreElements();){
			nd = en.nextElement();
			if (nd.isSelected()){
				if (ret.length() != 0)
					ret += ",";
				ret += nd.value.getId();
			}

			ret = getValues(nd, ret);
		}

		return ret;
	}

	public Value[] getMultivalue(){
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) getModel().getRoot();

		Value ret[] = new Value[getMultivalueCount(parent)];
		getMultivalue(parent, ret, 0);

		return ret;
	}

	public int getMultivalueCount(){
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) getModel().getRoot();

		return getMultivalueCount(parent);
	}

	@SuppressWarnings("unchecked")
	int getMultivalueCount(DefaultMutableTreeNode node){
		CheckNode nd;

		int count = 0;
		for (Enumeration<CheckNode> en = node.children(); en.hasMoreElements();){
			nd = en.nextElement();
			if (nd.isSelected()){
				count++;
			}

			count += getMultivalueCount(nd);
		}

		return count;
	}

	@SuppressWarnings("unchecked")
	void getMultivalue(DefaultMutableTreeNode node, Value[] ret, int count){
		CheckNode nd;

		for (Enumeration<CheckNode> en = node.children(); en.hasMoreElements();){
			nd = en.nextElement();
			if (nd.isSelected()){
				ret[count] = nd.value;
				count++;
			}

			getMultivalue(nd, ret, count);
		}
	}

	public void setMultivalue(Value[] val){
		if (val != null)
			for (Value v : val)
				setValue(v.getId());
	}

	CheckNode getNode(int valueID){
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) getModel().getRoot();

		return getNode(valueID, parent);
	}

	@SuppressWarnings("unchecked")
	CheckNode getNode(int valueID, DefaultMutableTreeNode parent){
		CheckNode nd, ret;

		for (Enumeration<CheckNode> en = parent.children(); en.hasMoreElements();){
			nd = en.nextElement();
			if (nd.value.getId() == valueID){
				return nd;
			}

			ret = getNode(valueID, nd);
			if (ret != null)
				return ret;
		}

		return null;
	}

	public void setValue(int valueID){
		CheckNode check = getNode(valueID);
		if (check != null){
			setSelected(check, true, true);
		}
	}

	@SuppressWarnings("unchecked")
	void setSelected(CheckNode parent, boolean isSelected, boolean fireChangeSignal){
		CheckNode nd;

		parent.setSelected(isSelected);

		for (Enumeration<CheckNode> en = parent.children(); en.hasMoreElements();){
			nd = en.nextElement();
			setSelected(nd, isSelected, fireChangeSignal);
			if (fireChangeSignal){
				((DefaultTreeModel) getModel()).nodeChanged(nd);
			}
		}

		if (fireChangeSignal){
			((DefaultTreeModel) getModel()).nodeChanged(parent);
		}
	}

	@SuppressWarnings("unchecked")
	public void setParentSelected(CheckNode cn){
		if (cn != null){
			boolean ToSelect, ToDeselect;

			ToSelect = false;
			ToDeselect = true;

			for (Enumeration<CheckNode> e = cn.children(); e.hasMoreElements();){
				CheckNode cc = e.nextElement();
				if (cc.isSelected()){
					ToDeselect = false;
					ToSelect = true;
				}
			}

			if (ToDeselect)
				cn.setSelected(false);

			if (ToSelect)
				cn.setSelected(true);

			if (cn.getParent() instanceof CheckNode)
				setParentSelected((CheckNode) cn.getParent());
		}
	}

	class NodeSelectionListener extends MouseAdapter{
		JValueTree tree;

		NodeSelectionListener(JValueTree tree){
			this.tree = tree;
		}

		public void mouseClicked(MouseEvent e){
			int x = e.getX();
			int y = e.getY();
			int row = tree.getRowForLocation(x, y);

			TreePath path = tree.getPathForRow(row);
			if (path != null){
				Object o = path.getLastPathComponent();
				if (o instanceof CheckNode){
					CheckNode node = (CheckNode) o;
					if (node.value != null && node.isEnabled()){
						boolean isSelected = !(node.isSelected());

						setSelected(node, isSelected, true);
					}
				}
			}
		}
	}
}
