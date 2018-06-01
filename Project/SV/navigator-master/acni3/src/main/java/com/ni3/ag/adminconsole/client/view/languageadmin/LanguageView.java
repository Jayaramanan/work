/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.languageadmin;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreePath;


import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACTable;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.dto.ErrorEntry;


public class LanguageView extends JPanel implements AbstractView, ErrorRenderer{

	private static final long serialVersionUID = 1L;
	private JSplitPane splitPane;
	private JPanel rightPanel;
	private LanguageLeftPanel leftPanel;
	private ACTable tableLanguage;

	private ACButton addButton;
	private ACButton updateButton;
	private ACButton deleteButton;
	private ACButton cancelButton;

	private ACToolBar toolBar;

	private ErrorPanel errorPanel;

	private LanguageView(){
	}

	public void initializeComponents(){
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		errorPanel = new ErrorPanel();
		add(errorPanel);

		splitPane = new JSplitPane();
		springLayout.putConstraint(SpringLayout.WEST, splitPane, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.NORTH, splitPane, 0, SpringLayout.SOUTH, errorPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, splitPane, 0, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.EAST, this);
		add(splitPane);

		leftPanel = new LanguageLeftPanel();
		splitPane.setLeftComponent(leftPanel);

		rightPanel = new JPanel();
		splitPane.setRightComponent(rightPanel);

		splitPane.setDividerLocation((int) (ACMain.getScreenWidth() / 5));

		SpringLayout rightPanelLayout = new SpringLayout();
		rightPanel.setLayout(rightPanelLayout);

		toolBar = new ACToolBar();
		addButton = toolBar.makeAddButton();
		deleteButton = toolBar.makeDeleteButton();
		updateButton = toolBar.makeUpdateButton();
		cancelButton = toolBar.makeRefreshButton();
		rightPanel.add(toolBar);
		rightPanelLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, rightPanel);

		JScrollPane scrollPane = new JScrollPane();
		tableLanguage = new ACTable();
		tableLanguage.enableCopyPaste();
		tableLanguage.enableToolTips();
		scrollPane.setViewportView(tableLanguage);
		rightPanel.add(scrollPane);

		rightPanelLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.SOUTH, toolBar);
		rightPanelLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, rightPanel);
		tableLanguage.setDefaultRenderer(String.class, new SimpleStringRenderer());
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
		cancelButton.addActionListener(actionListener);
	}

	public void setTableModel(LanguageTableModel model){
		tableLanguage.setModel(model);
		tableLanguage.setRowSorter(new TableRowSorter<LanguageTableModel>(model));
	}

	public LanguageTableModel getTableModel(){
		return (LanguageTableModel) tableLanguage.getModel();
	}

	public LanguageLeftPanel getLeftPanel(){
		return leftPanel;
	}

	public void refreshTable(){
		getTableModel().fireTableDataChanged();
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

	public void stopCellEditing(){
		if (tableLanguage.isEditing())
			tableLanguage.getCellEditor().stopCellEditing();
	}

	public void setActiveTableRow(int index){
		if (index < 0 || index >= tableLanguage.getModel().getRowCount())
			return;
		index = tableLanguage.convertRowIndexToView(index);
		if (index < 0 || index >= tableLanguage.getRowCount())
			return;
		tableLanguage.getSelectionModel().setSelectionInterval(index, index);
		Rectangle r = tableLanguage.getCellRect(index, 0, true);
		tableLanguage.scrollRectToVisible(r);
		tableLanguage.requestFocusInWindow();
	}

	public int getSelectedRowModelIndex(){
		if (tableLanguage.getSelectedRow() >= 0){
			return tableLanguage.convertRowIndexToModel(tableLanguage.getSelectedRow());
		}
		return -1;
	}

	@Override
	public void resetEditedFields(){
		tableLanguage.resetChanges();
	}

	@Override
	public boolean isChanged(){
		stopCellEditing();
		return tableLanguage.isChanged();
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] { Language.class });
		if (currentPath != null){
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, getLeftPanel().getTreeModel());
			getLeftPanel().setSelectionTreePath(found);
		}
	}

	public LanguageTableModel getTrableModel(){
		if (!(tableLanguage.getModel() instanceof LanguageTableModel))
			return null;
		return (LanguageTableModel) tableLanguage.getModel();
	}

	public UserLanguageProperty getSelectedLanguageProperty(){
		if (!(tableLanguage.getModel() instanceof LanguageTableModel))
			return null;

		LanguageTableModel model = (LanguageTableModel) tableLanguage.getModel();
		if (model == null)
			return null;
		int index = getSelectedRowModelIndex();
		if (index < 0)
			return null;
		return model.getSelected(index);
	}

	public void setSelectedLanguageProperty(UserLanguageProperty property){
		if (!(tableLanguage.getModel() instanceof LanguageTableModel))
			return;

		LanguageTableModel model = (LanguageTableModel) tableLanguage.getModel();
		if (model == null)
			return;

		int index = model.indexOf(property);
		if (index < 0)
			return;
		index = tableLanguage.convertRowIndexToView(index);
		tableLanguage.getSelectionModel().setSelectionInterval(index, index);
	}
}