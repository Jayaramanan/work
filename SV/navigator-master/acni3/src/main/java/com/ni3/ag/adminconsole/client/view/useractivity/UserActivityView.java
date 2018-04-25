/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useractivity;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.controller.useractivity.FilterModeComboListener;
import com.ni3.ag.adminconsole.client.controller.useractivity.LinkButtonListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACComboBox;
import com.ni3.ag.adminconsole.client.view.common.ACLinkButton;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.common.HTMLPanel;
import com.ni3.ag.adminconsole.client.view.common.calendar.ACCalendarField;
import com.ni3.ag.adminconsole.client.view.common.treetable.ACTreeTable;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserActivity;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class UserActivityView extends JPanel implements AbstractView, ErrorRenderer{

	private static final long serialVersionUID = 1L;
	private ACTree tree;
	private ACButton searchButton;
	private ACButton xlsReportButton;
	private ACButton pdfReportButton;
	private ACButton htmlReportButton;
	private ACCalendarField fromDate;
	private ACCalendarField toDate;
	private ACTreeTable activityTable;
	private ACComboBox filterModeCombo;
	private ACComboBox filterCombo;
	private ACLinkButton todayLinkButton, thisWeekLinkButton, thisMonthLinkButton;
	private JLabel serverTimeLabel;
	private HTMLPanel summaryArea;
	private JLabel userActLabel;

	private ErrorPanel errorPanel;

	private UserActivityView(){
	}

	public void initializeComponents(){
		SpringLayout elementLayout = new SpringLayout();
		this.setLayout(elementLayout);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation((int) (ACMain.getScreenWidth() / 5));

		tree = new ACTree();
		tree.setCellRenderer(new ACTreeCellRenderer());
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

		ACToolBar toolBar = new ACToolBar();
		searchButton = toolBar.makeSearchActivitiesButton();
		toolBar.addSeparator();
		xlsReportButton = toolBar.makeXLSReportButton();
		pdfReportButton = toolBar.makePDFReportButton();
		htmlReportButton = toolBar.makeHTMLReportButton();

		JScrollPane scrollPaneJobs = new JScrollPane();
		activityTable = new ACTreeTable();
		activityTable.enableCopyPaste();
		scrollPaneJobs.setViewportView(activityTable);

		errorPanel = new ErrorPanel();
		this.add(errorPanel);

		this.add(splitPane);

		elementLayout.putConstraint(SpringLayout.WEST, splitPane, 0, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.NORTH, splitPane, 0, SpringLayout.SOUTH, errorPanel);
		elementLayout.putConstraint(SpringLayout.SOUTH, splitPane, 0, SpringLayout.SOUTH, this);
		elementLayout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.EAST, this);

		JLabel fromLabel = new JLabel(Translation.get(TextID.DateFrom));
		JLabel toLabel = new JLabel(Translation.get(TextID.DateTo));
		userActLabel = new JLabel(Translation.get(TextID.User));
		JLabel filterModeLabel = new JLabel(Translation.get(TextID.FilterMode));

		fromDate = new ACCalendarField();
		toDate = new ACCalendarField();

		filterModeCombo = new ACComboBox();
		filterCombo = new ACComboBox();

		JPanel linkPanel = createLinkPanel();

		summaryArea = new HTMLPanel();
		JScrollPane summaryScrollPane = new JScrollPane(summaryArea);

		JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightSplit.setDividerLocation((int) (ACMain.getScreenHeight() / 1.6));
		rightSplit.setBorder(BorderFactory.createEmptyBorder());

		JPanel rightPanel = new JPanel();
		splitPane.setRightComponent(rightPanel);
		rightPanel.add(rightSplit);

		JPanel rightTopPanel = new JPanel();
		SpringLayout rightTopLayout = new SpringLayout();
		rightTopPanel.setLayout(rightTopLayout);
		rightSplit.setTopComponent(rightTopPanel);

		JPanel summaryPanel = new JPanel();
		SpringLayout rightBottomLayout = new SpringLayout();
		summaryPanel.setLayout(rightBottomLayout);
		rightSplit.setBottomComponent(summaryPanel);

		rightTopPanel.add(toolBar);
		rightTopPanel.add(fromLabel);
		rightTopPanel.add(toLabel);
		rightTopPanel.add(userActLabel);
		rightTopPanel.add(filterModeLabel);
		rightTopPanel.add(fromDate);
		rightTopPanel.add(toDate);
		rightTopPanel.add(filterModeCombo);
		rightTopPanel.add(filterCombo);
		rightTopPanel.add(linkPanel);
		rightTopPanel.add(scrollPaneJobs);

		summaryPanel.add(summaryScrollPane);

		SpringLayout rightPanelLayout = new SpringLayout();
		rightPanel.setLayout(rightPanelLayout);

		rightPanelLayout.putConstraint(SpringLayout.WEST, rightSplit, 10, SpringLayout.WEST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.NORTH, rightSplit, 0, SpringLayout.NORTH, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.SOUTH, rightSplit, -10, SpringLayout.SOUTH, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.EAST, rightSplit, -10, SpringLayout.EAST, rightPanel);

		SpringLayout rightTopPanelLayout = new SpringLayout();
		rightTopPanel.setLayout(rightTopPanelLayout);

		rightTopPanelLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, rightTopPanel);

		rightTopPanelLayout.putConstraint(SpringLayout.WEST, fromDate, 80, SpringLayout.WEST, rightTopPanel);
		rightTopPanelLayout.putConstraint(SpringLayout.NORTH, fromDate, 10, SpringLayout.SOUTH, toolBar);
		rightTopPanelLayout.putConstraint(SpringLayout.EAST, fromDate, 170, SpringLayout.WEST, fromDate);

		rightTopPanelLayout.putConstraint(SpringLayout.NORTH, fromLabel, 2, SpringLayout.NORTH, fromDate);
		rightTopPanelLayout.putConstraint(SpringLayout.EAST, fromLabel, -5, SpringLayout.WEST, fromDate);

		rightTopPanelLayout.putConstraint(SpringLayout.WEST, toDate, 300, SpringLayout.WEST, rightTopPanel);
		rightTopPanelLayout.putConstraint(SpringLayout.NORTH, toDate, 0, SpringLayout.NORTH, fromDate);
		rightTopPanelLayout.putConstraint(SpringLayout.EAST, toDate, 170, SpringLayout.WEST, toDate);

		rightTopPanelLayout.putConstraint(SpringLayout.NORTH, toLabel, 2, SpringLayout.NORTH, toDate);
		rightTopPanelLayout.putConstraint(SpringLayout.EAST, toLabel, -5, SpringLayout.WEST, toDate);

		rightTopPanelLayout.putConstraint(SpringLayout.NORTH, linkPanel, 0, SpringLayout.NORTH, fromDate);
		rightTopPanelLayout.putConstraint(SpringLayout.WEST, linkPanel, 500, SpringLayout.WEST, rightTopPanel);
		rightTopPanelLayout.putConstraint(SpringLayout.EAST, linkPanel, 350, SpringLayout.WEST, linkPanel);

		rightTopPanelLayout.putConstraint(SpringLayout.WEST, filterModeCombo, 80, SpringLayout.WEST, rightTopPanel);
		rightTopPanelLayout.putConstraint(SpringLayout.NORTH, filterModeCombo, 10, SpringLayout.SOUTH, fromDate);
		rightTopPanelLayout.putConstraint(SpringLayout.EAST, filterModeCombo, 170, SpringLayout.WEST, filterModeCombo);

		rightTopPanelLayout.putConstraint(SpringLayout.NORTH, filterModeLabel, 2, SpringLayout.NORTH, filterModeCombo);
		rightTopPanelLayout.putConstraint(SpringLayout.EAST, filterModeLabel, -5, SpringLayout.WEST, filterModeCombo);

		rightTopPanelLayout.putConstraint(SpringLayout.WEST, filterCombo, 300, SpringLayout.WEST, rightTopPanel);
		rightTopPanelLayout.putConstraint(SpringLayout.NORTH, filterCombo, 10, SpringLayout.SOUTH, fromDate);
		rightTopPanelLayout.putConstraint(SpringLayout.EAST, filterCombo, 170, SpringLayout.WEST, filterCombo);

		rightTopPanelLayout.putConstraint(SpringLayout.NORTH, userActLabel, 2, SpringLayout.NORTH, filterCombo);
		rightTopPanelLayout.putConstraint(SpringLayout.EAST, userActLabel, -5, SpringLayout.WEST, filterCombo);

		rightTopPanelLayout.putConstraint(SpringLayout.WEST, scrollPaneJobs, 0, SpringLayout.WEST, rightTopPanel);
		rightTopPanelLayout.putConstraint(SpringLayout.NORTH, scrollPaneJobs, 20, SpringLayout.SOUTH, filterCombo);
		rightTopPanelLayout.putConstraint(SpringLayout.SOUTH, scrollPaneJobs, -10, SpringLayout.SOUTH, rightTopPanel);
		rightTopPanelLayout.putConstraint(SpringLayout.EAST, scrollPaneJobs, 0, SpringLayout.EAST, rightTopPanel);

		SpringLayout summaryPanelLayout = new SpringLayout();
		summaryPanel.setLayout(summaryPanelLayout);

		summaryPanelLayout.putConstraint(SpringLayout.WEST, summaryScrollPane, 0, SpringLayout.WEST, summaryPanel);
		summaryPanelLayout.putConstraint(SpringLayout.NORTH, summaryScrollPane, 10, SpringLayout.NORTH, summaryPanel);
		summaryPanelLayout.putConstraint(SpringLayout.SOUTH, summaryScrollPane, 0, SpringLayout.SOUTH, summaryPanel);
		summaryPanelLayout.putConstraint(SpringLayout.EAST, summaryScrollPane, 0, SpringLayout.EAST, summaryPanel);

		filterModeCombo.addItem(TextID.UserBased);
		filterModeCombo.addItem(TextID.ActionBased);
		filterModeCombo.setRenderer(new FilterModeComboRenderer());
		filterCombo.setRenderer(new FilterComboRenderer());
	}

	private JPanel createLinkPanel(){
		JPanel panel = new JPanel(new FlowLayout());
		todayLinkButton = new ACLinkButton(TextID.Today, 11);
		todayLinkButton.setActionCommand(TextID.Today.toString());
		panel.add(todayLinkButton, 0);
		thisWeekLinkButton = new ACLinkButton(TextID.ThisWeek, 11);
		thisWeekLinkButton.setActionCommand(TextID.ThisWeek.toString());
		panel.add(thisWeekLinkButton, 1);
		thisMonthLinkButton = new ACLinkButton(TextID.ThisMonth, 11);
		thisMonthLinkButton.setActionCommand(TextID.ThisMonth.toString());
		panel.add(thisMonthLinkButton, 2);
		serverTimeLabel = new JLabel(Translation.get(TextID.ServerTime));
		panel.add(serverTimeLabel, 3);
		return panel;
	}

	public void addTreeSelectionListener(TreeSelectionListener tsl){
		tree.addTreeSelectionListener(tsl);
	}

	public UserActivityTreeModel getTreeModel(){
		return (UserActivityTreeModel) tree.getModel();
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
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] {});
		if (currentPath != null){
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, getTreeModel());
			tree.setSelectionPath(found);
		}
	}

	@Override
	public boolean isChanged(){
		return false;
	}

	public JTree getTree(){
		return tree;
	}

	@Override
	public void resetEditedFields(){
	}

	public void addSearchButtonListener(ActionListener l){
		searchButton.addActionListener(l);
	}

	public void addReportsButtonListener(ActionListener l){
		xlsReportButton.addActionListener(l);
		pdfReportButton.addActionListener(l);
		htmlReportButton.addActionListener(l);
	}

	public void setTreeModel(UserActivityTreeModel model){
		tree.setModel(model);
	}

	public void setDateFrom(Date value){
		fromDate.setValue(value);
	}

	public void setDateTo(Date value){
		toDate.setValue(value);
	}

	public Date getDateFrom(){
		return fromDate.getValue();
	}

	public Date getDateTo(){
		return toDate.getValue();
	}

	public void setCurrentServerTime(String currentServerTime){
		serverTimeLabel.setText(Translation.get(TextID.ServerTime) + ": " + currentServerTime);
	}

	public void setCurrentFilterMode(Object object){
		filterModeCombo.setSelectedItem(object);
	}

	public Object getCurrentFilterMode(){
		return filterModeCombo.getSelectedItem();
	}

	public void setFilterModeComboListener(FilterModeComboListener filterModeComboListener){
		filterModeCombo.addActionListener(filterModeComboListener);
	}

	public void clearFilterItems(){
		filterCombo.removeAllItems();
		filterCombo.addItem(null);
		filterCombo.setSelectedIndex(0);
	}

	public void addLinkButtonListener(LinkButtonListener l){
		todayLinkButton.addActionListener(l);
		thisWeekLinkButton.addActionListener(l);
		thisMonthLinkButton.addActionListener(l);
	}

	public void setFilterItems(List<?> activityTypes){
		if (activityTypes == null)
			return;
		for (Object o : activityTypes){
			if (o instanceof UserActivityType){
				UserActivityType uat = (UserActivityType) o;
				uat.setString(Translation.get(uat.getValue()));
			}
			filterCombo.addItem(o);
		}
	}

	public void setTableModelUser(List<User> data){
		UserActivityTreeTableModelUser treeModel = new UserActivityTreeTableModelUser(data);
		UserActivityTableModelUser tableModel = new UserActivityTableModelUser(activityTable.getTree());
		activityTable.setModel(treeModel, tableModel);
		activityTable.getTree().setCellRenderer(new UserActivityTreeCellRenderer(false));
	}

	public void setTableModelActivities(Map<UserActivityType, List<UserActivity>> map){
		UserActivityTreeTableModelActivity treeModel = new UserActivityTreeTableModelActivity(map);
		UserActivityTableModelActivity tableModel = new UserActivityTableModelActivity(activityTable.getTree());
		activityTable.setModel(treeModel, tableModel);
		activityTable.getTree().setCellRenderer(new UserActivityTreeCellRenderer(true));
	}

	public Object getCurrentFilter(){
		return filterCombo.getSelectedItem();
	}

	public void setSummary(String summary){
		summaryArea.showHTML(summary);
	}

	public void updateLabels(Object mode){
		if (mode == TextID.ActionBased)
			userActLabel.setText(Translation.get(TextID.Action));
		else if (mode == TextID.UserBased)
			userActLabel.setText(Translation.get(TextID.User));

	}
}