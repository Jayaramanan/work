/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.reports;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.controller.reports.ReportsController;
import com.ni3.ag.adminconsole.client.controller.reports.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACChangeableLabel;
import com.ni3.ag.adminconsole.client.view.common.ACComboBox;
import com.ni3.ag.adminconsole.client.view.common.ACTextArea;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.ReportTemplate;
import com.ni3.ag.adminconsole.domain.ReportType;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ReportsView extends JPanel implements AbstractView, ErrorRenderer{

	private static final long serialVersionUID = 1L;

	private ACTextArea templateTextArea;
	private ACButton addReportButton;
	private ACButton deleteReportButton;
	private ACButton updateReportButton;
	private ACButton refreshReportButton;
	private ACButton uploadThumbnailButton;
	private ACChangeableLabel imageIconLabel;
	private ACComboBox reportTypeCombo;
	private ACTree reportsTree;
	private ErrorPanel errorPanel;
	private byte[] preview;

	@Override
	public void initializeComponents(){
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		errorPanel = new ErrorPanel();
		add(errorPanel);

		JSplitPane mainSplit = new JSplitPane();
		springLayout.putConstraint(SpringLayout.WEST, mainSplit, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.NORTH, mainSplit, 0, SpringLayout.SOUTH, errorPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, mainSplit, 0, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, mainSplit, 0, SpringLayout.EAST, this);
		add(mainSplit);

		mainSplit.setDividerLocation((int) (ACMain.getScreenWidth() / 5));

		JPanel leftPanel = new JPanel();
		SpringLayout leftLayout = new SpringLayout();
		leftPanel.setLayout(leftLayout);
		ACToolBar leftBar = new ACToolBar();
		addReportButton = leftBar.makeAddButton();
		deleteReportButton = leftBar.makeDeleteButton();
		leftLayout.putConstraint(SpringLayout.WEST, leftBar, 10, SpringLayout.WEST, leftPanel);
		leftPanel.add(leftBar);

		JScrollPane treeScroll = new JScrollPane();
		reportsTree = new ACTree();
		reportsTree.setCellRenderer(new ACTreeCellRenderer());
		reportsTree.setCellEditor(new SchemaTreeCellEditor(reportsTree));
		reportsTree.setEditable(true);
		treeScroll.setViewportView(reportsTree);
		leftLayout.putConstraint(SpringLayout.NORTH, treeScroll, 0, SpringLayout.SOUTH, leftBar);
		leftLayout.putConstraint(SpringLayout.SOUTH, treeScroll, -10, SpringLayout.SOUTH, leftPanel);
		leftLayout.putConstraint(SpringLayout.EAST, treeScroll, -10, SpringLayout.EAST, leftPanel);
		leftLayout.putConstraint(SpringLayout.WEST, treeScroll, 10, SpringLayout.WEST, leftPanel);
		leftPanel.add(treeScroll);
		mainSplit.setLeftComponent(leftPanel);

		JPanel rightPanel = new JPanel();
		mainSplit.setRightComponent(rightPanel);
		SpringLayout rightPanelLayout = new SpringLayout();
		rightPanel.setLayout(rightPanelLayout);
		JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightSplit.setBorder(BorderFactory.createEmptyBorder());
		rightSplit.setDividerLocation((int) (ACMain.getScreenHeight() / 1.5));
		rightPanel.add(rightSplit);

		rightPanelLayout.putConstraint(SpringLayout.NORTH, rightSplit, 0, SpringLayout.NORTH, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.WEST, rightSplit, 10, SpringLayout.WEST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.EAST, rightSplit, -10, SpringLayout.EAST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.SOUTH, rightSplit, -10, SpringLayout.SOUTH, rightPanel);

		JPanel topPanel = new JPanel();
		SpringLayout topLayout = new SpringLayout();
		topPanel.setLayout(topLayout);
		rightSplit.setTopComponent(topPanel);

		ACToolBar rightBar = new ACToolBar();
		updateReportButton = rightBar.makeUpdateButton();
		refreshReportButton = rightBar.makeRefreshButton();
		topLayout.putConstraint(SpringLayout.WEST, rightBar, 0, SpringLayout.WEST, topPanel);
		topLayout.putConstraint(SpringLayout.NORTH, rightBar, 0, SpringLayout.NORTH, topPanel);
		topPanel.add(rightBar);

		reportTypeCombo = new ACComboBox(ReportType.values());
		reportTypeCombo.setEnabled(false);
		topPanel.add(reportTypeCombo);
		reportTypeCombo.setRenderer(new ReportTypeListCellRenderer());

		topLayout.putConstraint(SpringLayout.WEST, reportTypeCombo, 100, SpringLayout.WEST, rightBar);
		topLayout.putConstraint(SpringLayout.NORTH, reportTypeCombo, 10, SpringLayout.SOUTH, rightBar);
		topLayout.putConstraint(SpringLayout.EAST, reportTypeCombo, 200, SpringLayout.WEST, reportTypeCombo);

		JLabel reportTypeLabel = new JLabel(Translation.get(TextID.ReportType));
		topPanel.add(reportTypeLabel);

		topLayout.putConstraint(SpringLayout.EAST, reportTypeLabel, -10, SpringLayout.WEST, reportTypeCombo);
		topLayout.putConstraint(SpringLayout.NORTH, reportTypeLabel, 3, SpringLayout.NORTH, reportTypeCombo);

		templateTextArea = new ACTextArea();
		templateTextArea.setEditable(false);
		JScrollPane templateTAreaScroll = new JScrollPane(templateTextArea);
		topLayout.putConstraint(SpringLayout.WEST, templateTAreaScroll, 0, SpringLayout.WEST, rightBar);
		topLayout.putConstraint(SpringLayout.NORTH, templateTAreaScroll, 10, SpringLayout.SOUTH, reportTypeCombo);
		topLayout.putConstraint(SpringLayout.SOUTH, templateTAreaScroll, -10, SpringLayout.SOUTH, topPanel);
		topLayout.putConstraint(SpringLayout.EAST, templateTAreaScroll, -10, SpringLayout.EAST, topPanel);
		topPanel.add(templateTAreaScroll);

		JPanel bottomPanel = new JPanel();
		SpringLayout bottomLayout = new SpringLayout();
		bottomPanel.setLayout(bottomLayout);
		rightSplit.setBottomComponent(bottomPanel);
		ACToolBar bottomBar = new ACToolBar();
		uploadThumbnailButton = bottomBar.makeAddIconButton();
		bottomLayout.putConstraint(SpringLayout.WEST, bottomBar, 10, SpringLayout.WEST, bottomPanel);
		bottomLayout.putConstraint(SpringLayout.NORTH, bottomBar, 0, SpringLayout.NORTH, bottomPanel);
		bottomPanel.add(bottomBar);
		imageIconLabel = new ACChangeableLabel();
		bottomLayout.putConstraint(SpringLayout.WEST, imageIconLabel, 10, SpringLayout.WEST, bottomPanel);
		bottomLayout.putConstraint(SpringLayout.NORTH, imageIconLabel, 0, SpringLayout.SOUTH, bottomBar);
		bottomPanel.add(imageIconLabel);

	}

	@Override
	public boolean isChanged(){
		return templateTextArea.isChanged() || imageIconLabel.isChanged() || reportTypeCombo.isChanged();
	}

	@Override
	public void resetEditedFields(){
		templateTextArea.resetChanges();
		imageIconLabel.resetChanges();
		reportTypeCombo.resetChanges();
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] { ReportTemplate.class });
		if (currentPath != null){
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, getTreeModel());
			reportsTree.setSelectionPath(found);
		}
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

	public void setTreeModel(ReportsTreeModel treeModel){
		reportsTree.setModel(treeModel);
	}

	public void setTreeController(ReportsController reportsController){
		reportsTree.setCurrentController(reportsController);
	}

	public void addTreeEditorReportTemplateNameListener(CellEditorListener listener){
		reportsTree.getCellEditor().addCellEditorListener(listener);
	}

	public void addUpdateButtonListener(ActionListener listener){
		updateReportButton.addActionListener(listener);
	}

	public void addUploadThumbnailButtonListener(ActionListener listener){
		uploadThumbnailButton.addActionListener(listener);
	}

	public void addRefreshReportButtonListener(ActionListener listener){
		refreshReportButton.addActionListener(listener);
	}

	public void clearErrors(){
		errorPanel.clearErrorMessage();
	}

	public TreePath getTreeSelectionPath(){
		return reportsTree.getSelectionPath();
	}

	public ReportsTreeModel getTreeModel(){
		return (ReportsTreeModel) reportsTree.getModel();
	}

	public void setTreeSelectionPath(TreePath found){
		reportsTree.setSelectionPath(found);
	}

	public void addAddReportTemplateButtonListener(ActionListener listener){
		addReportButton.addActionListener(listener);
	}

	public void addTreeSelectionListener(SchemaTreeSelectionListener listener){
		reportsTree.addTreeSelectionListener(listener);
	}

	public void addDeleteReportButtonListener(ActionListener listener){
		deleteReportButton.addActionListener(listener);
	}

	public void updateTree(Map<DatabaseInstance, List<ReportTemplate>> map){
		ReportsTreeModel tModel = (ReportsTreeModel) getTreeModel();
		tModel.setReportMap(map);
		reportsTree.updateUI();
	}

	public String getXML(){
		return templateTextArea.getText();
	}

	public void setXML(String xml){
		templateTextArea.setText(xml);
	}

	public ReportType getReportType(){
		final Object type = reportTypeCombo.getSelectedItem();
		return (ReportType) type;
	}

	public void setReportType(ReportType type){
		reportTypeCombo.setSelectedItem(type);
	}

	public byte[] getPreview(){
		return preview;
	}

	private void setPreview(byte[] icon){
		if (icon != null){
			preview = new byte[icon.length];
			System.arraycopy(icon, 0, preview, 0, icon.length);
		} else
			preview = null;
	}

	public void setIcon(byte[] icon){
		if (icon == null)
			imageIconLabel.setIcon(null);
		else
			imageIconLabel.setIcon(new ImageIcon(icon));
		setPreview(icon);
	}

	public void setStartIcon(byte[] icon){
		if (icon == null)
			imageIconLabel.setStartIcon(null);
		else
			imageIconLabel.setStartIcon(new ImageIcon(icon));
		setPreview(icon);
	}

	public void setXMLEnabled(boolean b){
		templateTextArea.setEditable(b);
		reportTypeCombo.setEnabled(b);
	}

}
