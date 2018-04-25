/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.datalist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.SystemGlobals;

@SuppressWarnings("serial")
public class DataSetTableModel extends AbstractTableModel{
	private int fixedColumns;
	private List<DataItem> items;
	private Map<Integer, DataItem> hash;
	private boolean fixedPart;
	private Entity entity;
	private int shift;
	private DataSetTable parent;
	private List<Attribute> attributes;

	public static final int TOTAL_SCOPE_ALL = 0;
	public static final int TOTAL_SCOPE_GRAPH = 1;
	private int headerTotalScope; // 0 - all, 1 - on graph

	public DataSetTableModel(DataSetTable parent, Entity ent, List<Attribute> attributes, List<DBObject> items,
			int fixedColumns, boolean fixedPart, boolean initialCheck, MatrixSortOrder order){
		headerTotalScope = TOTAL_SCOPE_ALL;
		this.parent = parent;
		this.entity = ent;
		this.attributes = attributes;
		this.fixedColumns = fixedColumns;
		this.fixedPart = fixedPart;
		if (fixedPart)
			shift = -2;
		else
			shift = fixedColumns;

		this.items = Collections.synchronizedList(new ArrayList<DataItem>());
		this.hash = new Hashtable<Integer, DataItem>();

		if (items == null)
			return;
		for (DBObject n : items){
			if (n != null && n.getEntity() != null && ent != null && n.getEntity().ID == ent.ID){
				DataItem di = new DataItem(n, initialCheck);
				this.items.add(di);
				this.hash.put(n.getId(), di);

				if (initialCheck){
					di.setNode(findNode(n));
				}
			}
		}

		Collections.sort(this.items, new DataItemComparator(order));
	}

	public DataSetTableModel(DataSetTable parent, Entity ent, List<Attribute> attributes, DataSetTableModel syncModel,
			int FixedColumns, boolean FixedPart){
		this.parent = parent;
		this.entity = ent;
		this.attributes = attributes;
		this.fixedColumns = FixedColumns;
		this.fixedPart = FixedPart;
		if (FixedPart)
			shift = -2;
		else
			shift = FixedColumns;

		this.items = syncModel.items;
		this.hash = new HashMap<Integer, DataItem>();
	}

	public List<DataItem> getItems(){
		return Collections.unmodifiableList(items);
	}

	public void setItems(List<DataItem> items){
		this.items.clear();
		fireTableDataChanged();
		this.hash.clear();
		for (DataItem item : items){
			this.items.add(item);
			hash.put(item.obj.getId(), item);
		}
		fireTableDataChanged();
	}

	public void addItems(List<DBObject> items, boolean initialCheck, boolean initialStatus, MatrixSortOrder order){
		if (initialCheck){
			for (DataItem di : this.items){
				di.setChecked(false);
			}

			for (DBObject obj : items){
				setSelected(obj, initialStatus, false);
			}

		}

		for (DBObject n : items){
			if (n != null && n.getEntity() != null && entity != null && n.getEntity().ID == entity.ID){
				DataItem di;
				if (!hash.containsKey(n.getId())){
					di = new DataItem(n, initialCheck);
					this.items.add(di);
					this.hash.put(n.getId(), di);
				} else{
					di = hash.get(n.getId());
					di.obj = n;
				}

				if (initialCheck){
					di.setChecked(initialCheck);
					di.setNode(findNode(n));
				}
			}
		}

		Collections.sort(this.items, new DataItemComparator(order));
		fireTableDataChanged();
	}

	private Node findNode(DBObject n){
		return SystemGlobals.MainFrame.Doc.Subgraph.findNode(n.getId());
	}

	public int getColumnCount(){
		int result;
		if (fixedPart){
			result = fixedColumns + 2;
		} else{
			result = attributes.size() - fixedColumns;
		}
		return result;
	}

	public int getRowCount(){
		return items.size();
	}

	public String getColumnDescription(int col){
		final int index = col + shift;
		if (index >= 0 && index < attributes.size()){
			return attributes.get(index).description;
		}

		return "";
	}

	public String getColumnName(int col){
		if (col + shift >= 0){
			final Attribute attribute = attributes.get(col + shift);
			if (attribute.predefined || attribute.multivalue)
				return attribute.label;

			if (attribute.isNumericAttribute() && attribute.isAggregable()){
				double total = 0.0;
				double res;
				int count = 0;

				for (DataItem item : items){
					if (headerTotalScope == TOTAL_SCOPE_ALL || (headerTotalScope == TOTAL_SCOPE_GRAPH && item.isChecked())){
						double val = item.obj.getValueAsDouble(attribute);
						total += val;
						count++;
					}
				}

				res = total;

				if (parent.getHeaderTotalOperation()[col + shift] != 0){
					switch (parent.getHeaderTotalOperation()[col + shift]){
						case 1:
							res = total;
							break;

						case 2:
							if (count > 0)
								res = total / count;
							else
								res = 0;
							break;
					}
					return "<HTML><P align=center><U>" + attribute.label + "</U><BR>"
							+ attribute.displayValueAsPartOfHTML(res) + "</P></HTML>";
				}

			}

			return attribute.label;
		} else{
			if (SystemGlobals.isMarathonTesting()){
				return String.valueOf(col);
			} else{
				return "";
			}
		}
	}

	public Attribute getAttribute(int col){
		if (col + shift >= 0)
			return attributes.get(col + shift);
		else
			return null;
	}

	public DataItem getDBObjectAt(int row){
		return items.get(row);
	}

	public Object getValueAt(int row, int col){
		if (col + shift >= attributes.size())
			return null;
		if (row >= items.size())
			return null;

		final DataItem item = items.get(row);
		final DBObject obj = item.obj;
		if (col + shift >= 0){
			final Attribute attribute = attributes.get(col + shift);
			if (!attribute.isSnaAttribute())
				return attribute.displayValueAsFullHTML(obj.getValue(attribute.ID));
			else
				return getSnaValue(attribute, item);
		}

		if (col + shift == -2){
			return item.isDisplayed();
		}

		if (col + shift == -1)
			if (obj.getNumericMetaphor() != null){
				return obj.getNumericMetaphor();
			} else
				return obj.getIcon();

		return null;
	}

	private Object getSnaValue(Attribute attribute, DataItem item){
		Object value = null;
		if (item.isChecked()){
			Node node = item.getNode();
			if (node != null){
				value = node.getSnaValue(attribute.getSnaAttribute());
			}
			value = attribute.displayValue(value);
		}
		return value;
	}

	public void setValuesAt(Object aValue, int rowIndexes[]){
		DataItem item[] = new DataItem[rowIndexes.length];
		int count = 0;

		for (int n : rowIndexes){
			item[count] = items.get(n);
			item[count].setChecked((Boolean) aValue);
			count++;
		}

		for (DataItem i : item){
			parent.notifyListeners(i, 0, (Boolean) aValue);
		}
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		if (columnIndex == 0){
			DataItem item = items.get(rowIndex);
			item.setChecked((Boolean) aValue);

			parent.notifyListeners(item, 0, (Boolean) aValue);
		}
	}

	public void removeRow(int row){
		DataItem item = items.get(row);
		items.remove(row);
		hash.remove(item.obj.getId());

		parent.notifyListeners(null, -27, false);
	}

	public void removeRow(DBObject obj){
		for (DataItem data : items){
			if (data.obj.getId() == obj.getId()){
				items.remove(data);
				hash.remove(obj.getId());
				parent.notifyListeners(null, -27, false);

				break;
			}
		}
	}

	public void setSelected(DBObject obj, boolean status, boolean fireEvent){
		for (DataItem data : items){
			if (data.obj.getId() == obj.getId()){
				if (data.isChecked() != status){
					data.setChecked(status);
					if (status){
						data.setNode(findNode(obj));
					} else{
						data.setNode(null);
					}

					if (fireEvent){
						parent.notifyListeners(null, -27, false);
					}
				}
				break;
			}
		}
	}

	public int getSelectedCount(){
		int count = 0;
		for (DataItem item : items){
			if (item.isDisplayed()){
				count++;
			}
		}
		return count;
	}

	public void clearCheck(){
		for (DataItem data : items){
			data.setChecked(false);
			data.setNode(null);
		}

		parent.notifyListeners(null, -27, false);
	}

	public void sortByColumn(MatrixSortOrder order){
		Collections.sort(items, new DataItemComparator(order));
		fireTableDataChanged();
	}

	public List<Integer> getNodeIds(){
		List<Integer> ids = new ArrayList<Integer>();
		for (DataItem item : items){
			ids.add(item.obj.getId());
		}
		return ids;
	}

	public void setHeaderTotalOperation(int col, int newOperation){
		if (col + shift >= 0 && parent.getHeaderTotalOperation().length > col + shift){
			parent.getHeaderTotalOperation()[col + shift] = newOperation;
		}
	}

	public int getFixedColumns(){
		return fixedColumns;
	}

	public int getShift(){
		return shift;
	}

	public void setHeaderTotalScope(int newScope){
		headerTotalScope = newScope;
	}
}
