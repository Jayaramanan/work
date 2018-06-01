/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.datalist;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.controller.graph.GraphController;
import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.DataFilter;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.metaphor.NumericMetaphor;
import com.ni3.ag.navigator.client.domain.query.Query;
import com.ni3.ag.navigator.client.domain.query.Section;
import com.ni3.ag.navigator.client.gateway.GraphGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpGraphGatewayImpl;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.gui.Ni3Panel;
import com.ni3.ag.navigator.client.gui.datalist.MatrixSortOrder.SortColumn;
import com.ni3.ag.navigator.client.gui.graph.Node;

@SuppressWarnings("serial")
public class ItemsPanel extends Ni3Panel implements ItemsListListener{
	private GraphController graphController;

	private JTabbedPane topListPane;
	private ArrayList<DBObjectList> allList;

	public ItemsPanel(MainPanel parent){
		super(parent);
		graphController = new GraphController(parent);
		createComponents();
		layoutComponents();

		Doc.registerListener(this);
	}

	public int getListenerType(){
		return Ni3ItemListener.SRC_ItemsPanel;
	}

	public Dimension getPreferredSize(){
		return new Dimension(300, 200);
	}

	public void clear(){
		for (DBObjectList l : allList)
			l.clear();

		updateHeader();

		validate();
		repaint();
	}

	public void removeObjectFromList(DBObject obj){
		for (DBObjectList list : allList){
			if (list.ID == obj.getEntity().ID){
				list.listDescription.getModel().removeRow(obj);
				break;
			}
		}
		updateHeader();
	}

	private void applyPrefilter(DataFilter prefilter){
		for (DBObjectList list : allList){
			final List<DBObject> toRemove = new ArrayList<DBObject>();
			for (DataItem item : list.getItems()){
				if (prefilter.isObjectFilteredOut(item.obj)){
					toRemove.add(item.obj);
				}
			}
			if (!toRemove.isEmpty()){
				final DataSetTableModel model = list.listDescription.getModel();
				for (DBObject obj : toRemove){
					model.removeRow(obj);
				}
				model.fireTableDataChanged();
			}
		}
		updateHeader();
	}

	public void updateHeader(){
		int index = 0;

		for (DBObjectList l : allList){
			String title = l.Name;
			title += l.listDescription.updateHeader();
			topListPane.setTitleAt(index, title);

			if (l.listDescription.getTotal() == l.listDescription.getSelected())
				l.searchResultSelectAll.setSelected(true);

			if (l.listDescription.getSelected() == 0)
				l.searchResultSelectAll.setSelected(false);

			index++;
		}
	}

	public void setToFirstNonEmpty(){
		int index = 0;

		for (DBObjectList l : allList){
			int total;

			total = l.listDescription.getObjCount();

			if (total > 0){
				topListPane.setSelectedIndex(index);
				return;
			}

			index++;
		}
	}

	public void itemSelected(DBObject node, int index, int ClickCount, int Modifier){
		if (ClickCount == 2) // double click
		{
			ArrayList<Integer> DBRoots = new ArrayList<Integer>();
			DBRoots.add(node.getId());

			GraphGateway graphGateway = new HttpGraphGatewayImpl();
			List<Node> nodes = graphGateway.getNodes(DBRoots, Doc.SchemaID, Doc.DB.getDataFilter());
			if (nodes == null){
				Doc.undoredoManager.back();
			} else{
				Doc.showSubgraph(nodes, true);
			}

		} else if (ClickCount == -1) // mouse exited
		{
			Doc.setMatrixPointedNode(null);
		} else if (ClickCount == 0) // mouse over
		{
			Doc.setMatrixPointedNode(Doc.Subgraph.findNode(node.getId()));
		}

	}

	public void itemChecked(DataItem item, int index, boolean Status){
		if (index == -27){
			updateHeader();
		} else if (index >= 0){
			final DBObject node = item.obj;
			if (!Status){
				graphController.removeNodeFromGraph(node);
				item.setNode(null);
			} else{
				graphController.addNodeToGraph(node, null);
				Node n = parentMP.Doc.Subgraph.findNode(node.getId());
				if (n != null){
					n.fixed = true;
				}
				item.setNode(n);
			}

			invalidate();
			updateHeader();
		}
	}

	public void clearCheckSearch(){
		for (DBObjectList t : allList){
			t.searchResultSelectAll.setSelected(false);
			t.clearCheck();
		}
	}

	public void setSearchResults(List<DBObject> data){
		Doc.setShowOnlyDisplayedNodesInMatrix(false);
		setListData(data, false, false);
		setToFirstNonEmpty();

		showMatrix();

		validate();
	}

	public void setSearchOrder(Query query){
		for (DBObjectList l : allList){
			l.listDescription.clearOrder();
			for (Section s : query.getSections()){
				if (l.listDescription.getEntity() == s.getEnt()){
					l.listDescription.setOrder(s);
					break;
				}
			}

		}
	}

	private void showDataSet(boolean initialCheck, boolean initialStatus){
		List<DBObject> ds = getFilteredDataSet();
		if (Doc.isShowOnlyDisplayedNodesInMatrix()){
			removeInvisibleNodes();
		}
		if (ds != null){
			setListData(ds, initialCheck, initialStatus);
		} else{
			clear();
		}
		reloadNumericMetaphors(Doc.isShowNumericMetaphors());
	}

	private void setListData(List<DBObject> data, boolean initialCheck, boolean initialStatus){
		for (DBObjectList l : allList){
			l.setItems(data, initialCheck, initialStatus, false);
		}

		updateHeader();

		if (Doc.isShowNumericMetaphors()){
			reloadNumericMetaphors(true);
		}

		validate();
		repaint();
	}

	private List<DBObject> getFilteredDataSet(){
		List<DBObject> result = Doc.DS;
		if (Doc.isShowOnlyDisplayedNodesInMatrix()){
			result = Doc.Subgraph.getDisplayedDataSet();
		}
		return result;
	}

	private void removeInvisibleNodes(){
		final List<DBObject> ds = getFilteredDataSet();
		for (DBObjectList l : allList){
			final DataSetTableModel model = l.listDescription.getModel();
			boolean removed = false;
			int row = 0;
			while (row < model.getRowCount()){
				final DataItem dbObject = model.getDBObjectAt(row);

				if (!ds.contains(dbObject.obj)){
					model.removeRow(row);
					removed = true;
				} else{
					row++;
				}
			}
			if (removed){
				model.fireTableDataChanged();
			}
		}
	}

	public void reloadNumericMetaphors(boolean showNumericMetaphors){
		int number = 0;
		for (DBObjectList l : allList){
			final List<DataItem> items = l.getItems();
			for (DataItem item : items){
				Node res = Doc.Subgraph.findNode(item.obj.getId());
				boolean isGeoCoded = item.getNode() != null && item.getNode().isGeoCoded() && res != null && res.isActive();
				if (showNumericMetaphors && isGeoCoded){
					final NumericMetaphor metaphor = new NumericMetaphor(++number);
					item.obj.setNumericMetaphor(metaphor);
				} else{
					item.obj.setNumericMetaphor(null);
				}
			}
		}
		validate();
		repaint();
	}

	public void showMatrix(){
		parentMP.mainSplit.setDividerLocation(parentMP.mainSplit.getHeight() - 200);

		validate();
		repaint();
	}

	private void createComponents(){
		topListPane = new JTabbedPane();
		topListPane.setName("ItemsPanelTabbedPane");

		createTabList();
	}

	private void createTabList(){
		topListPane.removeAll();

		allList = new ArrayList<DBObjectList>(10);

		for (Entity e : parentMP.Doc.DB.schema.definitions){
			if (e.isNode() && e.CanRead){
				boolean founded = false;
				for (DBObjectList p : allList){
					if (p.ID == e.ID)
						founded = true;
				}

				if (!founded){
					DBObjectList l = createList(e);
					allList.add(l);

					topListPane.addTab(e.Name, null, l, e.getDescription());
				}
			}
		}
	}

	private DBObjectList createList(Entity ent){
		DBObjectList ret = new DBObjectList(parentMP, ent, ent.Name, ent.ID);

		ret.addItemsListListener(this);

		return ret;
	}

	private void layoutComponents(){
		// header for Search results

		Doc.setSearchNew(true);

		setLayout(new BorderLayout());
		add(topListPane, "Center");
	}

	public void onSchemaChanged(int SchemaID){
		createTabList();
	}

	@Override
	public void onChartFilterChanged(){
		for (DBObjectList l : allList){
			l.listDescription.refreshFixed();
		}
		updateHeader();
	}

	@Override
	public void onDynamicAttributeAdded(Attribute newAttr){
		for (DBObjectList l : allList){
			final Entity entity = l.listDescription.getEntity();
			if (entity.ID == newAttr.ent.ID){
				final List<Attribute> attributes = entity.getAttributesSortedForMatrix(Doc.isCurrentTopic());
				l.listDescription.changeTableStructure(attributes);
			}
		}

		validate();
		repaint();
	}

	@Override
	public void onRemoveDynamicAttributes(){
		for (DBObjectList l : allList){
			final Entity entity = l.listDescription.getEntity();
			final List<Attribute> attributes = entity.getAttributesSortedForMatrix(Doc.isCurrentTopic());
			if (entity.hasDynamicAttributes() || attributes.size() != l.listDescription.getAttributeCount()){
				l.listDescription.changeTableStructure(attributes);
			}
		}
	}

	public void onClearSearchResult(){
		clear();
	}

	public ArrayList<DBObjectList> getMatrixLists(){
		return allList;
	}

	private void setSortOrder(MatrixSortOrder order){
		for (DBObjectList l : allList){
			l.getOrder().clear();
		}

		if (order != null && order.getSorts() != null){
			for (DBObjectList l : allList){
				for (SortColumn sc : order.getSorts()){
					if (sc.getEntityId() == l.ID){
						int column = sc.getColumn();
						if (sc.getAttr() != null){
							final int col = l.listDescription.getColumnByAttributeId(sc.getAttr().ID);
							if (col >= 0){
								column = col;
							}
						}
						l.getOrder().addSort(column, sc.getAttr(), sc.getEntityId(), sc.isAsc());
					}
				}
				l.listDescription.sort();
			}
		}
	}

	private void changeTableStructure(boolean force){
		for (DBObjectList l : allList){
			final Entity entity = l.listDescription.getEntity();
			if (entity.hasContextAttributes() || force){
				final List<Attribute> attributes = entity.getAttributesSortedForMatrix(Doc.isCurrentTopic());
				l.listDescription.changeTableStructure(attributes);
			}
		}
	}

	// TODO objects, that are not in subgraph should be stored in model (Ni3Document), and not searched in table model
	private void clearDynamicValuesForNotInSubgraphObjects(){
		for (DBObjectList l : allList){
			final Entity entity = l.listDescription.getEntity();
			if (entity.hasDynamicAttributes()){
				final List<Attribute> attributes = entity.getDynamicAttributes();
				for (DataItem item : l.listDescription.getItems()){
					if (item.getNode() == null || Doc.Subgraph.findNode(item.obj.getId()) == null){
						for (Attribute attribute : attributes){
							item.obj.setValue(attribute.ID, null);
						}
					}
				}
			}
			l.listDescription.getScrollableModel().fireTableDataChanged();
		}
	}

	@Override
	public void event(int eventCode, int sourceID, Object source, Object param){
		super.event(eventCode, sourceID, source, param);
		switch (eventCode){
			case MSG_NodeRemoved:
				DBObject node = (DBObject) param;
				removeObjectFromList(node);
				break;
			case MSG_PreFilterChanged:
				DataFilter prefilter = (DataFilter) param;
				applyPrefilter(prefilter);
			case MSG_ClearSubgraph:
				clearCheckSearch();
				showDataSet(true, true);
				break;
			case MSG_SubgraphChanged:
			case MSG_NewSubgraph:
			case MSG_FilterChanged:
			case MSG_ChartFilterChanged:
			case MSG_DataSetChanged:
			case MSG_ShowOnlyDisplayedNodesInMatrixChanged:
				showDataSet(true, true);
				clearDynamicValuesForNotInSubgraphObjects();
				break;
			case MSG_MetaphorSetChanged:
			case MSG_ClearFavorite:
				validate();
				repaint();
				break;
			case MSG_ShowNumericMetaphorsChanged:
				reloadNumericMetaphors((Boolean) param);
				break;
			case MSG_TopicModeChanged:
				changeTableStructure(false);
				break;
			case MSG_SnaChartChanged:
				changeTableStructure(true);
				break;
			case MSG_MatrixSortChanged:
				setSortOrder((MatrixSortOrder) param);
				break;

		}
	}

	public List<DataItem> getAllItems(){
		List<DataItem> items = new ArrayList<DataItem>();
		for (DBObjectList list : allList){
			items.addAll(list.getItems());
		}
		return items;
	}
}
