/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreePath;


import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACCellEditor;
import com.ni3.ag.adminconsole.client.view.common.ACComboBox;
import com.ni3.ag.adminconsole.client.view.common.ACTable;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.common.BooleanCellRenderer;
import com.ni3.ag.adminconsole.client.view.common.calendar.ACDateEditor;
import com.ni3.ag.adminconsole.client.view.common.calendar.ACDateRenderer;
import com.ni3.ag.adminconsole.client.view.common.calendar.ACCalendarDialog.DisplayType;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.OfflineJob;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.jobs.JobType;


public class ThickClientView extends JPanel implements AbstractView, ErrorRenderer{

	private static final long serialVersionUID = 1L;
	private ACTree tree;
	private ACTable tableJobs;
	private ACButton addButton;
	private ACButton updateButton;
	private ACButton launchNowButton;
	private ACButton previewButton;
	private ACButton deleteButton;
	private ACButton refreshButton;
	private ACComboBox jobTypeCombo;
	private JLabel previewLabel;

	private ErrorPanel errorPanel;

	private UserSelectionEditor userSelectionEditor;

	private ThickClientView(){
	}

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

		ACToolBar toolBar = new ACToolBar();
		addButton = toolBar.makeAddButton();
		deleteButton = toolBar.makeDeleteButton();
		updateButton = toolBar.makeUpdateButton();
		refreshButton = toolBar.makeRefreshButton();
		previewButton = toolBar.makePreviewButton();
		launchNowButton = toolBar.makeLaunchNowButton();
		rightPanel.add(toolBar);
		rightPanelLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, rightPanel);
		previewLabel = new JLabel();
		rightPanel.add(previewLabel);
		rightPanelLayout.putConstraint(SpringLayout.NORTH, previewLabel, 0, SpringLayout.SOUTH, toolBar);
		rightPanelLayout.putConstraint(SpringLayout.WEST, previewLabel, 10, SpringLayout.WEST, rightPanel);

		JScrollPane scrollPaneJobs = new JScrollPane();
		tableJobs = new ACTable();
		tableJobs.enableCopyPaste();
		tableJobs.enableToolTips();
		scrollPaneJobs.setViewportView(tableJobs);
		rightPanel.add(scrollPaneJobs);

		errorPanel = new ErrorPanel();
		this.add(errorPanel);

		this.add(splitPane);

		elementLayout.putConstraint(SpringLayout.WEST, splitPane, 0, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.NORTH, splitPane, 0, SpringLayout.SOUTH, errorPanel);
		elementLayout.putConstraint(SpringLayout.SOUTH, splitPane, 0, SpringLayout.SOUTH, this);
		elementLayout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.EAST, this);

		rightPanelLayout.putConstraint(SpringLayout.WEST, scrollPaneJobs, 10, SpringLayout.WEST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.NORTH, scrollPaneJobs, 0, SpringLayout.SOUTH, previewLabel);
		rightPanelLayout.putConstraint(SpringLayout.SOUTH, scrollPaneJobs, -10, SpringLayout.SOUTH, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.EAST, scrollPaneJobs, -10, SpringLayout.EAST, rightPanel);

		tableJobs.setDefaultRenderer(User.class, new UserTableCellRenderer());
		TableCellRenderer boolCellRenderer = tableJobs.getDefaultRenderer(Boolean.class);
		tableJobs.setDefaultRenderer(Boolean.class, new BooleanCellRenderer(boolCellRenderer));

		jobTypeCombo = new ACComboBox();
		tableJobs.setDefaultEditor(JobType.class, new ACCellEditor(jobTypeCombo));

		tableJobs.setDefaultRenderer(Date.class, new ACDateRenderer(DisplayType.DateTime));
		tableJobs.setDefaultEditor(Date.class, new ACDateEditor(DisplayType.DateTime));

		userSelectionEditor = new UserSelectionEditor();
		tableJobs.setDefaultEditor(String.class, userSelectionEditor);

		setJobTypeComboData();
	}

	public void setPreviewText(String text){
		previewLabel.setText(text);
	}

	public void addTreeSelectionListener(TreeSelectionListener tsl){
		tree.addTreeSelectionListener(tsl);
	}

	public void setTreeModel(ThickClientTreeModel model){
		tree.setModel(model);
	}

	private void setJobTypeComboData(){
		for (JobType jt : JobType.values()){
			jobTypeCombo.addItem(jt);
		}
	}

	public JTree getTree(){
		return tree;
	}

	public ThickClientTreeModel getTreeModel(){
		return (ThickClientTreeModel) tree.getModel();
	}

	public void setTableModel(OfflineJobTableModel model){
		tableJobs.setModel(model);
		tableJobs.setRowSorter(new TableRowSorter<OfflineJobTableModel>(model));
	}

	public void setUserSelectionEditorGroups(List<Group> groups){
		userSelectionEditor.setGroups(groups);
	}

	public OfflineJobTableModel getTableModel(){
		return (OfflineJobTableModel) tableJobs.getModel();
	}

	public void addUpdateButtonActionListener(ActionListener actionListener){
		updateButton.addActionListener(actionListener);
	}

	public void addAddButtonActionListener(ActionListener actionListener){
		addButton.addActionListener(actionListener);
	}

	public void addDeleteButtonActionListener(ActionListener actionListener){
		deleteButton.addActionListener(actionListener);
	}

	public void addRefreshButtonActionListener(ActionListener actionListener){
		refreshButton.addActionListener(actionListener);
	}

	public void addPreviewButtonActionListener(ActionListener actionListener){
		previewButton.addActionListener(actionListener);
	}

	public void addLaunchNowButtonActionListener(ActionListener actionListener){
		launchNowButton.addActionListener(actionListener);
	}

	public void refreshTable(){
		getTableModel().fireTableDataChanged();
	}

	public int getSelectedRowIndex(){
		if (tableJobs.getSelectedRow() >= 0){
			return tableJobs.convertRowIndexToModel(tableJobs.getSelectedRow());
		}
		return -1;
	}

	public OfflineJob getSelectedJob(){
		return getTableModel().getSelectedJob(getSelectedRowIndex());
	}

	public void setActiveTableRow(OfflineJob job){
		OfflineJobTableModel model = getTableModel();
		int modelIndex = model.indexOf(job);
		if (modelIndex >= 0){
			tableJobs.setSelectedCellIndexes(new int[] { modelIndex, modelIndex, 0, 0 });

			int index = tableJobs.convertRowIndexToView(modelIndex);
			if (index >= 0){
				Rectangle r = tableJobs.getCellRect(index, 0, true);
				tableJobs.scrollRectToVisible(r);
			}
		}

		tableJobs.requestFocusInWindow();
	}

	public void stopCellEditing(){
		if (tableJobs.isEditing())
			tableJobs.getCellEditor().stopCellEditing();
	}

	@Override
	public void renderErrors(List<ErrorEntry> errors){
		List<String> msgs = new ArrayList<String>();
		for (int i = 0; i < errors.size(); i++){
			ErrorEntry err = errors.get(i);
			msgs.add(Translation.get(err.getId(), err.getErrors()));
		}
		errorPanel.setErrorMessages(msgs);
	}

	public void clearErrors(){
		errorPanel.clearErrorMessage();
	}

	@Override
	public void resetEditedFields(){
		tableJobs.resetChanges();
		setPreviewText("");
	}

	@Override
	public boolean isChanged(){
		stopCellEditing();
		return tableJobs.isChanged();
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] { OfflineJob.class });
		if (currentPath != null){
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, getTreeModel());
			tree.setSelectionPath(found);
		}
	}
}
