/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.metaphoradmin;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;

public class NodeMetaphorLeftPanel extends JPanel{
	private static final long serialVersionUID = -3673522377962520333L;
	private ACTree schemaTree;
	private JScrollPane treeScroll;

	public NodeMetaphorLeftPanel(){
		initComponents();
	}

	private void initComponents(){
		schemaTree = new ACTree();
		schemaTree.setCellRenderer(new ACTreeCellRenderer());
		schemaTree.setExpandsSelectedPaths(true);

		treeScroll = new JScrollPane(schemaTree);
		add(treeScroll);

		SpringLayout treeSpringLayout = new SpringLayout();
		setLayout(treeSpringLayout);
		treeSpringLayout.putConstraint(SpringLayout.NORTH, treeScroll, 10, SpringLayout.NORTH, this);
		treeSpringLayout.putConstraint(SpringLayout.SOUTH, treeScroll, -10, SpringLayout.SOUTH, this);
		treeSpringLayout.putConstraint(SpringLayout.EAST, treeScroll, -10, SpringLayout.EAST, this);
		treeSpringLayout.putConstraint(SpringLayout.WEST, treeScroll, 10, SpringLayout.WEST, this);
	}

	public void addTreeSelectionListener(TreeSelectionListener tsl){
		schemaTree.addTreeSelectionListener(tsl);
	}

	public void setTreeModel(NodeMetaphorTreeModel model){
		schemaTree.setModel(model);
	}

	public NodeMetaphorTreeModel getTreeModel(){
		return (NodeMetaphorTreeModel) schemaTree.getModel();
	}

	public TreePath getSelectionTreePath(){
		return schemaTree.getSelectionPath();
	}

	/**
	 * Use to re-extend tree when refresh/update button is pressed
	 * 
	 * @param path
	 */
	public void setSelectionTreePath(TreePath path){
		schemaTree.setSelectionPath(path);
	}

	public JTree getSchemaTree(){
		return schemaTree;
	}

	public void updateTree(){
		schemaTree.updateUI();
	}
}
