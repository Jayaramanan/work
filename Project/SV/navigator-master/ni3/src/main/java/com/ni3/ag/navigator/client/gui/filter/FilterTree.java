/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.filter;

import java.util.Enumeration;
import java.util.Vector;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.controller.graph.ValueUsageStatistics;
import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.model.Ni3Document;

@SuppressWarnings( { "unchecked", "serial" })
public class FilterTree extends JTree{

	private final Color CATEGORY_HALO_COLOR = new CategoryColor(0, 0, 0);

	private CheckNode root;

	private Vector<CheckNode> allCheckNodesVector = new Vector<CheckNode>();
	private Vector<CheckNode> allCheckNodesSecond = new Vector<CheckNode>();

	private Ni3Document doc;

	private DataFilter SYSGroupPrefilter;

	private boolean showDisplayedCountInLabel;
	private boolean showEmptyValues;
	private boolean onlyOneValue;
	private ValueUsageStatistics statistics;
	public int commonCount;

	public FilterTree(Ni3Document doc, DataFilter SYSGroupPrefilter, boolean showDisplayedCountInLabel,
			boolean showEmptyValues, boolean showNodes, boolean showEdges, boolean onlyOneValue,
			ValueUsageStatistics statistics){
		super(new CheckNode("Root", null, true, null));
		commonCount = 0;

		this.doc = doc;
		this.showDisplayedCountInLabel = showDisplayedCountInLabel;
		this.showEmptyValues = showEmptyValues;
		this.onlyOneValue = onlyOneValue;

		this.SYSGroupPrefilter = SYSGroupPrefilter;

		this.statistics = statistics;

		root = (CheckNode) getModel().getRoot();

		createTree(showNodes, showEdges);
	}

	public Vector<CheckNode> getAllNodes(){
		return allCheckNodesVector;
	}

	public boolean isShowEmptyValues(){
		return showEmptyValues;
	}

	public int getListenerType(){
		return Ni3ItemListener.SRC_FilterTree;
	}

	private void createChildren(CheckNode parentNode, int ParentID){

		for (Entity def : doc.DB.schema.definitions){
			if (def.CanRead)
				for (Attribute a : def.getAttributesSortedForFilter()){
					if (!a.inContext || doc.isCurrentTopic()){
						if (a.getValuesToUse() != null){
							for (Value v : a.getValuesToUse()){
								if ((showEmptyValues || statistics.isUsed(v)) && v.getParentId() == ParentID
										&& !SYSGroupPrefilter.checkExclusion(v.getId())){
									CheckNode node = new CheckNode(v.getLabel(), v, true, null);
									allCheckNodesVector.addElement(node);

									parentNode.add(node);
									createChildren(node, v.getId());
								}
							}
						}
					}
				}
		}

		boolean HasHalo = false;
		for (Enumeration<CheckNode> e = parentNode.children(); e.hasMoreElements();){
			CheckNode checkNode = e.nextElement();
			if (checkNode.getPredefinedValue().getHaloColor() != null)
				HasHalo = true;
		}

		if (HasHalo)
			parentNode.getPredefinedValue().setHaloColor(CATEGORY_HALO_COLOR);
	}

	void createTree(boolean showNodes, boolean showEdges){
		CheckNode checkNodeFirstLevel;
		CheckNode checkNodeSecondLevel;
		CheckNode checkNodeThirdLevel;

		root.removeAllChildren();
		allCheckNodesVector.clear();

		// Node types
		for (Entity def : doc.DB.schema.definitions){
			if (!def.CanRead)
				continue;

			if (def.isEdge() && !showEdges)
				continue;

			if (!def.isEdge() && !showNodes)
				continue;

			checkNodeFirstLevel = null;

			for (Attribute a : def.getAttributesSortedForFilter()){
				if (a.inFilter && (!a.inContext || doc.isCurrentTopic())){
					checkNodeSecondLevel = new CheckNode(a.label, new Value(-a.ID, 0, a.name, a.label), true, null);

					boolean showAttribute = showEmptyValues;
					if (a.getValuesToUse() != null){
						for (Value v : a.getValuesToUse()){
							if ((showEmptyValues || statistics.isUsed(v)) && v.getParentId() == 0
									&& !SYSGroupPrefilter.checkExclusion(v.getId())){
								checkNodeThirdLevel = new CheckNode(v.getLabel(), v, true, null);
								allCheckNodesVector.addElement(checkNodeThirdLevel);

								checkNodeSecondLevel.add(checkNodeThirdLevel);
								createChildren(checkNodeThirdLevel, v.getId());
								showAttribute = true;
							}
						}
					}

					if (showAttribute){
						if (checkNodeFirstLevel == null){
							checkNodeFirstLevel = createEntityNode(def);
						}

						allCheckNodesSecond.addElement(checkNodeSecondLevel);
						allCheckNodesVector.addElement(checkNodeSecondLevel);

						checkNodeFirstLevel.add(checkNodeSecondLevel);

						boolean HasHalo = false;
						CheckNode dete;
						for (Enumeration<CheckNode> e = checkNodeSecondLevel.children(); e.hasMoreElements();){
							dete = e.nextElement();
							if (dete.getPredefinedValue().getHaloColor() != null)
								HasHalo = true;
						}

						if (HasHalo)
							checkNodeSecondLevel.getPredefinedValue().setHaloColor(CATEGORY_HALO_COLOR);
					}
				}
			}
		}

		DefaultTreeModel model = (DefaultTreeModel) getModel();
		model.reload();
	}

	private CheckNode createEntityNode(Entity def){
		Icon icon = null;

		CheckNode entityNode = new CheckNode(def.Name, null, true, def);
		root.add(entityNode);
		return entityNode;
	}

	public void setTree(boolean showHalo){
		setRootVisible(true);
		setShowsRootHandles(true);
		expandRow(0);
		setRootVisible(false);

		setCellRenderer(new CheckRenderer(showHalo));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		putClientProperty("JTree.lineStyle", "Angled");
		addMouseListener(new NodeSelectionListener(this));
	}

	public void resetFilter(boolean Notify){
		for (CheckNode cn : allCheckNodesVector){
			cn.setEnabled(true);
			cn.setSelected(true);
			cn.setHaloSelected(false);
		}

		if (Notify)
			doc.dispatchEvent(Ni3ItemListener.MSG_FilterTreeChanged, Ni3ItemListener.SRC_FilterTree, null, null);
	}

	public void clearFilter(boolean Notify){
		for (CheckNode cn : allCheckNodesVector){
			cn.setEnabled(true);
			cn.setSelected(false);
			cn.setHaloSelected(false);
		}

		if (Notify)
			doc.dispatchEvent(Ni3ItemListener.MSG_FilterTreeChanged, Ni3ItemListener.SRC_FilterTree, null, null);
	}

	public void syncStatus(Vector<CheckNode> nodes){
		for (CheckNode node : nodes){
			setNodeStatus(node.getPredefinedValue(), node.isSelected());
		}
	}

	public void syncComplete(DataFilter filter, boolean initialStatus){
		for (CheckNode cn : allCheckNodesVector){
			cn.setEnabled(true);
			cn.setSelected(initialStatus);
		}

		for (Value val : filter.filter.values()){
			setNodeStatus(val, false);
			setNodeSelected(val, false, true);
		}
		doc.dispatchEvent(Ni3ItemListener.MSG_FilterTreeChanged, Ni3ItemListener.SRC_FilterTree, null, null);
	}

	public boolean setNodeStatus(Value value, boolean Status){
		for (CheckNode cn : allCheckNodesVector){
			if (cn.getPredefinedValue() != null && cn.getPredefinedValue().getId() == value.getId()){
				setEnabled(cn, Status);
				return true;
			}
		}

		for (CheckNode cn : allCheckNodesSecond){
			if (cn.getPredefinedValue() != null && cn.getPredefinedValue().getId() == value.getId()){
				setEnabled(cn, Status);
				return true;
			}
		}

		return false;
	}

	public void setParentSelected(CheckNode cn){
		if (cn != null){
			boolean toSelect = false;

			for (Enumeration e = cn.children(); e.hasMoreElements();){
				CheckNode cc = (CheckNode) e.nextElement();
				if (cc.isSelected()){
					toSelect = true;
					break;
				}
			}

			cn.setSelected(toSelect);

			setParentSelected((CheckNode) cn.getParent());
		}
	}

	public void setHaloParentSelected(CheckNode cn){
		if (cn != null){
			boolean toSelect = false;

			for (Enumeration e = cn.children(); e.hasMoreElements();){
				CheckNode cc = (CheckNode) e.nextElement();
				if (cc.isHaloSelected() && cc.getPredefinedValue() != null && cc.getPredefinedValue().getHaloColor() != null
						&& statistics.isUsed(cc.getPredefinedValue())){
					toSelect = true;
					break;
				}
			}

			cn.setHaloSelected(toSelect);

			setHaloParentSelected((CheckNode) cn.getParent());
		}
	}

	public boolean setNodeSelected(Value value, boolean Status, boolean Propagate){
		for (CheckNode cn : allCheckNodesVector){
			if (cn.getPredefinedValue() != null && cn.getPredefinedValue().getId() == value.getId()){
				if (Propagate)
					setSelected(cn, Status);
				else
					cn.setSelected(Status);

				setParentSelected((CheckNode) cn.getParent());

				return true;
			}
		}

		for (CheckNode cn : allCheckNodesSecond){
			if (cn.getPredefinedValue() != null && cn.getPredefinedValue().getId() == value.getId()){
				if (Propagate)
					setSelected(cn, Status);
				else
					cn.setSelected(Status);

				setParentSelected((CheckNode) cn.getParent());

				return true;
			}
		}

		return false;
	}

	public void ExpandAll(){
		for (int nn = 0; nn < getRowCount(); nn++){
			expandRow(nn);
		}
	}

	public void restoreFilter(DataFilter filter){
		if (filter == null)
			return;
		for (CheckNode cn : allCheckNodesVector){
			cn.setHaloSelected(false);
		}

		for (CheckNode cn : allCheckNodesVector){

			if (filter.filter.containsKey(cn.getPredefinedValue().getId()))
				cn.setSelected(false);
			else
				cn.setSelected(true);

			if (cn.getPredefinedValue().getAttribute() != null
					&& filter.filter.containsKey(-cn.getPredefinedValue().getAttribute().ID))
				cn.setSelected(false);

			for (Integer i : filter.haloOn)
				if (cn.getPredefinedValue().getId() == i){
					cn.setHaloSelected(true);
					break;
				}
		}

		if (filter.expanded != null)
			for (Integer i : filter.expanded){
				expandRow(i);
			}
	}

	public void selectAll(boolean status){
		for (CheckNode cn : allCheckNodesVector){
			cn.setSelected(status);
		}

		repaint();
	}

	public void selectAllExceptOne(boolean status, int ID){
		for (CheckNode cn : allCheckNodesVector){
			if (cn.getPredefinedValue().getId() != ID)
				cn.setSelected(status);
		}

		repaint();
	}

	public DataFilter createFilter(DataFilter filter){
		filter.copyFilter(SYSGroupPrefilter);

		for (CheckNode cn : allCheckNodesVector){
			if (cn.getPredefinedValue() != null){
				if (!cn.isSelected()){
					// If is not selected, put values in DataFilter
					filter.addExclusion(cn.getPredefinedValue());
				}

				if (cn.isHaloSelected())
					filter.addHalo(cn.getPredefinedValue().getId());
			}
		}

		for (int nn = 0; nn < getRowCount(); nn++){
			if (isExpanded(nn)){
				filter.addExpansion(nn);
			}

		}

		return filter;
	}

	public DataFilter createAntiFilter(DataFilter filter){
		commonCount = 0;
		for (CheckNode cn : allCheckNodesVector){
			if (cn.getPredefinedValue() != null && cn.isSelected() && cn.isEnabled() && cn.getChildCount() == 0){
				// If is not selected, put values in DataFilter
				commonCount += statistics.getUsage(cn.getPredefinedValue());
				filter.addExclusion(cn.getPredefinedValue());
			}
		}
		return filter;
	}

	public void setAntiFilter(DataFilter filter){
		for (CheckNode cn : allCheckNodesVector){
			if (cn.getPredefinedValue() != null){
				if (filter.filter.containsKey(cn.getPredefinedValue().getId()))
					cn.setSelected(true);
			}
		}
	}

	void setSelected(CheckNode parent, boolean isSelected){
		CheckNode nd;

		parent.setSelected(isSelected);

		setParentSelected((CheckNode) parent.getParent());

		for (Enumeration en = parent.children(); en.hasMoreElements();){
			nd = (CheckNode) en.nextElement();
			setSelected(nd, isSelected);
		}

		((DefaultTreeModel) getModel()).nodeChanged(parent);
	}

	void setHaloSelected(CheckNode parent, boolean isSelected, boolean checkParent){
		CheckNode nd;

		parent.setHaloSelected(isSelected);
		if (checkParent){
			setHaloParentSelected((CheckNode) parent.getParent());
		}

		for (Enumeration en = parent.children(); en.hasMoreElements();){
			nd = (CheckNode) en.nextElement();
			setHaloSelected(nd, isSelected, false);
		}

		((DefaultTreeModel) getModel()).nodeChanged(parent);
	}

	void setEnabled(CheckNode parent, boolean isEnabled){
		CheckNode nd;

		parent.setEnabled(isEnabled);
		for (Enumeration en = parent.children(); en.hasMoreElements();){
			nd = (CheckNode) en.nextElement();
			setEnabled(nd, isEnabled);
		}

		((DefaultTreeModel) getModel()).nodeChanged(parent);
	}

	public void setPrefilter(DataFilter prefilter){
		this.SYSGroupPrefilter = prefilter;
	}

	public void resetHalos(){
		for (CheckNode cn : allCheckNodesVector){
			cn.setHaloSelected(false);
		}

		repaint();
		doc.dispatchEvent(Ni3ItemListener.MSG_FilterTreeChanged, Ni3ItemListener.SRC_FilterTree, null, null);
	}

	private class CategoryColor extends Color{

		public CategoryColor(int r, int g, int b){
			super(r, g, b);
		}
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
					if (x > node.getXOffsetOfHaloCheck() + (path.getPathCount() - 1) * 20){
						boolean isSelected = !(node.isHaloSelected());

						setHaloSelected(node, isSelected, true);
						doc.dispatchEvent(Ni3ItemListener.MSG_FilterTreeChanged, Ni3ItemListener.SRC_FilterTree, null, node);
					} else{
						invertNodeSelection(node);
					}
				}

				repaint();
			}
		}

	}

	public void invertNodeSelection(CheckNode node){
		doc.setUndoRedoPoint(true);

		boolean isSelected = !(node.isSelected());

		setSelected(node, isSelected);
		if (isSelected && onlyOneValue)
			selectAllExceptOne(false, node.getPredefinedValue().getId());

		doc.dispatchEvent(Ni3ItemListener.MSG_FilterTreeChanged, Ni3ItemListener.SRC_FilterTree, null, node);
	}

	public class CheckRenderer extends JPanel implements TreeCellRenderer{
		private JCheckBox check;
		private TreeCheckHalo halocheck;
		private TreeLabel label;

		private boolean imaCheck = true;
		private boolean showHalo = true;

		public CheckRenderer(boolean showHalo){
			setLayout(null);
			add(label = new TreeLabel());
			add(check = new JCheckBox());
			add(halocheck = new TreeCheckHalo());

			this.showHalo = showHalo;

			check.setBackground(UIManager.getColor("Tree.textBackground"));

			label.setForeground(UIManager.getColor("Tree.textForeground"));
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
				boolean leaf, int row, boolean hasFocus){
			if (!(value instanceof CheckNode))
				return this;

			String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, hasFocus);

			setEnabled(tree.isEnabled() && ((CheckNode) value).isEnabled());
			check.setEnabled(tree.isEnabled() && ((CheckNode) value).isEnabled());
			check.setSelected(((CheckNode) value).isSelected());

			label.setEnabled(tree.isEnabled() && ((CheckNode) value).isEnabled());
			label.setFont(tree.getFont());
			label.setSelected(isSelected);
			label.setFocus(hasFocus);

			label.setIcon(((CheckNode) value).getIcon());

			if (((CheckNode) value).getPredefinedValue() != null){
				Value v = ((CheckNode) value).getPredefinedValue();

				halocheck.HaloColor = v.getHaloColor();
				if (!showHalo)
					halocheck.HaloColor = null;

				Rectangle rect = halocheck.getBounds();
				((CheckNode) value).setXOffsetOfHaloCheck(rect.x);

				int count;
				int countDisplayed = 0;

				if (v.getId() < 0){
					count = 0;

					CheckNode cn = (CheckNode) value;
					CheckNode dete;
					for (Enumeration<CheckNode> e = cn.children(); e.hasMoreElements();){
						dete = e.nextElement();

						count += statistics.getUsage(dete.getPredefinedValue());
						countDisplayed += statistics.getDisplayUsage(dete.getPredefinedValue());
					}
				} else{
					count = statistics.getUsage(v);
					countDisplayed = statistics.getDisplayUsage(v);
				}

				if (count != 0){
					if (!showDisplayedCountInLabel){
						label.setText(stringValue + " (" + count + ")");
					} else if (v.getId() > 0)
						label.setText(stringValue + " (" + countDisplayed + "/" + count + ")");
					else
						label.setText(stringValue);
					label.setSize(120, label.getWidth());
					halocheck.setSelected(v.isHaloColorSelected());
				} else{
					label.setText(stringValue);
					halocheck.setSelected(false);
				}
			} else{
				int count, countAll;
				count = countAll = 0;
				if (((CheckNode) value).getEntity() != null){
					Entity e = ((CheckNode) value).getEntity();
					for (GraphObject obj : doc.Subgraph.getObjects())
						if (obj.Type == e.ID){
							countAll++;
							if (!obj.isFilteredOut())
								count++;
						}

					if (!showDisplayedCountInLabel)
						label.setText(stringValue);
					else
						label.setText(stringValue + " (" + count + "/" + countAll + ")");

				}
			}

			imaCheck = ((CheckNode) value).hasCheckbox();
			check.setVisible(imaCheck);
			halocheck.setVisible(imaCheck);
			return this;
		}

		public Dimension getPreferredSize(){
			Dimension d_check = check.getPreferredSize();
			Dimension d_label = label.getPreferredSize();

			Dimension d_halo = halocheck.getPreferredSize();
			int halowidth = d_halo.width;
			if (halocheck.HaloColor == null)
				halowidth = 0;

			return new Dimension(d_check.width + (d_label.width + 35) + halowidth, (d_check.height < d_label.height
					? d_label.height : d_check.height));
		}

		public void doLayout(){
			Dimension d_check = check.getPreferredSize();
			Dimension d_label = label.getPreferredSize();

			Dimension d_halo = halocheck.getPreferredSize();
			int halowidth = d_halo.width;
			if (halocheck.HaloColor == null)
				halowidth = 0;

			int y_check = 0;
			int y_label = 0;
			if (d_check.height < d_label.height){
				y_check = (d_label.height - d_check.height) / 2;
			} else{
				y_label = (d_check.height - d_label.height) / 2;
			}
			check.setLocation(0, y_check);
			check.setBounds(0, y_check, d_check.width, d_check.height - 2);
			label.setLocation(d_check.width + halowidth, y_label);
			label.setBounds(d_check.width + halowidth, y_label, d_label.width + 5, d_label.height);
			halocheck.setLocation(d_check.width, y_label);
			halocheck.setBounds(d_check.width, y_label, halowidth, d_label.height);

			if (!imaCheck){
				label.setLocation(check.getLocation());
			}
		}

		public void setBackground(Color color){
			if (color instanceof ColorUIResource)
				color = null;
			super.setBackground(color);
		}

		public class TreeCheckHalo extends JCheckBox{
			public Color HaloColor;

			TreeCheckHalo(){
				setOpaque(false);
			}

			public void paint(Graphics g){
				if (HaloColor != null){
					Dimension d = getPreferredSize();
					g.setColor(HaloColor);
					((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
					((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					int height = d.height - 7;
					if (isSelected()){
						if (HaloColor instanceof CategoryColor){
							g.drawOval(0, 0, height, height);
							g.fillPolygon(new int[] { 3, height / 2, height - 2, height / 2 }, new int[] { 6, height - 3, 3,
									height / 2 }, 4);
						} else{
							g.fillOval(0, 0, height, height);
						}
					} else
						g.drawOval(0, 0, height, height);
				}
			}
		}

		public class TreeLabel extends JLabel{
			boolean isSelected;
			boolean hasFocus;

			public TreeLabel(){
			}

			public void setBackground(Color color){
				if (color instanceof ColorUIResource)
					color = null;
				super.setBackground(color);
			}

			public void paint(Graphics g){
				String str;
				if ((str = getText()) != null){
					if (str.length() > 0){

						if (isSelected){
							g.setColor(new Color(0, 255, 255));
						} else{
							g.setColor(UIManager.getColor("Tree.textBackground"));
						}

						Dimension d = getPreferredSize();
						int imageOffset = 0;
						Icon currentI = getIcon();
						if (currentI != null){
							imageOffset = currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
						}
						g.fillRect(imageOffset, 0, d.width - 1 - imageOffset, d.height - 3);
					}
				}

				super.paint(g);
			}

			public Dimension getPreferredSize(){
				Dimension retDimension = super.getPreferredSize();
				if (retDimension != null){
					retDimension = new Dimension(retDimension.width + 30, retDimension.height + 5);
				}
				return retDimension;
			}

			public void setSelected(boolean isSelected){
				this.isSelected = isSelected;
			}

			public void setFocus(boolean hasFocus){
				this.hasFocus = hasFocus;
			}
		}
	}
}
