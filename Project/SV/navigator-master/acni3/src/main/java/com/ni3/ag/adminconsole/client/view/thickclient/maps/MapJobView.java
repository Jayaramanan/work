/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.maps;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
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
import com.ni3.ag.adminconsole.client.view.common.calendar.ACDateEditor;
import com.ni3.ag.adminconsole.client.view.common.calendar.ACDateRenderer;
import com.ni3.ag.adminconsole.client.view.common.calendar.ACCalendarDialog.DisplayType;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.thickclient.UserListCellRenderer;
import com.ni3.ag.adminconsole.client.view.thickclient.UserTableCellRenderer;
import com.ni3.ag.adminconsole.domain.Map;
import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.jobs.MapJobStatus;
import com.ni3.ag.adminconsole.shared.jobs.MapJobType;

public class MapJobView extends JPanel implements AbstractView, ErrorRenderer{

	private static final long serialVersionUID = 1L;
	private ACTree tree;
	private ACTable tableJobs;
	private ACButton addButton;
	private ACButton updateButton;
	private ACButton deleteButton;
	private ACButton refreshButton;
	private ACButton launchNowButton;
	private ACButton viewMapDir;
	private ACComboBox userCombo;
	private ACComboBox jobTypeCombo;
	private JSplitPane rightSplit;

	private ErrorPanel errorPanel;
	private GisPanel gisPanel;
	private JPanel emptyPanel;

	private MapJobView(){
	}

	public JPanel getEmptyPanel(){
		return emptyPanel;
	}

	public void initializeComponents(){
		gisPanel = new GisTiledRasterPanel(this);

		SpringLayout mainLayout = new SpringLayout();
		this.setLayout(mainLayout);

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
		launchNowButton = toolBar.makeLaunchNowButton();
		viewMapDir = toolBar.makeMapDirViewButton();
		JPanel rightTopPanel = new JPanel();
		rightTopPanel.add(toolBar);
		SpringLayout rightTopPanelLayout = new SpringLayout();
		rightTopPanel.setLayout(rightTopPanelLayout);

		JScrollPane scrollPaneJobs = new JScrollPane();
		tableJobs = new ACTable();
		tableJobs.enableCopyPaste();
		tableJobs.enableToolTips();
		scrollPaneJobs.setViewportView(tableJobs);
		rightTopPanel.add(scrollPaneJobs);

		rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightSplit.setDividerLocation((int) (ACMain.getScreenHeight() / 3));
		rightPanel.add(rightSplit);

		rightPanelLayout.putConstraint(SpringLayout.NORTH, rightSplit, 0, SpringLayout.NORTH, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.WEST, rightSplit, 10, SpringLayout.WEST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.EAST, rightSplit, -10, SpringLayout.EAST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.SOUTH, rightSplit, -10, SpringLayout.SOUTH, rightPanel);

		rightSplit.setTopComponent(rightTopPanel);
		rightSplit.setBorder(BorderFactory.createEmptyBorder());
		rightSplit.setResizeWeight(0.5);
		errorPanel = new ErrorPanel();
		this.add(errorPanel);

		this.add(splitPane);

		mainLayout.putConstraint(SpringLayout.WEST, splitPane, 0, SpringLayout.WEST, this);
		mainLayout.putConstraint(SpringLayout.NORTH, splitPane, 0, SpringLayout.SOUTH, errorPanel);
		mainLayout.putConstraint(SpringLayout.SOUTH, splitPane, 0, SpringLayout.SOUTH, this);
		mainLayout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.EAST, this);

		rightTopPanelLayout.putConstraint(SpringLayout.WEST, scrollPaneJobs, 0, SpringLayout.WEST, rightTopPanel);
		rightTopPanelLayout.putConstraint(SpringLayout.NORTH, scrollPaneJobs, 0, SpringLayout.SOUTH, toolBar);
		rightTopPanelLayout.putConstraint(SpringLayout.SOUTH, scrollPaneJobs, 0, SpringLayout.SOUTH, rightTopPanel);
		rightTopPanelLayout.putConstraint(SpringLayout.EAST, scrollPaneJobs, 0, SpringLayout.EAST, rightTopPanel);

		userCombo = new ACComboBox();
		userCombo.setRenderer(new UserListCellRenderer());
		tableJobs.setDefaultEditor(User.class, new ACCellEditor(userCombo));
		tableJobs.setDefaultRenderer(User.class, new UserTableCellRenderer());

		jobTypeCombo = new ACComboBox();
		tableJobs.setDefaultEditor(MapJobType.class, new ACCellEditor(jobTypeCombo));

		tableJobs.setDefaultRenderer(Date.class, new ACDateRenderer(DisplayType.DateTime));
		tableJobs.setDefaultEditor(Date.class, new ACDateEditor(DisplayType.DateTime));

		setJobTypeComboData();
	}

	public void setGisPanel(Component c){
		if (c instanceof GisPanel){
			GisPanel gp = (GisPanel) c;
			JSplitPane gisSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, gp.toolbar, gisPanel);
			gisSplit.setResizeWeight(0);
			rightSplit.setBottomComponent(gisSplit);
		} else{
			rightSplit.setBottomComponent(c);
		}
	}

	public void addTreeSelectionListener(TreeSelectionListener tsl){
		tree.addTreeSelectionListener(tsl);
	}

	public void addTableSelectionListener(ListSelectionListener listener){
		ListSelectionModel model = tableJobs.getSelectionModel();
		model.addListSelectionListener(listener);
	}

	public void setTreeModel(MapJobTreeModel model){
		tree.setModel(model);
	}

	private void setJobTypeComboData(){
		for (MapJobType jt : MapJobType.values()){
			jobTypeCombo.addItem(jt);
		}
	}

	public void setUserComboData(List<User> users){
		userCombo.removeAllItems();
		if (users == null){
			return;
		}
		for (User user : users){
			userCombo.addItem(user);
		}
	}

	public JTree getTree(){
		return tree;
	}

	public MapJobTreeModel getTreeModel(){
		return (MapJobTreeModel) tree.getModel();
	}

	public void setTableModel(MapJobTableModel model){
		tableJobs.setModel(model);
		tableJobs.setRowSorter(new TableRowSorter<MapJobTableModel>(model));
	}

	public MapJobTableModel getTableModel(){
		return (MapJobTableModel) tableJobs.getModel();
	}

	public void addUpdateButtonActionListener(ActionListener actionListener){
		updateButton.addActionListener(actionListener);
	}

	public void addViewMapDirActionListener(ActionListener listener){
		viewMapDir.addActionListener(listener);
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

	public MapJob getSelectedJob(){
		return getTableModel().getSelectedJob(getSelectedRowIndex());
	}

	public void setActiveTableRow(MapJob job){
		MapJobTableModel model = getTableModel();
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
	}

	@Override
	public boolean isChanged(){
		stopCellEditing();
		return tableJobs.isChanged();
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] { MapJob.class });
		if (currentPath != null){
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, getTreeModel());
			tree.setSelectionPath(found);
		}
	}

	public void setMapPanelData(Map map){
		if (gisPanel instanceof GisPanel){
			((GisPanel) gisPanel).forceRepaint();
		}
	}

	public GisPanel getGisPanel(){
		return gisPanel;
	}

	public void setCoordinatesFromMap(double fromX, double toX, double fromY, double toY){
		MapJob job = getSelectedJob();
		if (job != null && MapJobStatus.Scheduled.getValue().equals(job.getStatus())){
			job.setX1(new BigDecimal(fromX).setScale(8, BigDecimal.ROUND_HALF_UP));
			job.setX2(new BigDecimal(toX).setScale(8, BigDecimal.ROUND_HALF_UP));
			job.setY1(new BigDecimal(fromY).setScale(8, BigDecimal.ROUND_HALF_UP));
			job.setY2(new BigDecimal(toY).setScale(8, BigDecimal.ROUND_HALF_UP));
			int rowIndex = getSelectedRowIndex();
			if (rowIndex >= 0){
				getTableModel().fireTableRowsUpdated(rowIndex, rowIndex);
			}
		}
	}
}
