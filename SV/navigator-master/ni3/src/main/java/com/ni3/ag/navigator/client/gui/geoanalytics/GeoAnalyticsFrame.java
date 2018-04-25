/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.geoanalytics;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.event.ChangeListener;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.ColorTableCellEditor;
import com.ni3.ag.navigator.client.gui.ColorTableCellRenderer;
import com.ni3.ag.navigator.client.gui.common.BigComboBoxDropDownUIFactory;
import com.ni3.ag.navigator.client.gui.common.Ni3Frame;
import com.ni3.ag.navigator.client.gui.search.DescriptionTableCellEditor;
import com.ni3.ag.navigator.shared.domain.Cluster;
import com.ni3.ag.navigator.shared.domain.GeoObjectSource;
import com.ni3.ag.navigator.shared.domain.GisTerritory;

public class GeoAnalyticsFrame extends Ni3Frame{
	private static final long serialVersionUID = 2623841779760040067L;
	private JButton loadButton;
	private JButton showButton;
	private JButton filterButton;
	private JButton saveButton;
	private JButton deleteButton;
	private JComboBox entityCombo;
	private JComboBox attributeCombo;
	private JComboBox sourceCombo;
	private JComboBox clusterCountCombo;
	private JComboBox layerCombo;
	private JRadioButton avgButton;
	private JRadioButton sumButton;
	private Diagram2D diagram;
	private JLabel entityLabel;
	private JLabel attributeLabel;
	private JLabel clusterCountLabel;
	private JLabel sourceLabel;
	private JLabel layerLabel;
	private MultiSlider clusterSlider;
	private JTable clusterTable;
	private JScrollPane clusterScroll;
	private JSplitPane splitPane;

	private JPanel mainPanel;
	private JPanel leftPanel;

	public GeoAnalyticsFrame(){
		super();
		setTitle(UserSettings.getWord("GeoAnalytics"));
		initComponents();
		fillClusterCountCombo();
		fillSourceCombo();
	}

	protected void initComponents(){
		setSize(new Dimension(800, 500));
		setMinimumSize(new Dimension(580, 300));
		mainPanel = new JPanel();

		getContentPane().add(mainPanel);

		loadButton = new JButton(UserSettings.getWord("GetTerritories"));
		showButton = new JButton(UserSettings.getWord("Show"));
		filterButton = new JButton(UserSettings.getWord("TerritoryFilter"));
		saveButton = new JButton(UserSettings.getWord("Save"));
		deleteButton = new JButton(UserSettings.getWord("Delete"));
		entityCombo = new JComboBox();
		entityLabel = new JLabel(UserSettings.getWord("Entity"));
		attributeCombo = new JComboBox();
		attributeLabel = new JLabel(UserSettings.getWord("Attribute"));
		sourceCombo = new JComboBox();
		sourceLabel = new JLabel(UserSettings.getWord("Source"));
		layerCombo = new JComboBox();
		layerLabel = new JLabel(UserSettings.getWord("Layer"));

		clusterCountCombo = new JComboBox();
		clusterCountLabel = new JLabel(UserSettings.getWord("ClusterCount"));
		clusterSlider = new MultiSlider(0, 10);
		clusterSlider.setSliderItems(new ArrayList<Integer>());

		sumButton = new JRadioButton(UserSettings.getWord("Sum"));
		avgButton = new JRadioButton(UserSettings.getWord("Avg"));
		final ButtonGroup calcModeGroup = new ButtonGroup();
		calcModeGroup.add(sumButton);
		calcModeGroup.add(avgButton);
		sumButton.setSelected(true);

		diagram = new Diagram2D();

		clusterTable = new JTable();
		clusterScroll = new JScrollPane();
		clusterScroll.setViewportView(clusterTable);
		clusterScroll.setBorder(BorderFactory.createEmptyBorder());
		clusterTable.setModel(new ClusterTableModel(new ArrayList<Cluster>()));

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(450);
		splitPane.setResizeWeight(1.0);
		splitPane.setBorder(BorderFactory.createLineBorder(Color.blue));

		leftPanel = new JPanel();

		clusterTable.setDefaultRenderer(Color.class, new ColorTableCellRenderer());
		clusterTable.setDefaultEditor(Color.class, new ColorTableCellEditor(this));
		clusterTable.setDefaultEditor(String.class, new DescriptionTableCellEditor());

		sourceCombo.setRenderer(new SourceListCellRenderer());

		mainPanel.add(loadButton);
		mainPanel.add(showButton);
		mainPanel.add(filterButton);
		mainPanel.add(saveButton);
		mainPanel.add(deleteButton);
		mainPanel.add(entityCombo);
		mainPanel.add(attributeCombo);
		mainPanel.add(sourceCombo);
		mainPanel.add(clusterCountCombo);
		mainPanel.add(layerCombo);
		mainPanel.add(sumButton);
		mainPanel.add(avgButton);
		mainPanel.add(entityLabel);
		mainPanel.add(attributeLabel);
		mainPanel.add(clusterCountLabel);
		mainPanel.add(sourceLabel);
		mainPanel.add(layerLabel);
		mainPanel.add(splitPane);
		splitPane.setLeftComponent(leftPanel);
		leftPanel.add(diagram);
		leftPanel.add(clusterSlider);
		splitPane.setRightComponent(clusterScroll);

		layoutComponents();

		attributeCombo.setUI(BigComboBoxDropDownUIFactory.getNativeCustomComboBoxUI());
		entityCombo.setUI(BigComboBoxDropDownUIFactory.getNativeCustomComboBoxUI());
	}

	private void layoutComponents(){
		SpringLayout layout = new SpringLayout();
		mainPanel.setLayout(layout);
		SpringLayout leftLayout = new SpringLayout();
		leftPanel.setLayout(leftLayout);

		layout.putConstraint(SpringLayout.NORTH, sourceCombo, 10, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.WEST, sourceCombo, 90, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.EAST, sourceCombo, 120, SpringLayout.WEST, sourceCombo);
		layout.putConstraint(SpringLayout.NORTH, sourceLabel, 2, SpringLayout.NORTH, sourceCombo);
		layout.putConstraint(SpringLayout.EAST, sourceLabel, -10, SpringLayout.WEST, sourceCombo);

		layout.putConstraint(SpringLayout.NORTH, entityCombo, 10, SpringLayout.SOUTH, sourceCombo);
		layout.putConstraint(SpringLayout.WEST, entityCombo, 0, SpringLayout.WEST, sourceCombo);
		layout.putConstraint(SpringLayout.EAST, entityCombo, 0, SpringLayout.EAST, sourceCombo);
		layout.putConstraint(SpringLayout.NORTH, entityLabel, 2, SpringLayout.NORTH, entityCombo);
		layout.putConstraint(SpringLayout.EAST, entityLabel, -10, SpringLayout.WEST, entityCombo);

		layout.putConstraint(SpringLayout.NORTH, attributeCombo, 10, SpringLayout.SOUTH, entityCombo);
		layout.putConstraint(SpringLayout.WEST, attributeCombo, 0, SpringLayout.WEST, entityCombo);
		layout.putConstraint(SpringLayout.EAST, attributeCombo, 0, SpringLayout.EAST, entityCombo);
		layout.putConstraint(SpringLayout.NORTH, attributeLabel, 2, SpringLayout.NORTH, attributeCombo);
		layout.putConstraint(SpringLayout.EAST, attributeLabel, -10, SpringLayout.WEST, attributeCombo);

		layout.putConstraint(SpringLayout.NORTH, layerCombo, 10, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.WEST, layerCombo, 300, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.EAST, layerCombo, 120, SpringLayout.WEST, layerCombo);
		layout.putConstraint(SpringLayout.NORTH, layerLabel, 2, SpringLayout.NORTH, layerCombo);
		layout.putConstraint(SpringLayout.EAST, layerLabel, -10, SpringLayout.WEST, layerCombo);

		layout.putConstraint(SpringLayout.NORTH, filterButton, 10, SpringLayout.SOUTH, layerCombo);
		layout.putConstraint(SpringLayout.EAST, filterButton, 0, SpringLayout.EAST, layerCombo);
		layout.putConstraint(SpringLayout.WEST, filterButton, 0, SpringLayout.WEST, layerCombo);

		layout.putConstraint(SpringLayout.NORTH, loadButton, 0, SpringLayout.NORTH, attributeCombo);
		layout.putConstraint(SpringLayout.EAST, loadButton, -10, SpringLayout.EAST, mainPanel);
		layout.putConstraint(SpringLayout.WEST, loadButton, -120, SpringLayout.EAST, loadButton);

		layout.putConstraint(SpringLayout.NORTH, splitPane, 10, SpringLayout.SOUTH, attributeCombo);
		layout.putConstraint(SpringLayout.WEST, splitPane, 10, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.EAST, splitPane, -10, SpringLayout.EAST, mainPanel);
		layout.putConstraint(SpringLayout.SOUTH, splitPane, -40, SpringLayout.SOUTH, mainPanel);

		leftLayout.putConstraint(SpringLayout.NORTH, diagram, 3, SpringLayout.NORTH, leftPanel);
		leftLayout.putConstraint(SpringLayout.WEST, diagram, 3, SpringLayout.WEST, leftPanel);
		leftLayout.putConstraint(SpringLayout.EAST, diagram, -3, SpringLayout.EAST, leftPanel);
		leftLayout.putConstraint(SpringLayout.SOUTH, diagram, -15, SpringLayout.SOUTH, leftPanel);

		leftLayout.putConstraint(SpringLayout.NORTH, clusterSlider, 0, SpringLayout.SOUTH, diagram);
		leftLayout.putConstraint(SpringLayout.WEST, clusterSlider, 0, SpringLayout.WEST, diagram);
		leftLayout.putConstraint(SpringLayout.EAST, clusterSlider, 0, SpringLayout.EAST, diagram);

		layout.putConstraint(SpringLayout.NORTH, clusterCountCombo, 10, SpringLayout.SOUTH, splitPane);
		layout.putConstraint(SpringLayout.WEST, clusterCountCombo, 0, SpringLayout.WEST, sourceCombo);
		layout.putConstraint(SpringLayout.EAST, clusterCountCombo, 40, SpringLayout.WEST, clusterCountCombo);
		layout.putConstraint(SpringLayout.NORTH, clusterCountLabel, 2, SpringLayout.NORTH, clusterCountCombo);
		layout.putConstraint(SpringLayout.EAST, clusterCountLabel, -10, SpringLayout.WEST, clusterCountCombo);

		layout.putConstraint(SpringLayout.NORTH, sumButton, 0, SpringLayout.NORTH, clusterCountCombo);
		layout.putConstraint(SpringLayout.WEST, sumButton, 30, SpringLayout.EAST, clusterCountCombo);

		layout.putConstraint(SpringLayout.NORTH, avgButton, 0, SpringLayout.NORTH, sumButton);
		layout.putConstraint(SpringLayout.WEST, avgButton, 20, SpringLayout.EAST, sumButton);

		layout.putConstraint(SpringLayout.NORTH, showButton, 0, SpringLayout.NORTH, sumButton);
		layout.putConstraint(SpringLayout.EAST, showButton, -10, SpringLayout.EAST, mainPanel);

		layout.putConstraint(SpringLayout.NORTH, saveButton, 0, SpringLayout.NORTH, showButton);
		layout.putConstraint(SpringLayout.EAST, saveButton, -10, SpringLayout.WEST, showButton);

		layout.putConstraint(SpringLayout.NORTH, deleteButton, 0, SpringLayout.NORTH, showButton);
		layout.putConstraint(SpringLayout.EAST, deleteButton, -10, SpringLayout.WEST, saveButton);
	}

	public void addEntityComboListener(ActionListener l){
		entityCombo.addActionListener(l);
	}

	public void addAttributeComboListener(ActionListener l){
		attributeCombo.addActionListener(l);
	}

	public void addLayerComboListener(ActionListener l){
		layerCombo.addActionListener(l);
	}

	public void addGetTerritoriesButtonListener(ActionListener l){
		loadButton.addActionListener(l);
	}

	public void addClusterCountComboListener(ActionListener l){
		clusterCountCombo.addActionListener(l);
	}

	public void addSumButtonListener(ActionListener l){
		sumButton.addActionListener(l);
	}

	public void addAvgButtonListener(ActionListener l){
		avgButton.addActionListener(l);
	}

	public void addShowButtonListener(ActionListener l){
		showButton.addActionListener(l);
	}

	public void addSliderChangeListener(ChangeListener l){
		clusterSlider.addChangeListener(l);
	}

	public void addFilterButtonListener(ActionListener l){
		filterButton.addActionListener(l);
	}

	public void addSourceComboListener(ActionListener l){
		sourceCombo.addActionListener(l);
	}

	public void addSaveButtonListener(ActionListener l){
		saveButton.addActionListener(l);
	}

	public void addDeleteButtonListener(ActionListener l){
		deleteButton.addActionListener(l);
	}

	public void addTableMouseListener(MouseListener l){
		clusterTable.addMouseListener(l);
	}

	public void fillEntitiesCombo(List<Entity> entities){
		entityCombo.removeAllItems();
		for (final Entity e : entities){
			entityCombo.addItem(e);
		}
	}

	public void fillAttributesCombo(List<Attribute> attributes){
		Attribute selectedItem = (Attribute) attributeCombo.getSelectedItem();
		attributeCombo.removeAllItems();
		for (final Attribute a : attributes){
			attributeCombo.addItem(a);
		}
		if (selectedItem != null)
			attributeCombo.setSelectedItem(selectedItem);
	}

	private void fillClusterCountCombo(){
		clusterCountCombo.removeAllItems();
		for (int i = 2; i <= 10; i++){
			clusterCountCombo.addItem(i);
		}
	}

	public void fillSourceCombo(){
		sourceCombo.removeAllItems();
		for (GeoObjectSource source : GeoObjectSource.values()){
			sourceCombo.addItem(source);
		}
		sourceCombo.setSelectedIndex(0);
	}

	public void fillLayerCombo(List<GisTerritory> territories){
		layerCombo.removeAllItems();
		for (GisTerritory gt : territories){
			if (gt.getTableName() != null && !gt.getTableName().isEmpty()){
				layerCombo.addItem(gt);
			}
		}
	}

	public Integer getSelectedClusterCount(){
		return (Integer) clusterCountCombo.getSelectedItem();
	}

	public void setSelectedClusterCount(int count){
		clusterCountCombo.setSelectedItem(count);
	}

	public Entity getSelectedEntity(){
		return (Entity) entityCombo.getSelectedItem();
	}

	public Attribute getSelectedAttribute(){
		return (Attribute) attributeCombo.getSelectedItem();
	}

	public GeoObjectSource getSelectedSource(){
		return (GeoObjectSource) sourceCombo.getSelectedItem();
	}

	public GisTerritory getSelectedLayer(){
		return (GisTerritory) layerCombo.getSelectedItem();
	}

	public boolean isSumMode(){
		return sumButton.isSelected();
	}

	public void setDiagramData(List<Double> data){
		diagram.setData(data);
		diagram.repaint();
	}

	public void setSliderValues(int territoryCount, List<Integer> positions){
		clusterSlider.setMin(0);
		clusterSlider.setMax(territoryCount - 1);
		clusterSlider.setSliderItems(positions);
		clusterSlider.repaint();
	}

	public void setSliderVisible(boolean b){
		clusterSlider.setVisible(b);
	}

	public List<Integer> getSliderValues(){
		return clusterSlider.getSliderValues();
	}

	public void setTableModel(ClusterTableModel model){
		clusterTable.setModel(model);
	}

	public void setTableData(List<Cluster> clusters){
		final ClusterTableModel model = (ClusterTableModel) clusterTable.getModel();
		model.setData(clusters);
		model.fireTableDataChanged();
	}

	public void refreshTable(){
		final ClusterTableModel model = (ClusterTableModel) clusterTable.getModel();
		model.fireTableDataChanged();
	}

	public void setFilterSelected(boolean b){
		filterButton.setSelected(b);
		filterButton.setForeground(b ? Color.BLUE : Color.BLACK);
	}

	public void setShowButtonEnabled(boolean enabled){
		showButton.setEnabled(enabled);
	}

	public void setSaveButtonEnabled(boolean enabled){
		saveButton.setEnabled(enabled);
	}

	public void stopTableEditing(){
		if (clusterTable.isEditing())
			clusterTable.getCellEditor().stopCellEditing();
	}

	public boolean isColorColumnAtPoint(Point point){
		boolean result = false;
		int column = clusterTable.columnAtPoint(point);
		if (column >= 0){
			int modelColumn = clusterTable.convertColumnIndexToModel(column);
			result = (modelColumn == ClusterTableModel.COLOR_COLUMN_INDEX);
		}
		return result;
	}

	public JTable getTable(){
		return clusterTable;
	}

}
