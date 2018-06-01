/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.datamerge;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.BooleanCellRenderer;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;

public class NodeMergeDialog extends Ni3Dialog{
	private static final long serialVersionUID = 6234555822930170305L;
	private JButton okButton;
	private JButton cancelButton;
	private JTable table;
	private JTable connectionTable;
	private boolean okPressed = false;

	public NodeMergeDialog(){
		super();
		setTitle(UserSettings.getWord("MergeNodes"));
		initComponents();
	}

	protected void initComponents(){
		setSize(new Dimension(400, 400));

		JPanel mainPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		mainPanel.setLayout(layout);
		getContentPane().add(mainPanel);

		table = new JTable();
		JScrollPane sp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setViewportView(table);

		connectionTable = new JTable();
		JScrollPane sp1 = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp1.setViewportView(connectionTable);

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		mainPanel.add(split);
		layout.putConstraint(SpringLayout.NORTH, split, 0, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, split, 0, SpringLayout.EAST, mainPanel);
		layout.putConstraint(SpringLayout.WEST, split, 0, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.SOUTH, split, -40, SpringLayout.SOUTH, mainPanel);

		split.setTopComponent(sp);
		split.setBottomComponent(sp1);

		okButton = new JButton(UserSettings.getWord("Ok"));
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				okPressed = true;
				setVisible(false);
			}
		});
		cancelButton = new JButton(UserSettings.getWord("Cancel"));
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				okPressed = false;
				setVisible(false);
			}
		});

		layout.putConstraint(SpringLayout.NORTH, cancelButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, cancelButton, -10, SpringLayout.EAST, mainPanel);
		mainPanel.add(cancelButton);

		layout.putConstraint(SpringLayout.NORTH, okButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, okButton, -10, SpringLayout.WEST, cancelButton);
		mainPanel.add(okButton);

		split.setDividerLocation(200);

		table.setDefaultRenderer(String.class, new NodeMergeStringCellRenderer());
		TableCellRenderer boolCellRenderer = table.getDefaultRenderer(Boolean.class);
		table.setDefaultRenderer(Boolean.class, new BooleanCellRenderer(boolCellRenderer));
	}

	public void addOkButtonListener(ActionListener l){
		okButton.addActionListener(l);
	}

	public void addCancelButtonListener(ActionListener l){
		cancelButton.addActionListener(l);
	}

	public void setTableModel(TableModel model){
		table.setModel(model);
	}

	public void setConnectionTableModel(TableModel model){
		connectionTable.setModel(model);
	}

	public NodeMergeTableModel getTableModel(){
		return (NodeMergeTableModel) table.getModel();
	}

	public NodeConnectionMergeTableModel getConnectionTableModel(){
		return (NodeConnectionMergeTableModel) connectionTable.getModel();
	}

	public boolean isOkPressed(){
		return okPressed;
	}

	public void showIt(){
		okPressed = false;
		double screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		double screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		setLocation((int) (screenWidth / 2) - getWidth() / 2, (int) (screenHeight / 2) - getHeight() / 2);
		setVisible(true);
	}
}
