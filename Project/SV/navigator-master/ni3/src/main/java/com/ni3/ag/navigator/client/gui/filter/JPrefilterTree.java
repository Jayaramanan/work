/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.filter;

import java.util.Enumeration;
import java.util.Vector;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.model.Ni3Document;

@SuppressWarnings({"unchecked", "serial"})
public class JPrefilterTree extends JTree{

	private CheckNode root;

	public Vector<CheckNode> allCheckNodesVector = new Vector<CheckNode>();
	private Vector<CheckNode> allCheckNodesSecond = new Vector<CheckNode>();

	private Ni3Document doc;
	public static Color BackgroundColor;

	public DataFilter SYSGroupPrefilter;

	public JPrefilterTree(Ni3Document doc, DataFilter SYSGroupPrefilter){
		super(new CheckNode("Root", null, true, null));

		this.SYSGroupPrefilter = SYSGroupPrefilter;
		this.doc = doc;

		root = (CheckNode) getModel().getRoot();

		createTree();
	}

	public int getListenerType(){
		return Ni3ItemListener.SRC_PreFilterTree;
	}

	void createChildren(CheckNode parentNode, int ParentID){
		CheckNode node;

		for (Entity def : doc.DB.schema.definitions){
			if (def.CanRead)
				for (Attribute a : def.getAttributesSortedForFilter()){
					if (!a.inContext || doc.isCurrentTopic()){
						if (a.getValuesToUse() != null){
							for (Value v : a.getValuesToUse()){
								if (v.getParentId() == ParentID){
									if (!SYSGroupPrefilter.checkExclusion(v.getId())){
										node = new CheckNode(v.getLabel(), v, true, null);
										allCheckNodesVector.addElement(node);

										parentNode.add(node);
										createChildren(node, v.getId());
									}
								}
							}
						}
					}
				}
		}
	}

	void createTree(){
		CheckNode checkNodeFirstLevel;
		CheckNode checkNodeSecondLevel;
		CheckNode checkNodeThirdLevel;

		root.removeAllChildren();
		allCheckNodesVector.clear();

		// Node types
		for (Entity def : doc.DB.schema.definitions){
			if (!def.CanRead)
				continue;

			checkNodeFirstLevel = null;

			for (Attribute a : def.getAttributesSortedForFilter()){
				if (a.inPrefilter && (!a.inContext || doc.isCurrentTopic())){
					if (checkNodeFirstLevel == null){
						checkNodeFirstLevel = new CheckNode(def.Name, null, true, def);
						root.add(checkNodeFirstLevel);
					}

					checkNodeSecondLevel = new CheckNode(a.label, new Value(-a.ID, 0, a.name, a.label), true, null);
					allCheckNodesVector.addElement(checkNodeSecondLevel);
					allCheckNodesSecond.addElement(checkNodeSecondLevel);
					checkNodeFirstLevel.add(checkNodeSecondLevel);

					if (a.getValuesToUse() != null)
						for (Value v : a.getValuesToUse()){
							if (v.getParentId() == 0 && !doc.SYSGroupPrefilter.checkExclusion(v.getId())){
								checkNodeThirdLevel = new CheckNode(v.getLabel(), v, true, null);
								allCheckNodesVector.addElement(checkNodeThirdLevel);

								checkNodeSecondLevel.add(checkNodeThirdLevel);
								createChildren(checkNodeThirdLevel, v.getId());
							}
						}
				}
			}
		}

		DefaultTreeModel model = (DefaultTreeModel) getModel();
		model.reload();
	}

	public void setTree(){
		setRootVisible(true);
		setShowsRootHandles(true);
		expandRow(0);
		setBackground(BackgroundColor);
		setRootVisible(false);

		setCellRenderer(new CheckRenderer(BackgroundColor));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		putClientProperty("JTree.lineStyle", "Angled");
		addMouseListener(new NodeSelectionListener(this));
	}

	public void setParentSelected(CheckNode cn){
		if (cn != null){
			boolean ToSelect, ToDeselect;

			ToSelect = false;
			ToDeselect = true;

			for (Enumeration e = cn.children(); e.hasMoreElements(); ){
				CheckNode cc = (CheckNode) e.nextElement();
				if (cc.isSelected()){
					ToDeselect = false;
					ToSelect = true;
				}
			}

			if (ToDeselect)
				cn.setSelected(false);

			if (ToSelect)
				cn.setSelected(true);

			setParentSelected((CheckNode) cn.getParent());
		}
	}

	public void restoreFilter(DataFilter filter){
		for (CheckNode cn : allCheckNodesVector){
			cn.setSelected(true);

			if (cn.getPredefinedValue() != null && filter.filter.containsKey(cn.getPredefinedValue().getId())){
				cn.setSelected(false);
			}
		}

		CheckNode cn3;

		for (CheckNode cn : allCheckNodesSecond){
			boolean hasSelected = false;
			Enumeration enu = cn.children();
			while (enu.hasMoreElements()){
				cn3 = (CheckNode) enu.nextElement();
				if (cn3.isSelected()){
					hasSelected = true;
					break;
				}
			}

			cn.setSelected(hasSelected);
		}

		for (Integer i : filter.expanded){
			expandRow(i);
		}
	}

	public DataFilter createFilter(DataFilter filter){
		filter.copyFilter(doc.SYSGroupPrefilter);
		for (CheckNode cn : allCheckNodesVector){
			if (cn.getPredefinedValue() != null && !cn.isSelected()){
				// If is not selected, put values in DataFilter
				filter.addExclusion(cn.getPredefinedValue());
			}
		}

		for (int nn = 0; nn < getRowCount(); nn++){
			if (isExpanded(nn)){
				filter.addExpansion(nn);
			}

		}

		return filter;
	}

	public void resetFilter(boolean Notify){
		for (CheckNode cn : allCheckNodesVector){
			cn.setEnabled(true);
			cn.setSelected(true);
			cn.getPredefinedValue().setEnabled(true);
		}

		if (Notify)
			doc.dispatchEvent(Ni3ItemListener.MSG_PreFilterTreeChanged, Ni3ItemListener.SRC_PreFilterTree, null, null);
	}

	void setSelected(CheckNode parent, boolean isSelected){
		CheckNode nd;

		parent.getPredefinedValue().setEnabled(isSelected);

		parent.setSelected(isSelected);

		setParentSelected((CheckNode) parent.getParent());

		for (Enumeration en = parent.children(); en.hasMoreElements(); ){
			nd = (CheckNode) en.nextElement();
			setSelected(nd, isSelected);
			((DefaultTreeModel) getModel()).nodeChanged(nd);
		}

		((DefaultTreeModel) getModel()).nodeChanged(parent);
	}

	void setEnabled(CheckNode parent, boolean isEnabled){
		CheckNode nd;

		parent.setEnabled(isEnabled);
		for (Enumeration en = parent.children(); en.hasMoreElements(); ){
			nd = (CheckNode) en.nextElement();
			setEnabled(nd, isEnabled);
			((DefaultTreeModel) getModel()).nodeChanged(nd);
		}

		((DefaultTreeModel) getModel()).nodeChanged(parent);
	}

	public void setPrefilter(DataFilter prefilter){
		this.SYSGroupPrefilter = prefilter;
	}

	class NodeSelectionListener extends MouseAdapter{
		JTree tree;

		NodeSelectionListener(JTree tree){
			this.tree = tree;
		}

		public void mouseClicked(MouseEvent e){
			int x = e.getX();
			int y = e.getY();
			int row = tree.getRowForLocation(x, y);

			TreePath path = tree.getPathForRow(row);
			if (path != null){
				CheckNode node = (CheckNode) path.getLastPathComponent();
				if (node.getPredefinedValue() != null && node.isEnabled()){
					doc.setUndoRedoPoint(true);

					boolean isSelected = !(node.isSelected());

					setSelected(node, isSelected);

					doc.dispatchEvent(Ni3ItemListener.MSG_PreFilterTreeChanged, Ni3ItemListener.SRC_PreFilterTree, null,
							node);
					repaint();
				}
			}
		}
	}
}
