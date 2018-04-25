/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;

public class UserAdminLeftPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private ACTree tree;
	private JScrollPane treeScroll;
	private ACButton btnDelete;
	private ACButton btnAdd;
    private ACButton btnCopy;

	private ACToolBar toolBar;

	public UserAdminLeftPanel(){
		initComponents();
	}

	private void initComponents(){
		SpringLayout treeSpringLayout = new SpringLayout();
		setLayout(treeSpringLayout);

		toolBar = new ACToolBar();
		btnAdd = toolBar.makeAddGroupButton();
		btnDelete = toolBar.makeDeleteGroupButton();
        btnCopy = toolBar.makeCopyGroupButton();
		add(toolBar);

		treeSpringLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, this);

		tree = new ACTree();
		tree.setCellRenderer(new UserAdminTreeCellRenderer());
		tree.setExpandsSelectedPaths(true);

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

	public void setTreeModel(UserAdminTreeModel model){
		tree.setModel(model);
	}

	public UserAdminTreeModel getTreeModel(){
		return (UserAdminTreeModel) tree.getModel();
	}

	public void addDeleteGroupButtonListener(ActionListener listener){
		btnDelete.addActionListener(listener);
	}

	public void addAddGroupButtonListener(ActionListener listener){
		btnAdd.addActionListener(listener);
	}

	public void addCopyGroupButtonListener(ActionListener listener){
		btnCopy.addActionListener(listener);
	}

	public TreePath getSelectionTreePath(){
		return tree.getSelectionPath();
	}

	public int[] getSelectionRows(){
		return tree.getSelectionRows();
	}

	public void setSelectionRow(int row){
		tree.setSelectionRow(row);
	}

	/**
	 * Use to re-extend tree when refresh/update button is pressed
	 * 
	 * @param path
	 */
	public void setSelectionTreePath(TreePath path){
		tree.setSelectionPath(path);
	}

	public JTree getTree(){
		return tree;
	}
}
