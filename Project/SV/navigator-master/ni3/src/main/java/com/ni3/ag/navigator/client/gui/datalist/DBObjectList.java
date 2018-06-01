/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.datalist;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.ni3.ag.navigator.client.controller.graph.GraphController;
import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.gui.Ni3Panel;

@SuppressWarnings("serial")
public class DBObjectList extends Ni3Panel implements ActionListener{
	final public DataSetTable listDescription;
	public JCheckBox searchResultSelectAll;
	private JCheckBox showNodesFromGraph;
	private JRadioButton headerTotalsAll;
	private JRadioButton headerTotalsGraph;
	private GraphController graphController;

	public int ID;
	public String Name;

	public DBObjectList(MainPanel parent, Entity ent, String name, int ID){
		super(parent);
		graphController = new GraphController(parent);

		this.ID = ID;
		this.Name = name;

		String selectAllLabel = getWord("Select all");
		searchResultSelectAll = new JCheckBox(selectAllLabel);

		searchResultSelectAll.addActionListener(this);

		headerTotalsAll = new JRadioButton(UserSettings.getWord("All"));
		headerTotalsAll.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				listDescription.setHeaderTotalScope(DataSetTableModel.TOTAL_SCOPE_ALL);
			}
		});
		headerTotalsAll.setSelected(true);

		headerTotalsGraph = new JRadioButton(UserSettings.getWord("Graph"));
		headerTotalsGraph.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				listDescription.setHeaderTotalScope(DataSetTableModel.TOTAL_SCOPE_GRAPH);
			}
		});

		ButtonGroup group = new ButtonGroup();
		group.add(headerTotalsAll);
		group.add(headerTotalsGraph);

		// This listbox holds the actual descriptions of list items.
		List<Attribute> attributes = ent.getAttributesSortedForMatrix(Doc.isCurrentTopic());
		listDescription = new DataSetTable(Doc, ent, attributes, false, true);

		listDescription.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		listDescription.addMouseMotionListener(new MouseMotionListener(){
			public void mouseMoved(MouseEvent me){
				DBObject obj = listDescription.objAtPoint(me.getPoint());

				if (obj != null)
					listDescription.notifyListeners(obj, 1, 0, 0);
			}

			public void mouseDragged(MouseEvent me){
			}
		});

		listDescription.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent me){
			}

			public void mouseEntered(MouseEvent me){
			}

			public void mouseExited(MouseEvent me){
				listDescription.notifyListeners(null, 1, -1, 0);
			}

		});

		setLayout(new BorderLayout());

		JPanel northJP = new JPanel();
		northJP.setLayout(new FlowLayout(FlowLayout.LEFT));
		northJP.add(searchResultSelectAll);
		if (UserSettings.getBooleanAppletProperty("SumValueFor_Visible", true)){
			northJP.add(new JLabel(UserSettings.getWord("Sum Value for:")));
			northJP.add(headerTotalsAll);
			northJP.add(headerTotalsGraph);
		}

		add(northJP, BorderLayout.NORTH);

		final String lbl = getWord("ShowOnlyDisplayedNodes");
		showNodesFromGraph = new JCheckBox(lbl);
		showNodesFromGraph.addActionListener(this);
		showNodesFromGraph.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		northJP.add(showNodesFromGraph);

		add(listDescription, BorderLayout.CENTER);

		setVisible(true);
		Doc.registerListener(this);
	}

	public int getListenerType(){
		return Ni3ItemListener.SRC_Other;
	}

	public void clear(){
		listDescription.setItems(null, false, true, true);
	}

	public int setItems(List<DBObject> items, boolean initialCheck, boolean initialStatus, boolean clearList){
		listDescription.setItems(items, initialCheck, initialStatus, clearList);
		repaint();

		return listDescription.rowCount();
	}

	public void addItemsListListener(ItemsListListener ill){
		listDescription.addItemsListListener(ill);
	}

	public void clearCheck(){
		listDescription.clearCheck();
	}

	private void deselectAll(){
		DataSetTableModel model = listDescription.getModel();

		int n, l;
		DataItem di;

		List<Integer> nodesToRemove = new ArrayList<Integer>();

		l = model.getRowCount();
		for (n = 0; n < l; n++){
			di = model.getDBObjectAt(n);

			if (di.isChecked()){
				nodesToRemove.add(di.obj.getId());
			}
		}

		graphController.removeNodesFromGraphByIds(nodesToRemove);
	}

	private void selectAll(){
		ArrayList<Integer> dbRoots = new ArrayList<Integer>();

		DataSetTableModel model = listDescription.getModel();

		int n, l;
		DataItem di;

		l = model.getRowCount();

		for (n = 0; n < l; n++){
			di = model.getDBObjectAt(n);
			if (!di.isChecked()){
				dbRoots.add(di.obj.getId());
			}
		}

		if (dbRoots.size() + Doc.Subgraph.getNodes().size() > Doc.DB.getMaximumNodeCount()){
			searchResultSelectAll.setSelected(false);
			parentMP.showNoResultWindow(MainPanel.TOO_MANY_SEARCH_RESULT);
			return;
		}

		graphController.addNodesToGraph(dbRoots, null);
	}

	public void actionPerformed(ActionEvent ae){
		if (searchResultSelectAll == ae.getSource()){
			if (!searchResultSelectAll.isSelected()){
				deselectAll();
			} else{
				selectAll();
			}
		} else if (showNodesFromGraph == ae.getSource()){
			if (showNodesFromGraph.isSelected() != Doc.isShowOnlyDisplayedNodesInMatrix()){
				Doc.setShowOnlyDisplayedNodesInMatrix(showNodesFromGraph.isSelected());
			}
		}
	}

	public List<DataItem> getItems(){
		return listDescription.getItems();
	}

	public MatrixSortOrder getOrder(){
		return listDescription.getOrder();
	}

	@Override
	public void event(int eventCode, int sourceID, Object source, Object param){
		switch (eventCode){
			case MSG_ShowOnlyDisplayedNodesInMatrixChanged:
				if (showNodesFromGraph.isSelected() != Doc.isShowOnlyDisplayedNodesInMatrix()){
					showNodesFromGraph.setSelected(Doc.isShowOnlyDisplayedNodesInMatrix());
				}
				break;
		}
	}
}
