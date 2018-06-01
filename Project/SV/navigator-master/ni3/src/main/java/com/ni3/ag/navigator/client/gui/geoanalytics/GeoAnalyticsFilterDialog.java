/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.geoanalytics;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.TableColumn;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.gui.CheckBoxHeader;
import com.ni3.ag.navigator.shared.domain.GeoTerritory;

public class GeoAnalyticsFilterDialog extends Ni3Dialog{
	private static final long serialVersionUID = 2623841779760040067L;
	private JButton applyButton;
	private JButton cancelButton;
	private JTable filterTable;
	private JScrollPane tableScroll;
	private CheckBoxHeader headerRenderer;

	private JPanel mainPanel;

	public GeoAnalyticsFilterDialog(JFrame parent){
		super(parent);
		setTitle(UserSettings.getWord("TerritoryFilter"));
		initComponents();
		layoutComponents();
	}

	protected void initComponents(){
		setModal(true);
		setSize(new Dimension(300, 300));
		mainPanel = new JPanel();

		getContentPane().add(mainPanel);

		applyButton = new JButton(UserSettings.getWord("Ok"));
		cancelButton = new JButton(UserSettings.getWord("Cancel"));

		filterTable = new JTable();
		filterTable.getTableHeader().setReorderingAllowed(false);
		tableScroll = new JScrollPane();
		tableScroll.setViewportView(filterTable);
		filterTable.setModel(new GeoAnalyticsFilterTableModel(new ArrayList<GeoTerritory>()));

		mainPanel.add(applyButton);
		mainPanel.add(cancelButton);
		mainPanel.add(tableScroll);

		TableColumn column = filterTable.getColumnModel().getColumn(GeoAnalyticsFilterTableModel.SELECTION_COLUMN_INDEX);
		column.setPreferredWidth(35);
		column.setMinWidth(35);
		column.setMaxWidth(35);

		final ItemListener itemListener = new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e){
				Object source = e.getSource();
				if (source instanceof JCheckBox){
					boolean checked = ((JCheckBox) source).isSelected();
					final GeoAnalyticsFilterTableModel tableModel = getTableModel();
					tableModel.setAllSelected(checked);
					tableModel.fireTableDataChanged();
				}
			}
		};
		headerRenderer = new CheckBoxHeader(filterTable, itemListener);
		column.setHeaderRenderer(headerRenderer);
	}

	private void layoutComponents(){
		SpringLayout layout = new SpringLayout();
		mainPanel.setLayout(layout);

		layout.putConstraint(SpringLayout.NORTH, tableScroll, 10, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.WEST, tableScroll, 10, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.EAST, tableScroll, -10, SpringLayout.EAST, mainPanel);
		layout.putConstraint(SpringLayout.SOUTH, tableScroll, -40, SpringLayout.SOUTH, mainPanel);

		layout.putConstraint(SpringLayout.NORTH, cancelButton, 10, SpringLayout.SOUTH, tableScroll);
		layout.putConstraint(SpringLayout.EAST, cancelButton, -10, SpringLayout.EAST, mainPanel);

		layout.putConstraint(SpringLayout.NORTH, applyButton, 0, SpringLayout.NORTH, cancelButton);
		layout.putConstraint(SpringLayout.EAST, applyButton, -10, SpringLayout.WEST, cancelButton);

	}

	public void addApplyButtonListener(ActionListener l){
		applyButton.addActionListener(l);
	}

	public void addCancelButtonListener(ActionListener l){
		cancelButton.addActionListener(l);
	}

	public Set<GeoTerritory> getFilteredOutTerritories(){
		GeoAnalyticsFilterTableModel model = getTableModel();
		final Set<GeoTerritory> territories = model.getFilteredOutTerritories();
		return territories;
	}

	private GeoAnalyticsFilterTableModel getTableModel(){
		return (GeoAnalyticsFilterTableModel) filterTable.getModel();
	}

	public void setTableData(List<GeoTerritory> allGeoTerritories, Set<GeoTerritory> filteredOutTerritories){
		GeoAnalyticsFilterTableModel model = getTableModel();
		headerRenderer.setSelected(filteredOutTerritories.isEmpty());
		model.setData(allGeoTerritories, filteredOutTerritories);
	}
}
