/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.reports;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionListener;

import com.ni3.ag.navigator.client.domain.ReportTemplate;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;

public class ReportPreviewDialog extends Ni3Dialog{
	private static final long serialVersionUID = 6491514892587562437L;

	private JButton xlsButton;
	private JButton pdfButton;
	private JButton cancelButton;
	private JButton resetButton;

	private JList list;
	private JTree columnTree;

	public ReportPreviewDialog(){
		super();
		setTitle(UserSettings.getWord("Reports"));
		initComponents();
	}

	protected void initComponents(){
		setModal(true);
		setSize(new Dimension(600, 600));

		JPanel mainPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		mainPanel.setLayout(layout);
		getContentPane().add(mainPanel);
		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplit.setDividerLocation(300);
		mainPanel.add(mainSplit);

		list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane sp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setViewportView(list);
		mainSplit.setLeftComponent(sp);

		list.setCellRenderer(new ReportListCellRenderer());

		layout.putConstraint(SpringLayout.NORTH, mainSplit, 10, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, mainSplit, -10, SpringLayout.EAST, mainPanel);
		layout.putConstraint(SpringLayout.WEST, mainSplit, 10, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.SOUTH, mainSplit, -40, SpringLayout.SOUTH, mainPanel);

		xlsButton = new JButton(UserSettings.getWord("XLS"), IconCache.getImageIcon(IconCache.REPORT_XLS));
		xlsButton.setToolTipText(UserSettings.getWord("Save as XLS"));
		pdfButton = new JButton(UserSettings.getWord("PDF"), IconCache.getImageIcon(IconCache.REPORT_PDF));
		pdfButton.setToolTipText(UserSettings.getWord("Save as PDF"));

		cancelButton = new JButton(UserSettings.getWord("Cancel"));
		cancelButton.setPreferredSize(new Dimension(cancelButton.getPreferredSize().width, 25));
		resetButton = new JButton(UserSettings.getWord("Reset"));
		resetButton.setPreferredSize(new Dimension(resetButton.getPreferredSize().width, 25));

		layout.putConstraint(SpringLayout.NORTH, resetButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, resetButton, -10, SpringLayout.EAST, mainPanel);
		mainPanel.add(resetButton);

		layout.putConstraint(SpringLayout.NORTH, cancelButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, cancelButton, -10, SpringLayout.WEST, resetButton);
		mainPanel.add(cancelButton);

		layout.putConstraint(SpringLayout.NORTH, xlsButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, xlsButton, -10, SpringLayout.WEST, cancelButton);
		mainPanel.add(xlsButton);

		layout.putConstraint(SpringLayout.NORTH, pdfButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, pdfButton, -10, SpringLayout.WEST, xlsButton);
		mainPanel.add(pdfButton);

		columnTree = new JTree();
		columnTree.setCellRenderer(new CheckBoxTreeCellRenderer());
		columnTree.setCellEditor(new CheckBoxTreeCellEditor(columnTree));
		columnTree.setEditable(true);
		JScrollPane treeScroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treeScroll.setViewportView(columnTree);

		mainSplit.setRightComponent(treeScroll);
		mainSplit.setBorder(BorderFactory.createEmptyBorder());
	}

	public void addListSelectionListener(ListSelectionListener l){
		list.addListSelectionListener(l);
	}

	public void addPdfButtonListener(ActionListener l){
		pdfButton.addActionListener(l);
	}

	public void addXlsButtonListener(ActionListener l){
		xlsButton.addActionListener(l);
	}

	public void addResetButtonListener(ActionListener l){
		resetButton.addActionListener(l);
	}

	public void addCancelButtonListener(ActionListener l){
		cancelButton.addActionListener(l);
	}

	public void showDialog(){
		list.clearSelection();
		checkTreeVisibility();
		setVisible(true);
	}

	public void checkTreeVisibility(){
		ReportTemplate report = getSelectedReport();
		columnTree.setEnabled(report != null && report.isDynamicReport());
	}

	public ReportTemplate getSelectedReport(){
		if (list.getSelectedIndex() < 0){
			return null;
		}
		return (ReportTemplate) list.getSelectedValue();
	}

	public void setTreeModel(ReportColumnSelectionTreeModel model){
		columnTree.setModel(model);
	}

	public void initListModel(List<ReportTemplate> reports){
		DefaultListModel model = new DefaultListModel();
		for (ReportTemplate rt : reports){
			model.addElement(rt);
		}
		list.setModel(model);
	}

	public void refreshColumnTree(){
		columnTree.repaint();
	}

}