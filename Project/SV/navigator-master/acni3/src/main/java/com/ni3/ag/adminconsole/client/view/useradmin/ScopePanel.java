/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACCheckBox;
import com.ni3.ag.adminconsole.client.view.common.ACTextArea;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ChangeResetable;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ScopePanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private ACCheckBox useScopeForNodesBox, useScopeForEdgesBox;
	private ACTextArea nodesScopeQueryTArea, edgesScopeQueryTArea;
	private ACButton refreshBtn, updateBtn;

	private ACToolBar toolBar;

	private ChangeResetable[] resetableComponents;

	private final int gapSide = 20, gapTop = 20;

	public ScopePanel(){
		initializeComponents();
	}

	private void initializeComponents(){
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		toolBar = new ACToolBar();
		updateBtn = toolBar.makeUpdateButton();
		refreshBtn = toolBar.makeRefreshButton();
		add(toolBar);
		layout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, this);

		useScopeForNodesBox = new ACCheckBox(Translation.get(TextID.UseScopeForNodes));
		useScopeForNodesBox.setHorizontalTextPosition(SwingConstants.LEADING);
		useScopeForEdgesBox = new ACCheckBox(Translation.get(TextID.UseScopeForEdges));
		useScopeForEdgesBox.setHorizontalTextPosition(SwingConstants.LEADING);

		nodesScopeQueryTArea = new ACTextArea();

		edgesScopeQueryTArea = new ACTextArea();

		JPanel btnPanel = new JPanel();
		SpringLayout btnLayout = new SpringLayout();
		btnPanel.setLayout(btnLayout);

		// ==========----------
		// layout
		// =======-------------
		JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		layout.putConstraint(SpringLayout.NORTH, mainSplit, 0, SpringLayout.SOUTH, toolBar);
		layout.putConstraint(SpringLayout.WEST, mainSplit, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, mainSplit, 0, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, mainSplit, 0, SpringLayout.SOUTH, this);
		add(mainSplit);
		mainSplit.setTopComponent(makeNodesScopePanel());
		mainSplit.setBottomComponent(makeEdgesScopePanel());
		mainSplit.setDividerLocation(420);

		resetableComponents = new ChangeResetable[] { useScopeForNodesBox, nodesScopeQueryTArea, useScopeForEdgesBox,
		        edgesScopeQueryTArea };
	}

	private JPanel makeNodesScopePanel(){
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		layout.putConstraint(SpringLayout.NORTH, useScopeForNodesBox, gapTop, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, useScopeForNodesBox, gapSide, SpringLayout.WEST, panel);
		panel.add(useScopeForNodesBox);
		JScrollPane nodesScopeSPane = new JScrollPane(nodesScopeQueryTArea);
		layout.putConstraint(SpringLayout.NORTH, nodesScopeSPane, gapTop + 2, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.SOUTH, nodesScopeSPane, -10, SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.WEST, nodesScopeSPane, 100, SpringLayout.EAST, useScopeForNodesBox);
		layout.putConstraint(SpringLayout.EAST, nodesScopeSPane, -gapSide, SpringLayout.EAST, panel);
		panel.add(nodesScopeSPane);
		JLabel nodesScopeQueryLabel = new JLabel(Translation.get(TextID.Query));
		layout.putConstraint(SpringLayout.NORTH, nodesScopeQueryLabel, gapTop + 5, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.EAST, nodesScopeQueryLabel, -5, SpringLayout.WEST, nodesScopeSPane);
		panel.add(nodesScopeQueryLabel);
		return panel;
	}

	private JPanel makeEdgesScopePanel(){
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		layout.putConstraint(SpringLayout.NORTH, useScopeForEdgesBox, gapTop, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, useScopeForEdgesBox, gapSide, SpringLayout.WEST, panel);
		panel.add(useScopeForEdgesBox);
		JScrollPane edgesScopeSPane = new JScrollPane(edgesScopeQueryTArea);
		layout.putConstraint(SpringLayout.NORTH, edgesScopeSPane, gapTop + 2, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.SOUTH, edgesScopeSPane, -10, SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.WEST, edgesScopeSPane, 100, SpringLayout.EAST, useScopeForEdgesBox);
		layout.putConstraint(SpringLayout.EAST, edgesScopeSPane, -gapSide, SpringLayout.EAST, panel);
		panel.add(edgesScopeSPane);
		JLabel edgesScopeQueryLabel = new JLabel(Translation.get(TextID.Query));
		layout.putConstraint(SpringLayout.NORTH, edgesScopeQueryLabel, gapTop + 5, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.EAST, edgesScopeQueryLabel, -5, SpringLayout.WEST, edgesScopeSPane);
		panel.add(edgesScopeQueryLabel);
		return panel;
	}

	public void addUpdateGroupScopeButtonListener(ActionListener actionListener){
		updateBtn.addActionListener(actionListener);
	}

	public void addRefreshGroupScopeButtonListener(ActionListener actionListener){
		refreshBtn.addActionListener(actionListener);
	}

	public void setData(boolean useScopeForNodes, String nodesScope, boolean useScopeForEdges, String edgesScope){
		useScopeForNodesBox.setSelected(useScopeForNodes);
		nodesScopeQueryTArea.setText(nodesScope);
		useScopeForEdgesBox.setSelected(useScopeForEdges);
		edgesScopeQueryTArea.setText(edgesScope);
	}

	public boolean isUseNodeScope(){
		return useScopeForNodesBox.isSelected();
	}

	public boolean isUseEdgeScope(){
		return useScopeForEdgesBox.isSelected();
	}

	public String getNodesScope(){
		return nodesScopeQueryTArea.getText();
	}

	public String getEdgesScope(){
		return edgesScopeQueryTArea.getText();
	}

	public ChangeResetable[] getChangeResetableComponents(){
		return resetableComponents;
	}
}
