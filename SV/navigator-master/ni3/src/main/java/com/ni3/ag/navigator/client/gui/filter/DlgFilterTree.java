/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.filter;

import java.awt.*;
import javax.swing.*;

import com.ni3.ag.navigator.client.controller.graph.ValueUsageStatistics;
import com.ni3.ag.navigator.client.domain.DataFilter;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.model.Ni3Document;

@SuppressWarnings("serial")
public class DlgFilterTree extends Ni3Dialog{
	public static final int RET_CANCEL = 0;
	public static final int RET_OK = 1;

	private int returnStatus = RET_CANCEL;

	private FilterTree tree;
	private JCheckBox selectAll;
	private boolean showNodes;
	private boolean showEdges;
	private boolean showHalo;
	private boolean oneValueOnly;

	public Ni3Document doc;
	public DataFilter antiFilter;

	public DlgFilterTree(Ni3Document doc, int x, int y, boolean showNodes, boolean showEdges,
						 boolean showHalo, boolean oneValueOnly, DataFilter antifilter,
						 ValueUsageStatistics statistics){
		super();
		this.setTitle(UserSettings.getWord("Selective Expand"));

		this.doc = doc;
		this.showNodes = showNodes;
		this.showEdges = showEdges;
		this.showHalo = showHalo;
		this.antiFilter = antifilter;
		this.oneValueOnly = oneValueOnly;

		initComponents(statistics);

		setLocation(x, y);
		setSize(280, 350);
	}

	protected void initComponents(ValueUsageStatistics statistics){
		getContentPane().setLayout(new BorderLayout());

		tree = new FilterTree(doc, doc.SYSGroupPrefilter, false, false, showNodes, showEdges,
				oneValueOnly, statistics);
        tree.setName("SelectiveExpandFilterTree");

		tree.setTree(showHalo);
		tree.syncComplete(doc.filter, false);

		tree.ExpandAll();

		int maxWidth = 0;

		for (CheckNode node : tree.getAllNodes()){
			if (node.toString().length() > maxWidth)
				maxWidth = node.toString().length();
		}

		tree.clearFilter(false);

		if (antiFilter != null)
			tree.setAntiFilter(antiFilter);

		JScrollPane treePanel = new JScrollPane(tree);

		selectAll = new JCheckBox("Select all");
		selectAll.setSelected(false);

		if (!oneValueOnly)
			add(selectAll, BorderLayout.NORTH);

		selectAll.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				selectAllButtonActionPerformed();
			}
		});

		add(treePanel, BorderLayout.CENTER);

		JPanel okcancel = new JPanel();

		JButton btn = new JButton(UserSettings.getWord("OK"));
		okcancel.add(btn);

		btn.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				okButtonActionPerformed();
			}
		});

		btn = new JButton(UserSettings.getWord("Cancel"));
		okcancel.add(btn);
		btn.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				cancelButtonActionPerformed();
			}
		});

		add(okcancel, BorderLayout.SOUTH);
	}

	private void selectAllButtonActionPerformed(){
		tree.selectAll(selectAll.isSelected());
	}

	@Override
	protected void onEnterAction(){
		if (antiFilter == null)
			antiFilter = new DataFilter();
		else
			antiFilter.reset();

		antiFilter = tree.createAntiFilter(new DataFilter());
		doClose(RET_OK);
	}

	public int getCommonCount(){
		return tree.commonCount;
	}

	private void okButtonActionPerformed(){
		onEnterAction();
	}

	/**
	 * @return the return status of this dialog - one of RET_OK or RET_CANCEL
	 */
	public int getReturnStatus(){
		return returnStatus;
	}

	private void cancelButtonActionPerformed(){
		doClose(RET_CANCEL);
	}

	@SuppressWarnings("unused")
	private void closeDialog(java.awt.event.WindowEvent evt){
		doClose(RET_CANCEL);
	}

	private void doClose(int retStatus){
		returnStatus = retStatus;
		setVisible(false);
		dispose();
	}
}
