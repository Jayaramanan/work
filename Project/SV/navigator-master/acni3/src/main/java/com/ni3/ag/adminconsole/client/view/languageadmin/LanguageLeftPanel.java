/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.languageadmin;

import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;

public class LanguageLeftPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private ACTree tree;
	private JScrollPane treeScroll;
	private ACButton btnDelete;
	private ACButton btnAdd;
	ACToolBar toolBar;

	public LanguageLeftPanel(){
		initComponents();
	}

	private void initComponents(){
		SpringLayout treeSpringLayout = new SpringLayout();
		setLayout(treeSpringLayout);

		toolBar = new ACToolBar();
		btnAdd = toolBar.makeAddLanguageButton();
		btnDelete = toolBar.makeDeleteLanguageButton();
		add(toolBar);

		treeSpringLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, this);

		tree = new ACTree();
		tree.setCellRenderer(new ACTreeCellRenderer());
		tree.setExpandsSelectedPaths(true);
		tree.setEditable(true);
		tree.setCellEditor(new LanguageTreeCellEditor(tree, (DefaultTreeCellRenderer) tree.getCellRenderer()));

		treeScroll = new JScrollPane(tree);
		add(treeScroll);

		treeSpringLayout.putConstraint(SpringLayout.NORTH, treeScroll, 0, SpringLayout.SOUTH, toolBar);
		treeSpringLayout.putConstraint(SpringLayout.SOUTH, treeScroll, -10, SpringLayout.SOUTH, this);
		treeSpringLayout.putConstraint(SpringLayout.EAST, treeScroll, -10, SpringLayout.EAST, this);
		treeSpringLayout.putConstraint(SpringLayout.WEST, treeScroll, 10, SpringLayout.WEST, this);

	}

	public void addTreeSelectionListener(TreeSelectionListener tsl){
		tree.addTreeSelectionListener(tsl);
	}

	public void setTreeModel(LanguageTreeModel model){
		tree.setModel(model);
	}

	public LanguageTreeModel getTreeModel(){
		return (LanguageTreeModel) tree.getModel();
	}

	public void addDeleteLanguageButtonListener(ActionListener listener){
		btnDelete.addActionListener(listener);
	}

	public void addAddLanguageButtonListener(ActionListener listener){
		btnAdd.addActionListener(listener);
	}

	public TreePath getSelectionTreePath(){
		return tree.getSelectionPath();
	}

	/**
	 * Use to re-extend tree when refresh/update button is pressed
	 * 
	 * @param path
	 */
	public void setSelectionTreePath(TreePath path){
		tree.setSelectionPath(path);
		tree.expandPath(path);
	}

	public JTree getTree(){
		return tree;
	}
}
