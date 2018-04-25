/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses.navigator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreeModel;
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
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;


public class NavigatorLicenseView extends JPanel implements AbstractView, ErrorRenderer{
	private static final long serialVersionUID = 1L;
	private ACTree tree;
	private ACTable userTable;
	private ACButton updateButton;
	private ACButton refreshButton;
	private ErrorPanel errorPanel;

	private NavigatorLicenseView(){
	}

	public void initializeComponents(){
		SpringLayout mainLayout = new SpringLayout();
		setLayout(mainLayout);

		errorPanel = new ErrorPanel();
		add(errorPanel);

		JSplitPane mainSplit = new JSplitPane();
		mainLayout.putConstraint(SpringLayout.NORTH, mainSplit, 0, SpringLayout.SOUTH, errorPanel);
		mainLayout.putConstraint(SpringLayout.WEST, mainSplit, 0, SpringLayout.WEST, this);
		mainLayout.putConstraint(SpringLayout.EAST, mainSplit, 0, SpringLayout.EAST, this);
		mainLayout.putConstraint(SpringLayout.SOUTH, mainSplit, 0, SpringLayout.SOUTH, this);
		add(mainSplit);

		tree = new ACTree();
		JScrollPane treeScroll = new JScrollPane();
		tree.setExpandsSelectedPaths(true);
		treeScroll.setViewportView(tree);

		JPanel leftPanel = new JPanel();
		SpringLayout leftPanelLayout = new SpringLayout();
		leftPanel.setLayout(leftPanelLayout);

		leftPanelLayout.putConstraint(SpringLayout.WEST, treeScroll, 10, SpringLayout.WEST, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.NORTH, treeScroll, 10, SpringLayout.NORTH, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.SOUTH, treeScroll, -10, SpringLayout.SOUTH, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.EAST, treeScroll, -10, SpringLayout.EAST, leftPanel);

		mainSplit.setLeftComponent(leftPanel);
		leftPanel.add(treeScroll);

		userTable = new ACTable();
		userTable.enableCopyPaste();
		userTable.enableToolTips();
		JScrollPane userTableScroll = new JScrollPane();
		userTableScroll.getViewport().add(userTable);

		JPanel rightPanel = new JPanel();
		mainSplit.setRightComponent(rightPanel);
		SpringLayout rightPanelLayout = new SpringLayout();
		rightPanel.setLayout(rightPanelLayout);

		ACToolBar toolBar = new ACToolBar();
		updateButton = toolBar.makeUpdateButton();
		refreshButton = toolBar.makeRefreshButton();
		rightPanel.add(toolBar);
		rightPanelLayout.putConstraint(SpringLayout.NORTH, toolBar, 0, SpringLayout.NORTH, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, rightPanel);

		BufferedImage img = new BufferedImage(25, 10, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(Color.yellow);
		g.fillRect(0, 0, 25, 10);
		img.flush();
		String legendText = Translation.get(TextID.ModuleWillExpireSoon);
		JLabel legendLabel = new JLabel(legendText, new ImageIcon(img), JLabel.TRAILING);
		rightPanel.add(legendLabel);
		rightPanelLayout.putConstraint(SpringLayout.NORTH, legendLabel, 10, SpringLayout.NORTH, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.EAST, legendLabel, -10, SpringLayout.EAST, rightPanel);

		rightPanel.add(userTableScroll);
		rightPanelLayout.putConstraint(SpringLayout.NORTH, userTableScroll, 0, SpringLayout.SOUTH, toolBar);
		rightPanelLayout.putConstraint(SpringLayout.WEST, userTableScroll, 10, SpringLayout.WEST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.EAST, userTableScroll, -10, SpringLayout.EAST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.SOUTH, userTableScroll, -10, SpringLayout.SOUTH, rightPanel);

		mainSplit.setDividerLocation((int) (ACMain.getScreenWidth() / 5));

		tree.setCellRenderer(new ACTreeCellRenderer());
		NavigatorLicenseBooleanCellRenderer renderer = new NavigatorLicenseBooleanCellRenderer();
		userTable.setDefaultRenderer(Boolean.class, renderer);
		userTable.setDefaultEditor(Boolean.class, userTable.getDefaultEditor(Boolean.class));
	}

	public void renderErrors(List<ErrorEntry> errors){
		if (errors == null)
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

	public void clearErrors(){
		errorPanel.clearErrorMessage();
	}

	public JTree getTree(){
		return tree;
	}

	public JTable getUserTable(){
		return userTable;
	}

	public void refreshTable(){
		getTableModel().fireTableDataChanged();
	}

	public UserEditionTableModel getTableModel(){
		TableModel model = userTable.getModel();
		return (model instanceof UserEditionTableModel) ? (UserEditionTableModel) model : null;
	}

	public JButton getUpdateButton(){
		return updateButton;
	}

	public JButton getRefreshButton(){
		return refreshButton;
	}

	public void setActiveTableRow(User user){
		if (user == null){
			return;
		}
		UserEditionTableModel model = getTableModel();
		int modelIndex = model.indexOf(user);
		if (modelIndex >= 0){
			userTable.setSelectedCellIndexes(new int[] { modelIndex, modelIndex, 0, 0 });

			int index = userTable.convertRowIndexToView(modelIndex);
			if (index >= 0){
				Rectangle r = userTable.getCellRect(index, 0, true);
				userTable.scrollRectToVisible(r);
			}
		}

		userTable.requestFocusInWindow();
	}

	public int getSelectedModelIndex(){
		if (userTable.getSelectedRow() >= 0){
			return userTable.convertRowIndexToModel(userTable.getSelectedRow());
		}
		return -1;
	}

	public void stopCellEditing(){
		if (userTable.isEditing()){
			userTable.getCellEditor().stopCellEditing();
		}
	}

	@Override
	public void resetEditedFields(){
		userTable.resetChanges();
	}

	public void setTableModel(UserEditionTableModel tableModel){
		userTable.setModel(tableModel);
		userTable.setRowSorter(new TableRowSorter<UserEditionTableModel>(tableModel));
		updateTableHeaders();
	}

	@Override
	public boolean isChanged(){
		stopCellEditing();
		return userTable.isChanged();
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] current = holder.getMaxPath(new Class<?>[] { Group.class });
		if (current != null){
			TreeModelSupport treeSupport = new TreeModelSupport();
			TreePath found = treeSupport.findPathByNodes(current, getTree().getModel());
			getTree().setSelectionPath(found);
		}
	}

	public TreeModel getTreeModel(){
		return tree.getModel();
	}

	public void setNavigatorLicenseTreeModel(NavigatorLicenseTreeModel treeModel){
		tree.setModel(treeModel);
	}

	public User getSelectedUser(){
		return getTableModel().getSelected(getSelectedModelIndex());
	}

	public void addTableCellSelectionListener(MouseListener listener){
		userTable.addMouseListener(listener);
	}

	public void updateTree(){
		tree.updateUI();
	}

	public void updateTableHeaders(){
		for (int col = 1; col < userTable.getColumnCount(); col++){
			TableColumn tc = userTable.getColumnModel().getColumn(col);
			tc.setHeaderRenderer(new LicenseHeaderRenderer());
		}
	}
}
