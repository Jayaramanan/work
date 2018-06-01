/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.diag;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.controller.diag.DiagnoseButtonListener;
import com.ni3.ag.adminconsole.client.controller.diag.DiagnosticsController;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;

public class DiagnosticsView extends JPanel implements AbstractView, ErrorRenderer{
	private static final long serialVersionUID = 1L;
	private JSplitPane mainSplit;
	private ErrorPanel errorPanel;
	private ACToolBar toolBar;
	private ACButton diagnoseButton;
	private ACTree schemasTree;
	private JTable taskTable;

	@Override
	public void initializeComponents(){
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		errorPanel = new ErrorPanel();
		add(errorPanel);

		mainSplit = new JSplitPane();
		layout.putConstraint(SpringLayout.NORTH, mainSplit, 0, SpringLayout.SOUTH, errorPanel);
		layout.putConstraint(SpringLayout.WEST, mainSplit, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, mainSplit, 0, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, mainSplit, 0, SpringLayout.SOUTH, this);
		add(mainSplit);
		mainSplit.setDividerLocation((int) (ACMain.getScreenWidth() / 5));
		mainSplit.setLeftComponent(createLeftPanel());
		mainSplit.setRightComponent(createRightPanel());
	}

	private JPanel createLeftPanel(){
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		toolBar = new ACToolBar();
		diagnoseButton = toolBar.makeDiagnosticButton();
		panel.add(toolBar);

		JScrollPane treeScroll = new JScrollPane();
		layout.putConstraint(SpringLayout.NORTH, treeScroll, 0, SpringLayout.SOUTH, toolBar);
		layout.putConstraint(SpringLayout.WEST, treeScroll, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, treeScroll, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, treeScroll, -10, SpringLayout.SOUTH, panel);
		panel.add(treeScroll);

		schemasTree = new ACTree();
		schemasTree.setExpandsSelectedPaths(true);
		schemasTree.setCellRenderer(new ACTreeCellRenderer());
		treeScroll.setViewportView(schemasTree);
		return panel;
	}

	private JPanel createRightPanel(){
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		taskTable = new JTable();
		taskTable.setDefaultRenderer(TaskButton.class, new FixButtonCellRenderer());
		taskTable.setDefaultEditor(TaskButton.class, new FixButtonCellEditor());
		taskTable.setDefaultRenderer(DiagnoseTaskResult.class, new DiagnoseTaskStatusRenderer());
		taskTable.setDefaultRenderer(String.class, new MultilineStringCellRenderer());
		JScrollPane tableScroll = new JScrollPane();
		tableScroll.setViewportView(taskTable);
		taskTable.setRowHeight(33);

		layout.putConstraint(SpringLayout.NORTH, tableScroll, 10, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, tableScroll, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, tableScroll, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, tableScroll, -10, SpringLayout.SOUTH, panel);
		panel.add(tableScroll);

		return panel;
	}

	@Override
	public void resetEditedFields(){
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isChanged(){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] { Schema.class });
		if (currentPath != null){
			AbstractTreeModel treeModel = (AbstractTreeModel) schemasTree.getModel();
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, treeModel);
			schemasTree.setSelectionPath(found);
		}
	}

	@Override
	public void renderErrors(List<ErrorEntry> errors){
		if (errors == null || errors.isEmpty())
			errorPanel.setErrorMessages(null);
		else{
			List<String> msgs = new ArrayList<String>();
			for (int i = 0; i < errors.size(); i++){
				ErrorEntry err = errors.get(i);
				msgs.add(Translation.get(err.getId(), err.getErrors()));
			}
			errorPanel.setErrorMessages(msgs);
		}
	}

	public void setTreeModel(SchemaTreeModel schemaTreeModel){
		schemasTree.setModel(schemaTreeModel);
	}

	public void setTreeController(DiagnosticsController diagnosticsController){
		schemasTree.setCurrentController(diagnosticsController);
	}

	public void updateTree(){
		schemasTree.updateUI();
	}

	public void setTableModel(TaskTableModel taskTableModel){
		taskTable.setModel(taskTableModel);
		taskTable.setRowSorter(new TableRowSorter<TaskTableModel>(taskTableModel));
	}

	public void setTableWidths(int[] widths){
		for (int i = 0; i < widths.length; i++)
			taskTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
	}

	public void addTreeListener(TreeSelectionListener listener){
		schemasTree.addTreeSelectionListener(listener);
	}

	public void addDiagnoseButtonListener(DiagnoseButtonListener diagnoseButtonListener){
		diagnoseButton.addActionListener(diagnoseButtonListener);
	}

	public void updateTable(){
		TaskTableModel model = (TaskTableModel) taskTable.getModel();
		model.fireTableDataChanged();
		taskTable.repaint();
	}

	public void updateTableForce(){
		updateTable();
		taskTable.paintImmediately(0, 0, taskTable.getWidth(), taskTable.getHeight());
	}

	public void disableCurrentButton(){
		int index = taskTable.getSelectedRow();
		index = taskTable.convertRowIndexToModel(index);
		if (index == -1)
			return;
		TaskTableModel m = (TaskTableModel) taskTable.getModel();
		TaskButton b = (TaskButton) m.getValueAt(index, TaskTableModel.BUTTON_COLUMN_INDEX);
		b.setEnabled(b.isEnabled());
		b.repaint();
	}

	public void updateTableModel(DiagnoseTaskResult result){
		TaskTableModel ttm = (TaskTableModel) taskTable.getModel();
		ttm.replaceResult(result);
	}
}
