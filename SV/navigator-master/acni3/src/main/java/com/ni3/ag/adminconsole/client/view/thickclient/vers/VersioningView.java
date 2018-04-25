/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.vers;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.controller.thickclient.vers.VersioningGroupTreeListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACTable;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.domain.ModuleUser;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class VersioningView extends JPanel implements AbstractView, ErrorRenderer{
	private static final long serialVersionUID = -3524484883739780491L;
	private ACTree groupsTree;
	private ErrorPanel errorPanel;
	private ACToolBar userModulesToolBar;
	private ACToolBar moduleToolBar;
	private ACTable userModuleTable;
	private ACTable moduleTable;
	private ACButton addModuleButton;
	private ACButton deleteModuleButton;
	private ACButton refreshModuleButton;
	private ACButton saveModuleButton;
	private ACButton refreshUserModuleButton;
	private ACButton saveUserModuleButton;
	private ACButton uploadModuleButton;
	private ACButton downloadModuleButton;
	private ACButton sendButton;
	private ACButton sendSSOButton;

	@Override
	public void initializeComponents(){
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		errorPanel = new ErrorPanel();
		add(errorPanel);

		JSplitPane mainSplit = new JSplitPane();
		layout.putConstraint(SpringLayout.NORTH, mainSplit, 10, SpringLayout.SOUTH, errorPanel);
		layout.putConstraint(SpringLayout.WEST, mainSplit, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, mainSplit, 0, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, mainSplit, 0, SpringLayout.SOUTH, this);
		add(mainSplit);

		JPanel leftPanel = new JPanel();
		SpringLayout leftLayout = new SpringLayout();
		leftPanel.setLayout(leftLayout);

		groupsTree = new ACTree();
		groupsTree.setExpandsSelectedPaths(true);
		JScrollPane treeScroll = new JScrollPane();
		treeScroll.setViewportView(groupsTree);
		leftLayout.putConstraint(SpringLayout.NORTH, treeScroll, 10, SpringLayout.NORTH, leftPanel);
		leftLayout.putConstraint(SpringLayout.WEST, treeScroll, 10, SpringLayout.WEST, leftPanel);
		leftLayout.putConstraint(SpringLayout.EAST, treeScroll, -10, SpringLayout.EAST, leftPanel);
		leftLayout.putConstraint(SpringLayout.SOUTH, treeScroll, -10, SpringLayout.SOUTH, leftPanel);
		leftPanel.add(treeScroll);

		JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		JPanel topPanel = new JPanel();
		SpringLayout topLayout = new SpringLayout();
		topPanel.setLayout(topLayout);

		userModulesToolBar = new ACToolBar();
		topLayout.putConstraint(SpringLayout.NORTH, userModulesToolBar, 0, SpringLayout.NORTH, topPanel);
		topLayout.putConstraint(SpringLayout.WEST, userModulesToolBar, 10, SpringLayout.WEST, topPanel);
		saveUserModuleButton = userModulesToolBar.makeUpdateButton();
		refreshUserModuleButton = userModulesToolBar.makeRefreshButton();
		sendButton = userModulesToolBar.makeSendButton();
		sendSSOButton = userModulesToolBar.makeSendSSOButton();
		topPanel.add(userModulesToolBar);

		userModuleTable = new ACTable();
		JScrollPane userModuleTableScroll = new JScrollPane();
		userModuleTableScroll.setViewportView(userModuleTable);
		topLayout.putConstraint(SpringLayout.NORTH, userModuleTableScroll, 0, SpringLayout.SOUTH, userModulesToolBar);
		topLayout.putConstraint(SpringLayout.WEST, userModuleTableScroll, 10, SpringLayout.WEST, topPanel);
		topLayout.putConstraint(SpringLayout.EAST, userModuleTableScroll, -10, SpringLayout.EAST, topPanel);
		topLayout.putConstraint(SpringLayout.SOUTH, userModuleTableScroll, -10, SpringLayout.SOUTH, topPanel);
		topPanel.add(userModuleTableScroll);

		JPanel bottomPanel = new JPanel();
		SpringLayout bottomLayout = new SpringLayout();
		bottomPanel.setLayout(bottomLayout);
		moduleToolBar = new ACToolBar();
		addModuleButton = moduleToolBar.makeAddButton();
		deleteModuleButton = moduleToolBar.makeDeleteButton();
		saveModuleButton = moduleToolBar.makeUpdateButton2();
		refreshModuleButton = moduleToolBar.makeRefreshButton2();
		uploadModuleButton = moduleToolBar.makeUploadButton();
		downloadModuleButton = moduleToolBar.makeDownloadButton();
		bottomLayout.putConstraint(SpringLayout.NORTH, moduleToolBar, 0, SpringLayout.NORTH, bottomPanel);
		bottomLayout.putConstraint(SpringLayout.WEST, moduleToolBar, 10, SpringLayout.WEST, bottomPanel);
		bottomPanel.add(moduleToolBar);

		JScrollPane moduleTableScroll = new JScrollPane();
		moduleTable = new ACTable();
		moduleTable.enableToolTips();
		moduleTableScroll.setViewportView(moduleTable);
		bottomLayout.putConstraint(SpringLayout.NORTH, moduleTableScroll, 10, SpringLayout.SOUTH, moduleToolBar);
		bottomLayout.putConstraint(SpringLayout.WEST, moduleTableScroll, 10, SpringLayout.WEST, bottomPanel);
		bottomLayout.putConstraint(SpringLayout.EAST, moduleTableScroll, -10, SpringLayout.EAST, bottomPanel);
		bottomLayout.putConstraint(SpringLayout.SOUTH, moduleTableScroll, -10, SpringLayout.SOUTH, bottomPanel);
		bottomPanel.add(moduleTableScroll);

		rightSplit.setTopComponent(topPanel);
		rightSplit.setBottomComponent(bottomPanel);

		mainSplit.setLeftComponent(leftPanel);
		mainSplit.setRightComponent(rightSplit);
		mainSplit.setDividerLocation((int) (ACMain.getScreenWidth() / 5));
		rightSplit.setDividerLocation((int) ((ACMain.getScreenHeight() - 50) / 2));
		groupsTree.setCellRenderer(new ACTreeCellRenderer());
		moduleTable.enableCopyPaste();
		userModuleTable.enableCopyPaste();
		userModuleTable.setDefaultRenderer(User.class, new UserCellRenderer());
		userModuleTable.setDefaultRenderer(ModuleUser.class, new UserModuleCellRenderer());
		userModuleTable.setDefaultEditor(ModuleUser.class, new UserModuleCellEditor());
		moduleTable.setDefaultRenderer(String.class, new ModuleTableCellRenderer(moduleTable
		        .getDefaultRenderer(String.class)));

	}

	public void setGroupTreeModel(TreeModel m){
		groupsTree.setModel(m);
	}

	@Override
	public void resetEditedFields(){
		moduleTable.resetChanges();
		userModuleTable.resetChanges();
	}

	@Override
	public boolean isChanged(){
		stopEditing();
		return isModuleTableChanged() || isUserModuleTableChanged();
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] { Group.class });
		if (currentPath != null){
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, groupsTree.getModel());
			groupsTree.setSelectionPath(found);
		}
	}

	public void setGroupTreeListener(VersioningGroupTreeListener versioningGroupTreeListener){
		groupsTree.getSelectionModel().addTreeSelectionListener(versioningGroupTreeListener);
	}

	public void updatePaths(List<String> paths){
		moduleTable.setDefaultEditor(String.class, new ModuleTableStringCellEditor(paths));
	}

	public void setModuleTableModelData(List<Module> modules, List<Group> groups, List<String> paths){
		ModuleTableModel currentModel = null;
		if (moduleTable.getModel() instanceof ModuleTableModel)
			currentModel = (ModuleTableModel) moduleTable.getModel();

		int index = -1;
		if (currentModel == null){
			currentModel = new ModuleTableModel(modules, groups);
			moduleTable.setModel(currentModel);
			moduleTable.setRowSorter(new TableRowSorter<ModuleTableModel>(currentModel));
		} else{
			index = moduleTable.getSelectedRow();
			Module m = null;
			if (index >= 0)
				m = currentModel.getModule(moduleTable.convertRowIndexToModel(index));

			currentModel.setModules(modules);

			if (m != null)
				index = currentModel.indexOf(m);
			if (index >= 0)
				index = moduleTable.convertRowIndexToView(index);
		}
		currentModel.fireTableDataChanged();
		if (index >= 0)
			moduleTable.getSelectionModel().setSelectionInterval(index, index);

		updatePaths(paths != null ? paths : new ArrayList<String>());
	}

	public ACTree getGroupTree(){
		return groupsTree;
	}

	public void setUserModuleTableModel(UserModuleTableModel userModuleTableModel){
		UserModuleTableModel currentModel = null;
		if (userModuleTable.getModel() instanceof UserModuleTableModel)
			currentModel = (UserModuleTableModel) userModuleTable.getModel();
		int index = userModuleTable.getSelectedRow();
		User u = null;
		if (currentModel != null && index >= 0)
			u = currentModel.getUser(userModuleTable.convertRowIndexToModel(index));
		userModuleTable.setModel(userModuleTableModel);
		userModuleTable.setRowSorter(new TableRowSorter<UserModuleTableModel>(userModuleTableModel));
		index = userModuleTableModel.indexOf(u);
		if (index >= 0)
			userModuleTable.getSelectionModel().setSelectionInterval(index, index);
	}

	public void setAddModuleButtonListener(ActionListener l){
		addModuleButton.addActionListener(l);
	}

	public void setDeleteModuleButtonListener(ActionListener l){
		deleteModuleButton.addActionListener(l);
	}

	public void setRefreshModuleButtonListener(ActionListener l){
		refreshModuleButton.addActionListener(l);
	}

	public void setSaveModuleButtonListener(ActionListener l){
		saveModuleButton.addActionListener(l);
	}

	public int getModuleTableSelectionIndex(){
		int index = moduleTable.getSelectedRow();
		if (index == -1)
			return -1;
		index = moduleTable.convertRowIndexToModel(index);
		return index;
	}

	public int getUserModuleTableSelectionIndex(){
		int index = userModuleTable.getSelectedRow();
		if (index == -1)
			return -1;
		index = userModuleTable.convertRowIndexToModel(index);
		return index;
	}

	public boolean isModuleTableChanged(){
		return moduleTable.isChanged();
	}

	public boolean isUserModuleTableChanged(){
		return userModuleTable.isChanged();
	}

	public void stopEditing(){
		if (moduleTable.isEditing())
			moduleTable.getCellEditor().stopCellEditing();
		if (userModuleTable.isEditing())
			userModuleTable.getCellEditor().stopCellEditing();
	}

	public void renderErrors(List<ErrorEntry> list){
		List<String> errorMsgs = new ArrayList<String>();
		for (ErrorEntry error : list)
			errorMsgs.add(Translation.get(error.getId(), error.getErrors()));
		errorPanel.setErrorMessages(errorMsgs);
	}

	public void renderError(TextID error){
		List<String> errorMsgs = new ArrayList<String>();
		errorMsgs.add(Translation.get(error));
		errorPanel.setErrorMessages(errorMsgs);
	}

	public void clearErrors(){
		errorPanel.setErrorMessages(null);
	}

	public void setSelectedModule(int next){
		next = moduleTable.convertRowIndexToView(next);
		moduleTable.getSelectionModel().addSelectionInterval(next, next);
		Rectangle rect = moduleTable.getCellRect(next, 0, true);
		moduleTable.scrollRectToVisible(rect);
	}

	public Module getSelectedModule(){
		ModuleTableModel tModel = (ModuleTableModel) moduleTable.getModel();
		int index = this.getModuleTableSelectionIndex();
		return tModel.getModule(index);
	}

	public User getSelectedUser(){
		UserModuleTableModel tModel = (UserModuleTableModel) userModuleTable.getModel();
		int index = this.getUserModuleTableSelectionIndex();
		return tModel.getUser(index);
	}

	public void deleteRowsFromModuleTable(int start, int end){
		ModuleTableModel mtm = (ModuleTableModel) moduleTable.getModel();
		mtm.fireTableRowsDeleted(start, end);
	}

	public void updateModuleTable(){
		ModuleTableModel mtm = (ModuleTableModel) moduleTable.getModel();
		mtm.fireTableDataChanged();
	}

	public void setUserModuleRefreshButtonListener(ActionListener l){
		refreshUserModuleButton.addActionListener(l);
	}

	public void setUserModuleSaveButtonListener(ActionListener l){
		saveUserModuleButton.addActionListener(l);
	}

	public ModuleTableModel getModuleTableModel(){
		return (ModuleTableModel) moduleTable.getModel();
	}

	public void addUploadButtonListener(ActionListener al){
		this.uploadModuleButton.addActionListener(al);
	}

	public void addDownloadButtonListener(ActionListener al){
		this.downloadModuleButton.addActionListener(al);
	}

	public void addSendButtonListener(ActionListener al){
		sendButton.addActionListener(al);
	}

	public UserModuleTableModel getUserModuleTableModel(){
		return (UserModuleTableModel) userModuleTable.getModel();
	}

	public void setSelectedUser(User selected){
		UserModuleTableModel tModel = (UserModuleTableModel) userModuleTable.getModel();
		int index = tModel.indexOf(selected);
		if (index < 0)
			return;
		index = userModuleTable.convertRowIndexToView(index);
		userModuleTable.getSelectionModel().setSelectionInterval(index, index);
	}

	public void addSendSSOButtonListener(ActionListener al){
		sendSSOButton.addActionListener(al);
	}
}
