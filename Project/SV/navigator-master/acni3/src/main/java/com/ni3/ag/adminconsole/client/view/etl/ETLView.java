/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.etl;

import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;
import javax.swing.tree.TreePath;


import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.common.ACLinkButton;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;


public class ETLView extends JPanel implements AbstractView, ErrorRenderer{
	private static final long serialVersionUID = -264667622360385361L;

	private ACTree tree;
	private ACLinkButton etlLinkButton;
	private ErrorPanel errorPanel;

	@Override
	public void initializeComponents(){
		SpringLayout elementLayout = new SpringLayout();
		this.setLayout(elementLayout);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation((int) (ACMain.getScreenWidth() / 5));

		tree = new ACTree();
		tree.setCellRenderer(new ACTreeCellRenderer());
		tree.setExpandsSelectedPaths(true);
		JScrollPane treeScroll = new JScrollPane(tree);

		JPanel leftPanel = new JPanel();
		SpringLayout leftPanelLayout = new SpringLayout();
		leftPanel.setLayout(leftPanelLayout);

		leftPanelLayout.putConstraint(SpringLayout.WEST, treeScroll, 10, SpringLayout.WEST, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.NORTH, treeScroll, 10, SpringLayout.NORTH, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.SOUTH, treeScroll, -10, SpringLayout.SOUTH, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.EAST, treeScroll, -10, SpringLayout.EAST, leftPanel);

		splitPane.setLeftComponent(leftPanel);
		leftPanel.add(treeScroll);

		JPanel rightPanel = new JPanel();
		splitPane.setRightComponent(rightPanel);
		SpringLayout rightPanelLayout = new SpringLayout();
		rightPanel.setLayout(rightPanelLayout);

		errorPanel = new ErrorPanel();
		this.add(errorPanel);

		this.add(splitPane);

		elementLayout.putConstraint(SpringLayout.WEST, splitPane, 0, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.NORTH, splitPane, 0, SpringLayout.SOUTH, errorPanel);
		elementLayout.putConstraint(SpringLayout.SOUTH, splitPane, 0, SpringLayout.SOUTH, this);
		elementLayout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.EAST, this);

		etlLinkButton = new ACLinkButton(TextID.ETLLink);
		rightPanel.add(etlLinkButton);

		rightPanelLayout.putConstraint(SpringLayout.WEST, etlLinkButton, 10, SpringLayout.WEST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.NORTH, etlLinkButton, 30, SpringLayout.NORTH, rightPanel);
	}

	public void addETLLinkButtonListener(ActionListener listener){
		etlLinkButton.addActionListener(listener);
	}

	@Override
	public boolean isChanged(){
		return false;
	}

	@Override
	public void resetEditedFields(){
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] {});
		if (currentPath != null){
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, tree.getModel());
			tree.setSelectionPath(found);
		}
	}

	@Override
	public void renderErrors(List<ErrorEntry> errors){
	}

	public ACTree getTree(){
		return tree;
	}

	public void setTreeModel(ETLTreeModel treeModel){
		tree.setModel(treeModel);
	}

}
