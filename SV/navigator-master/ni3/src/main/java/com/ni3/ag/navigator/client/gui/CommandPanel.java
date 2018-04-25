/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.controller.graph.GraphController;
import com.ni3.ag.navigator.client.controller.graph.ValueUsageStatistics;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.gateway.GraphGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpGraphGatewayImpl;
import com.ni3.ag.navigator.client.gui.filter.DlgFilterTree;
import com.ni3.ag.navigator.client.gui.filter.FiltersPanel;
import com.ni3.ag.navigator.client.gui.graph.CommandPanelSettings;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.GraphCollection;

@SuppressWarnings("serial")
public class CommandPanel extends Ni3Panel{

	// Each commands
	private JCheckBox cbShowLabels;
	private JCheckBox cbShowEdgeThickness = null;
	private JCheckBox cbShowEdgeLabels = null;
	private JCheckBox cbDirectedGraph;
	private JCheckBox cbShowContractedEdgeCount;
	private JPanel pnlGraphControlBig;
	private DropDownButton btnExpandOneLevel;

	private JButton btnContractOneLevel;

	private JLabel lblGraphNodesCounter;
	private JLabel lblGraphEdgesCounter;

	private int controlCount;
	private GraphController graphController;

	public FiltersPanel filtersPanel;

	public CommandPanel(MainPanel parent){
		super(parent);
		this.graphController = new GraphController(parent);

		createComponents();
		layoutComponents();
		this.setMinimumSize(new Dimension(0, 0));

		setUp();
		Doc.registerListener(this);
	}

	public int getListenerType(){
		return Ni3ItemListener.SRC_Other;
	}

	public void setUp(){
		CommandPanelSettings settings = Doc.getCommandPanelSettings();

		cbShowLabels.setSelected(settings.isShowNodeLabels());

		if (cbShowEdgeLabels != null)
			cbShowEdgeLabels.setSelected(settings.isShowEdgeLabels());

		if (cbDirectedGraph != null)
			cbDirectedGraph.setSelected(settings.isShowDirectedEdges());

		if (cbShowEdgeThickness != null)
			cbShowEdgeThickness.setSelected(settings.isShowEdgeThickness());
	}

	private void createComponents(){
		lblGraphNodesCounter = new JLabel("  " + getWord("Nodes") + " : 0");
		lblGraphNodesCounter.setFont(getFont("GRAPH_CONTROL_COUNTER_FONT"));

		lblGraphEdgesCounter = new JLabel("  " + getWord("Edges") + " : 0");
		lblGraphEdgesCounter.setFont(getFont("GRAPH_CONTROL_COUNTER_FONT"));

		// Show labels
		cbShowLabels = new JCheckBox(getWord("Show labels"));
		cbShowLabels.setToolTipText(getWord("SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT"));
		cbShowLabels.addItemListener(new ShowLabelsCheckboxEventHandler());
		final Font graphControlFont = getFont("GRAPH_CONTROL_ITEM_FONT");
		cbShowLabels.setFont(graphControlFont);

		controlCount = 0;
		// Show Edge labels
		final boolean edgeThicknessInUse = UserSettings.getBooleanAppletProperty("ShowEdgeThickness_InUse", true);
		if (edgeThicknessInUse){
			cbShowEdgeThickness = new JCheckBox(getWord("Show edge thickness"));
			cbShowEdgeThickness.setToolTipText(getWord("SHOW_EDGE_THICKNESS_CHECKBOX_TOOLTIP_TEXT"));
			cbShowEdgeThickness.addItemListener(new ShowEdgeThicknessCheckboxEventHandler());
			cbShowEdgeThickness.setFont(graphControlFont);
			controlCount++;
		}
		// Show Edge labels
		final boolean edgeLabelsInUse = UserSettings.getBooleanAppletProperty("ShowEdgeLabel_InUse", true);
		if (edgeLabelsInUse){
			cbShowEdgeLabels = new JCheckBox(getWord("Show Edge labels"));
			cbShowEdgeLabels.setToolTipText(getWord("SHOW_EDGE_LABELS_CHECKBOX_TOOLTIP_TEXT"));
			cbShowEdgeLabels.addItemListener(new ShowEdgeLabelsCheckboxEventHandler());
			cbShowEdgeLabels.setFont(graphControlFont);
			controlCount++;
		}

		final boolean directedGraphInUse = UserSettings.getBooleanAppletProperty("ShowDirectedGraph_InUse", true);
		if (directedGraphInUse){
			// Directed - undirected graph
			cbDirectedGraph = new JCheckBox(getWord("Directed graph"));
			cbDirectedGraph.setToolTipText(getWord("DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT"));
			cbDirectedGraph.addItemListener(new DirectedGraphCheckboxEventHandler());
			cbDirectedGraph.setFont(graphControlFont);
			controlCount++;
		}

		final boolean counterInUse = UserSettings.getBooleanAppletProperty("ShowNodeExpandCounter_InUse", true);
		if (counterInUse){
			final boolean showCounter = UserSettings.getBooleanAppletProperty("ShowNodeExpandCounter", true);
			cbShowContractedEdgeCount = new JCheckBox(getWord("ShowContractedEdgeCount"));
			cbShowContractedEdgeCount.setSelected(showCounter);
			cbShowContractedEdgeCount.setToolTipText(getWord("ShowContractedEdgeCountTooltip"));
			cbShowContractedEdgeCount.addItemListener(new ShowContractedEdgeCountCheckboxEventHandler());
			cbShowContractedEdgeCount.setFont(graphControlFont);
			controlCount++;
		}

		filtersPanel = new FiltersPanel(parentMP);
	}

	private void layoutComponents(){
		JPanel pnlGraphControl = new JPanel(new GridLayout(3 + controlCount, 1));

		pnlGraphControl.setMinimumSize(new Dimension(10, (3 + controlCount) * 15));
		pnlGraphControl.setMaximumSize(new Dimension(10, (3 + controlCount) * 15));
		pnlGraphControl.setPreferredSize(new Dimension(10, (3 + controlCount) * 15));

		pnlGraphControlBig = new JPanel(new BorderLayout());

		pnlGraphControlBig.setMinimumSize(new Dimension(10, (3 + controlCount) * 15));
		pnlGraphControlBig.setMaximumSize(new Dimension(10, (3 + controlCount) * 15));
		pnlGraphControlBig.setPreferredSize(new Dimension(10, (3 + controlCount) * 15));

		pnlGraphControl.add(cbShowLabels);
		if (cbShowEdgeLabels != null)
			pnlGraphControl.add(cbShowEdgeLabels);
		if (cbDirectedGraph != null)
			pnlGraphControl.add(cbDirectedGraph);
		if (cbShowEdgeThickness != null)
			pnlGraphControl.add(cbShowEdgeThickness);
		if (cbShowContractedEdgeCount != null)
			pnlGraphControl.add(cbShowContractedEdgeCount);
		pnlGraphControl.add(lblGraphNodesCounter);
		pnlGraphControl.add(lblGraphEdgesCounter);
		createLevelButtons();
		pnlGraphControlBig.add(pnlGraphControl, "Center");

		JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlGraphControlBig, filtersPanel);

		setLayout(new BorderLayout());

		add(mainSplit, "Center");
	}

	public void setNodeCounter(){
		final int nodes = Doc.getNodeCount();
		final int nodesTotal = Doc.getNodeTotalCount();
		lblGraphNodesCounter.setText("  " + getWord("Nodes") + " : " + nodes + "/" + nodesTotal);
		filtersPanel.repaintTree();
	}

	public void setEdgeCounter(){
		final int edges = Doc.getEdgeCount();
		final int edgesTotal = Doc.getEdgeTotalCount();
		lblGraphEdgesCounter.setText("  " + getWord("Edges") + " : " + edges + "/" + edgesTotal);
		filtersPanel.repaintTree();
	}

	private void createLevelButtons(){
		JPanel levelsButtonPanel = new JPanel();
		levelsButtonPanel.setLayout(new GridLayout(2, 1));

		String options[] = { UserSettings.getWord("Expand All"), UserSettings.getWord("Selective expand") };
		String cmds[] = { "ExpandAll", "SelectiveExpand" };

		btnExpandOneLevel = new DropDownButton(IconCache.getImageIcon(IconCache.FILTER_EXPAND), options, cmds);
		btnExpandOneLevel.setName("Expand");
		btnExpandOneLevel.setToolTipText(UserSettings.getWord("ExpandGraph"));
		levelsButtonPanel.add(btnExpandOneLevel);

		btnExpandOneLevel.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				if ("ExpandAll".equals(evt.getActionCommand())){
					graphController.expandOneLevel(false, false);
				} else if ("SelectiveExpand".equals(evt.getActionCommand())){
					selectiveExpandAll();
				}
			}
		});

		btnContractOneLevel = new JButton(IconCache.getImageIcon(IconCache.FILTER_COLLAPSE));
		btnContractOneLevel.setName("Contract");
		btnContractOneLevel.setToolTipText(UserSettings.getWord("ContractGraph"));
		levelsButtonPanel.add(btnContractOneLevel);

		btnContractOneLevel.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				graphController.contractOneLevel();
			}
		});

		pnlGraphControlBig.add(levelsButtonPanel, "East");
	}

	protected void selectiveExpandAll(){
		final GraphCollection bunch = new GraphCollection(false);
		final java.util.List<Integer> roots = new ArrayList<Integer>();
		for (Node node : Doc.Subgraph.getNodes())
			roots.add(node.ID);

		GraphGateway graphGateway = new HttpGraphGatewayImpl();
		//TODO is it a really right way to do it (load +1 graph -> subtract existing -> show)(out of memory potentially possible)
		List<GraphObject> graphObjects = graphGateway.getNodesAndEdges(roots, Doc.SchemaID, Doc.DB.getDataFilter(), -1);

		if (graphObjects != null){
			bunch.addResultToGraph(graphObjects);
			Doc.DB.prepareSubgraph(bunch, false);
		} else{
			return;
		}
		bunch.subtract(Doc.Subgraph);
		ValueUsageStatistics statistics = GraphController.calculateStatistics(bunch.getObjects(), true);

		final DlgFilterTree dlg = new DlgFilterTree(Doc, 100, 100, false, true, false, false, null, statistics);
		dlg.setVisible(true);

		if(dlg.getCommonCount() >= Doc.DB.getMaximumNodeCount())
		{
			GraphController.showNodeLimitError();
			return;
		}
		if (dlg.getReturnStatus() == DlgNodeProperties.RET_OK){
			graphController.selectiveExpandAll(dlg.antiFilter);
		}

	}

	class ShowEdgeThicknessCheckboxEventHandler implements ItemListener{
		public void itemStateChanged(ItemEvent ie){
			final CommandPanelSettings cpSettings = Doc.getCommandPanelSettings();
			cpSettings.setShowEdgeThickness(cbShowEdgeThickness.isSelected());
			Doc.setCommandPanelSettings(cpSettings);
		}
	}

	class ShowLabelsCheckboxEventHandler implements ItemListener{
		public void itemStateChanged(ItemEvent ie){
			final CommandPanelSettings cpSettings = Doc.getCommandPanelSettings();
			cpSettings.setShowNodeLabels(cbShowLabels.isSelected());
			Doc.setCommandPanelSettings(cpSettings);
		}
	}

	class ShowEdgeLabelsCheckboxEventHandler implements ItemListener{
		public void itemStateChanged(ItemEvent ie){
			final CommandPanelSettings cpSettings = Doc.getCommandPanelSettings();
			cpSettings.setShowEdgeLabels(cbShowEdgeLabels.isSelected());
			Doc.setCommandPanelSettings(cpSettings);
		}
	}

	class DirectedGraphCheckboxEventHandler implements ItemListener{
		public void itemStateChanged(ItemEvent ie){
			final CommandPanelSettings cpSettings = Doc.getCommandPanelSettings();
			cpSettings.setShowDirectedEdges(cbDirectedGraph.isSelected());
			Doc.setCommandPanelSettings(cpSettings);
		}
	}

	class ShowContractedEdgeCountCheckboxEventHandler implements ItemListener{
		public void itemStateChanged(ItemEvent ie){
			Doc.getGraphVisualSettings().setShowContractedEdgeCounter(cbShowContractedEdgeCount.isSelected());
			Doc.dispatchEvent(MSG_GraphDirty, SRC_MainPanel, parentMP, null);
		}
	}

	@Override
	public void event(int eventCode, int sourceID, Object source, Object param){
		switch (eventCode){
			case MSG_EdgeCountChanged:
				setEdgeCounter();
				break;
			case MSG_NodeCountChanged:
				setNodeCounter();
				break;
			case MSG_ShowContractedEdgeCounterChanged:
				cbShowContractedEdgeCount.setSelected((Boolean) param);
				break;
			case MSG_CommandPanelSettingsChanged:
			case MSG_FavoriteLoaded:
				setUp();
				break;
		}
	}
}
