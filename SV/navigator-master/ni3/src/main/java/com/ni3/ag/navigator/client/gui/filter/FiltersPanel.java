/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.filter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.*;

import com.ni3.ag.navigator.client.controller.graph.GraphController;
import com.ni3.ag.navigator.client.domain.DataFilter;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.gui.Ni3Panel;

@SuppressWarnings("serial")
public class FiltersPanel extends Ni3Panel implements ActionListener, Ni3ItemListener, ComponentListener{
	private JTabbedPane Pane;

	private JPanel panelFilter;
	private JPanel panelPreFilter;
	private FilterTree ftree;
	private JPrefilterTree preftree;
	private JScrollPane sp;
	private JScrollPane presp;
	private JCheckBox orphans;
	private JCheckBox connectedOnlyBtn;
	private JCheckBox NoFocus;
	private JCheckBox NoSingles, FilterFrom, FilterTo, currentFavoritesOnly;
	private JButton ApplyPrefilter;
	private JButton reset;
	private JRadioButton topicSelection[];
	private SpringLayout mainLayout, filterLayout, preFilterLayout;
	private int currentWidth, position;
	private JPanel cmdFilterPanel;
	private JLabel frame;

	public FiltersPanel(MainPanel parent){
		super(parent);

		Doc.registerListener(this);

		addComponentListener(this);

		createComponents();

		restoreFilter(Doc.filter);
		restorePrefilter(Doc.DB.getDataFilter());

		initListeners();
	}

	public int getListenerType(){
		return Ni3ItemListener.SRC_FiltersPanel;
	}

	private void createComponents(){
		Pane = new JTabbedPane();
		Pane.setName("FilterTabbedPane");

		createFilterPanel();

		boolean dataFilterVisible = UserSettings.getBooleanAppletProperty("DisplayFilter_Visible", false);
		if (dataFilterVisible) {
			createPreFilterPanel();
		} else {
			//TODO: dirty hack to avoid NPE's, remove later
			preftree = new JPrefilterTree(Doc, Doc.SYSGroupPrefilter);
		}

		mainLayout = new SpringLayout();
		setLayout(mainLayout);

		setVisible(true);

		reset = new JButton(getWord("ResetFilter"));
		reset.setActionCommand("Reset");
		reset.addActionListener(this);

		this.add(reset);
		this.add(Pane);
	}

	private void createFilterPanel(){
		panelFilter = new JPanel(new BorderLayout());

		filterLayout = new SpringLayout();
		cmdFilterPanel = new JPanel(filterLayout);

		sp = new JScrollPane();

		boolean showEmptyValues = UserSettings.getBooleanAppletProperty("DisplayFilter_ShowEmptyValues", true);
		ftree = new FilterTree(Doc, Doc.SYSGroupPrefilter, true, showEmptyValues, true, true, false, Doc.getStatistics());
		ftree.setName("FilterTree");
		setTree();

		orphans = new JCheckBox(getWord("NoOrphans"));
		if (UserSettings.getBooleanAppletProperty("NoOrphans_Visible", false))
			cmdFilterPanel.add(orphans);

		connectedOnlyBtn = new JCheckBox(getWord("NoUnrelated"));
		if (UserSettings.getBooleanAppletProperty("NoUnrelated_Visible", true))
			cmdFilterPanel.add(connectedOnlyBtn);

		NoFocus = new JCheckBox(getWord("NoFocus"));
		if (UserSettings.getBooleanAppletProperty("NoFocus_Visible", true))
			cmdFilterPanel.add(NoFocus);

		NoSingles = new JCheckBox(getWord("NoSingles"));
		if (UserSettings.getBooleanAppletProperty("NoSingles_Visible", true))
			cmdFilterPanel.add(NoSingles);

		FilterFrom = new JCheckBox(getWord("FilterFrom"));
		if (UserSettings.getBooleanAppletProperty("FilterFrom_Visible", true))
			cmdFilterPanel.add(FilterFrom);

		FilterTo = new JCheckBox(getWord("FilterTo"));

		if (UserSettings.getBooleanAppletProperty("FilterTo_Visible", true))
			cmdFilterPanel.add(FilterTo);

		frame = new JLabel();
		frame.setOpaque(false);
		frame.setBorder(BorderFactory.createLoweredBevelBorder());

		currentFavoritesOnly = new JCheckBox(getWord("CurrentFavoritesOnly"));
		if (UserSettings.getBooleanAppletProperty("TopicGUI_InUse", false)){
			cmdFilterPanel.add(currentFavoritesOnly);
			cmdFilterPanel.add(frame);
		}
		topicSelection = new JRadioButton[3];

		topicSelection[0] = new JRadioButton(UserSettings.getWord("My"));
		topicSelection[0].setToolTipText(UserSettings.getWord("My"));
		topicSelection[0].setEnabled(false);

		topicSelection[1] = new JRadioButton(UserSettings.getWord("Group"));
		topicSelection[1].setToolTipText(UserSettings.getWord("Group"));
		topicSelection[1].setEnabled(false);

		topicSelection[2] = new JRadioButton(UserSettings.getWord("All"));
		topicSelection[2].setToolTipText(UserSettings.getWord("All"));
		topicSelection[2].setEnabled(false);
		topicSelection[2].setSelected(true);

		ButtonGroup buttonTopic = new ButtonGroup();
		buttonTopic.add(topicSelection[0]);
		buttonTopic.add(topicSelection[1]);
		buttonTopic.add(topicSelection[2]);

		cmdFilterPanel.add(sp);

		cmdFilterPanel.setPreferredSize(new Dimension(100, 120));
		if (UserSettings.getBooleanAppletProperty("TopicEdgeSelection_Visible", false)){
			cmdFilterPanel.add(topicSelection[0]);
			cmdFilterPanel.add(topicSelection[1]);
			cmdFilterPanel.add(topicSelection[2]);
		}

		panelFilter.setLayout(new BorderLayout());

		panelFilter.add(cmdFilterPanel, "Center");

		Pane.addTab(getWord("Filter"), panelFilter);
	}

	private void initListeners(){
		orphans.addActionListener(this);
		orphans.setActionCommand("orphans");

		connectedOnlyBtn.addActionListener(this);
		connectedOnlyBtn.setActionCommand("ConnectedOnly");

		NoFocus.addActionListener(this);
		NoFocus.setActionCommand("NoFocus");

		NoSingles.addActionListener(this);
		NoSingles.setActionCommand("NoSingles");

		FilterFrom.addActionListener(this);
		FilterFrom.setActionCommand("FilterFrom");

		FilterTo.addActionListener(this);
		FilterTo.setActionCommand("FilterTo");

		currentFavoritesOnly.addActionListener(this);
		currentFavoritesOnly.setActionCommand("CurrentFavoritesOnly");

		for (JRadioButton b : topicSelection){
			b.addActionListener(this);
			b.setActionCommand("topicFilter");
		}

		if (ApplyPrefilter != null) {
			ApplyPrefilter.addActionListener(this);
			ApplyPrefilter.setActionCommand("ApplyPrefilter");
		}
	}

	private void createPreFilterPanel(){
		panelPreFilter = new JPanel(new BorderLayout());
		preFilterLayout = new SpringLayout();
		JPanel cmdFilterPanel = new JPanel(preFilterLayout);
		cmdFilterPanel.setPreferredSize(new Dimension(100, 20));

		presp = new JScrollPane();

		ApplyPrefilter = new JButton(getWord("ApplyPrefilter"));
		ApplyPrefilter.setMaximumSize(new Dimension(70, 20));

		preFilterLayout.putConstraint(SpringLayout.NORTH, ApplyPrefilter, 0, SpringLayout.NORTH, cmdFilterPanel);
		preFilterLayout.putConstraint(SpringLayout.WEST, ApplyPrefilter, 0, SpringLayout.WEST, cmdFilterPanel);
		preFilterLayout.putConstraint(SpringLayout.EAST, ApplyPrefilter, ApplyPrefilter.getText().length() * 13,
				SpringLayout.WEST, cmdFilterPanel);
		preFilterLayout.putConstraint(SpringLayout.SOUTH, ApplyPrefilter, 20, SpringLayout.NORTH, cmdFilterPanel);

		cmdFilterPanel.add(ApplyPrefilter);
		cmdFilterPanel.add(new JLabel(""));

		preftree = new JPrefilterTree(Doc, Doc.SYSGroupPrefilter);
		setPreTree();

		panelPreFilter.setLayout(new BorderLayout());

		panelPreFilter.add(presp, "Center");
		panelPreFilter.add(cmdFilterPanel, "North");

		Pane.addTab(getWord("PreFilter"), panelPreFilter);
	}

	public void resetDisplayFilter(){
		ftree.resetFilter(true);

		repaint();
	}

	private void setTree(){
		ftree.setTree(true);
		sp.getViewport().setView(ftree);
	}

	private void setPreTree(){
		preftree.setTree();
		presp.getViewport().setView(preftree);
	}

	public void reloadFilter(DataFilter filter){
		ftree.setPrefilter(Doc.SYSGroupPrefilter);
		ftree.createTree(true, true);
		restoreFilter(filter);
	}

	public void reloadPrefilter(DataFilter filter){
		preftree.setPrefilter(Doc.SYSGroupPrefilter);
		preftree.createTree();
		restorePrefilter(filter);
	}

	public void restoreFilter(DataFilter filter){
		ftree.restoreFilter(filter);

		connectedOnlyBtn.setSelected(filter.isConnectedOnly());
		orphans.setSelected(filter.isNoOrphans());

		NoFocus.setSelected(filter.dontFilterFocusNodes);
		NoSingles.setSelected(filter.NoSingles);
		FilterFrom.setSelected(filter.FilterFrom);
		FilterTo.setSelected(filter.FilterTo);
		currentFavoritesOnly.setSelected(filter.currentFavoritesonly);

		if (filter.topicMode > 0 && filter.topicMode < 3)
			topicSelection[filter.topicMode].setSelected(true);

		for (int n = 0; n < 3; n++)
			topicSelection[n].setEnabled(!filter.currentFavoritesonly);

		connectedOnlyBtn.setEnabled(!filter.isNoOrphans());
	}

	public void restorePrefilter(DataFilter filter){
		preftree.restoreFilter(filter);
	}

	public DataFilter createFilter(){
		DataFilter filter = new DataFilter(Doc.SYSGroupPrefilter);

		filter = ftree.createFilter(filter);

		final boolean enabled = !orphans.isSelected();
		connectedOnlyBtn.setEnabled(enabled);
		if (!enabled && connectedOnlyBtn.isSelected()){
			connectedOnlyBtn.setSelected(false);
		}

		filter.setNoOrphans(orphans.isSelected());
		filter.setConnectedOnly(connectedOnlyBtn.isSelected() && !orphans.isSelected());

		filter.dontFilterFocusNodes = NoFocus.isSelected();

		filter.NoSingles = NoSingles.isSelected();
		filter.FilterFrom = FilterFrom.isSelected();
		filter.FilterTo = FilterTo.isSelected();
		filter.currentFavoritesonly = currentFavoritesOnly.isSelected();

		if (filter.currentFavoritesonly){
			filter.topicMode = -1;
		} else{
			for (int n = 0; n < 3; n++)
				if (topicSelection[n].isSelected())
					filter.topicMode = n;
		}

		filter.copyChartFilters(Doc.filter);

		return filter;
	}

	public DataFilter createPreFilter(){
		DataFilter filter = new DataFilter(Doc.SYSGroupPrefilter);

		filter = preftree.createFilter(filter);

		return filter;
	}

	public void setOrphans(boolean val){
		orphans.setSelected(val);
	}

	public void applyPrefilter(){
		ftree.syncStatus(preftree.allCheckNodesVector);
		DataFilter prefilter = preftree.createFilter(new DataFilter());
		GraphController gc = new GraphController(parentMP);
		gc.applyDataFilterToGraph(prefilter);
		Doc.setPrefilter(prefilter);
	}

	public void actionPerformed(ActionEvent ae){
		String action = ae.getActionCommand();

		if ("CurrentFavoritesOnly".equals(action)){
			boolean enable = currentFavoritesOnly.isSelected();

			for (int n = 0; n < 3; n++)
				topicSelection[n].setEnabled(!enable);
		}

		if ("topicFilter".equals(action) || "orphans".equals(action) || "ConnectedOnly".equals(action)
				|| "NoFocus".equals(action) || "NoSingles".equals(action) || "FilterFrom".equals(action)
				|| "CurrentFavoritesOnly".equals(action) || "FilterTo".equals(action)){
			Doc.setFilter(createFilter(), true, false);
		} else if ("ApplyPrefilter".equals(action)){
			applyPrefilter();
		} else if ("Reset".equals(action)){
			if (Pane.getSelectedIndex() == 0){
				ftree.resetFilter(true);
				ftree.syncComplete(createPreFilter(), true);
			} else{
				preftree.resetFilter(true);
				applyPrefilter();
			}
		}

		repaint();
	}

	public void onNewSubgraph(){
		restoreFilter(Doc.filter);
		restorePrefilter(Doc.DB.getDataFilter());
	}

	public void event(int EventCode, int SourceID, Object source, Object param){
		super.event(EventCode, SourceID, source, param);

		switch (EventCode){
			case Ni3ItemListener.MSG_FilterTreeChanged:
				Doc.setFilter(createFilter(), true, false);
				break;
			case Ni3ItemListener.MSG_FilterNew:
				if (param instanceof DataFilter){
					reloadFilter((DataFilter) param);
				}
				break;
			case Ni3ItemListener.MSG_PrefilterNew:
				if (param instanceof DataFilter){
					reloadPrefilter((DataFilter) param);
				}
				break;
			case Ni3ItemListener.MSG_PreFilterChanged:
			case Ni3ItemListener.MSG_NewSubgraph:
				ftree.syncStatus(preftree.allCheckNodesVector);
				break;
			case Ni3ItemListener.MSG_SubgraphChanged:
			case Ni3ItemListener.MSG_ClearSubgraph:
				if (!ftree.isShowEmptyValues()){
					ftree.createTree(true, true);
					ftree.restoreFilter(Doc.filter);
				}
				break;
			case Ni3ItemListener.MSG_TopicModeChanged:
				if (ftree.isShowEmptyValues()){
					ftree.createTree(true, true);
					ftree.restoreFilter(Doc.filter);
					preftree.createTree();
					preftree.restoreFilter(Doc.DB.getDataFilter());
				}
				break;
		}
	}

	@Override
	public void componentHidden(ComponentEvent e){
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent e){
		// TODO Auto-generated method stub

	}

	@Override
	public void componentResized(ComponentEvent e){
		currentWidth = super.getSize().width;
		layoutControls();
		layoutFilterPanel();
	}

	@Override
	public void componentShown(ComponentEvent e){
		// TODO Auto-generated method stub

	}

	private void layoutControls(){
		if (currentWidth < 150)
			position = 150;
		else
			position = currentWidth;
		mainLayout.putConstraint(SpringLayout.WEST, reset, -Math.min(position - 170, reset.getText().length() * 13),
				SpringLayout.EAST, this);
		mainLayout.putConstraint(SpringLayout.EAST, reset, 0, SpringLayout.EAST, this);
		mainLayout.putConstraint(SpringLayout.NORTH, reset, 0, SpringLayout.NORTH, this);
		mainLayout.putConstraint(SpringLayout.SOUTH, reset, 20, SpringLayout.NORTH, this);

		mainLayout.putConstraint(SpringLayout.WEST, Pane, 0, SpringLayout.WEST, this);
		mainLayout.putConstraint(SpringLayout.EAST, Pane, 0, SpringLayout.EAST, this);
		mainLayout.putConstraint(SpringLayout.NORTH, Pane, 0, SpringLayout.NORTH, this);
		mainLayout.putConstraint(SpringLayout.SOUTH, Pane, 0, SpringLayout.SOUTH, this);
		this.doLayout();

	}

	private void layoutFilterPanel(){
		if (cmdFilterPanel != null){
			position = (currentWidth - 20) / 3;
			if (position > 110)
				position = 110;

			filterLayout.putConstraint(SpringLayout.NORTH, orphans, 0, SpringLayout.NORTH, cmdFilterPanel);
			filterLayout.putConstraint(SpringLayout.WEST, orphans, 10, SpringLayout.WEST, cmdFilterPanel);
			filterLayout.putConstraint(SpringLayout.EAST, orphans, 10 + position, SpringLayout.WEST, cmdFilterPanel);

			filterLayout.putConstraint(SpringLayout.NORTH, connectedOnlyBtn, 0, SpringLayout.NORTH, orphans);
			filterLayout.putConstraint(SpringLayout.WEST, connectedOnlyBtn, 0, SpringLayout.EAST, orphans);
			filterLayout.putConstraint(SpringLayout.EAST, connectedOnlyBtn, position, SpringLayout.EAST, orphans);

			filterLayout.putConstraint(SpringLayout.NORTH, NoFocus, 0, SpringLayout.NORTH, connectedOnlyBtn);
			filterLayout.putConstraint(SpringLayout.WEST, NoFocus, 0, SpringLayout.EAST, connectedOnlyBtn);
			filterLayout.putConstraint(SpringLayout.EAST, NoFocus, position, SpringLayout.EAST, connectedOnlyBtn);

			filterLayout.putConstraint(SpringLayout.NORTH, NoSingles, 0, SpringLayout.SOUTH, orphans);
			filterLayout.putConstraint(SpringLayout.WEST, NoSingles, 0, SpringLayout.WEST, orphans);
			filterLayout.putConstraint(SpringLayout.EAST, NoSingles, 0, SpringLayout.EAST, orphans);

			filterLayout.putConstraint(SpringLayout.NORTH, FilterFrom, 0, SpringLayout.SOUTH, connectedOnlyBtn);
			filterLayout.putConstraint(SpringLayout.WEST, FilterFrom, 0, SpringLayout.WEST, connectedOnlyBtn);
			filterLayout.putConstraint(SpringLayout.EAST, FilterFrom, 0, SpringLayout.EAST, connectedOnlyBtn);

			filterLayout.putConstraint(SpringLayout.NORTH, FilterTo, 0, SpringLayout.SOUTH, NoFocus);
			filterLayout.putConstraint(SpringLayout.WEST, FilterTo, 0, SpringLayout.WEST, NoFocus);
			filterLayout.putConstraint(SpringLayout.EAST, FilterTo, 0, SpringLayout.EAST, NoFocus);

			if (UserSettings.getBooleanAppletProperty("TopicGUI_InUse", false)){
				filterLayout.putConstraint(SpringLayout.NORTH, currentFavoritesOnly, 10, SpringLayout.SOUTH, NoSingles);
				filterLayout.putConstraint(SpringLayout.WEST, currentFavoritesOnly, 0, SpringLayout.WEST, NoSingles);
				filterLayout.putConstraint(SpringLayout.EAST, currentFavoritesOnly, 3 * position - 5, SpringLayout.WEST,
						NoSingles);

				if (UserSettings.getBooleanAppletProperty("TopicEdgeSelection_Visible", false)){
					filterLayout.putConstraint(SpringLayout.NORTH, topicSelection[0], 0, SpringLayout.SOUTH,
							currentFavoritesOnly);
					filterLayout.putConstraint(SpringLayout.WEST, topicSelection[0], 0, SpringLayout.WEST, orphans);
					filterLayout.putConstraint(SpringLayout.EAST, topicSelection[0], position, SpringLayout.WEST, orphans);

					filterLayout.putConstraint(SpringLayout.NORTH, topicSelection[1], 0, SpringLayout.SOUTH,
							currentFavoritesOnly);
					filterLayout
							.putConstraint(SpringLayout.WEST, topicSelection[1], 0, SpringLayout.EAST, topicSelection[0]);
					filterLayout.putConstraint(SpringLayout.EAST, topicSelection[1], position, SpringLayout.EAST,
							topicSelection[0]);

					filterLayout.putConstraint(SpringLayout.NORTH, topicSelection[2], 0, SpringLayout.SOUTH,
							currentFavoritesOnly);
					filterLayout
							.putConstraint(SpringLayout.WEST, topicSelection[2], 0, SpringLayout.EAST, topicSelection[1]);
					filterLayout.putConstraint(SpringLayout.EAST, topicSelection[2], position, SpringLayout.EAST,
							topicSelection[1]);

					filterLayout.putConstraint(SpringLayout.NORTH, frame, 5, SpringLayout.SOUTH, NoSingles);
					filterLayout.putConstraint(SpringLayout.WEST, frame, 5, SpringLayout.WEST, cmdFilterPanel);
					filterLayout.putConstraint(SpringLayout.EAST, frame, -5, SpringLayout.EAST, cmdFilterPanel);
					filterLayout.putConstraint(SpringLayout.SOUTH, frame, 5, SpringLayout.SOUTH, topicSelection[0]);
				} else{
					filterLayout.putConstraint(SpringLayout.NORTH, frame, 5, SpringLayout.SOUTH, NoSingles);
					filterLayout.putConstraint(SpringLayout.WEST, frame, 5, SpringLayout.WEST, cmdFilterPanel);
					filterLayout.putConstraint(SpringLayout.EAST, frame, -5, SpringLayout.EAST, cmdFilterPanel);
					filterLayout.putConstraint(SpringLayout.SOUTH, frame, 5, SpringLayout.SOUTH, currentFavoritesOnly);
				}

				filterLayout.putConstraint(SpringLayout.NORTH, sp, 5, SpringLayout.SOUTH, frame);
			} else
				filterLayout.putConstraint(SpringLayout.NORTH, sp, 5, SpringLayout.SOUTH, FilterTo);

			filterLayout.putConstraint(SpringLayout.WEST, sp, 0, SpringLayout.WEST, cmdFilterPanel);
			filterLayout.putConstraint(SpringLayout.EAST, sp, 0, SpringLayout.EAST, cmdFilterPanel);
			filterLayout.putConstraint(SpringLayout.SOUTH, sp, 0, SpringLayout.SOUTH, cmdFilterPanel);

			cmdFilterPanel.doLayout();
		}

	}

	public void repaintTree(){
		ftree.treeDidChange();
	}

	public void untickConnectedOnly(){
		if (connectedOnlyBtn.isSelected())
			connectedOnlyBtn.doClick();
	}

	public void resetHalos(){
		ftree.resetHalos();
	}
}
