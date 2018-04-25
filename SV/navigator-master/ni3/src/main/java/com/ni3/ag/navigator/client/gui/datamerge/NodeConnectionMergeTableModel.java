/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */

package com.ni3.ag.navigator.client.gui.datamerge;

import java.util.List;
import javax.swing.table.AbstractTableModel;

import com.ni3.ag.navigator.client.domain.NodeConnectionMergeRow;
import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.UserSettings;

public class NodeConnectionMergeTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	public static final int SELECTION_COLUMN_INDEX = 2;
	private static final int EDGE_COLUMN_INDEX = 0;
	private static final int NODE_COLUMN_INDEX = 1;

	private static final String[] columnsNames = new String[] { UserSettings.getWord("ConnectionType"),
	        UserSettings.getWord("DestinationNode"), UserSettings.getWord("KeepConnection") };
	private static final Class<?>[] columnClasses = new Class[] { String.class, String.class, Boolean.class };

	private List<NodeConnectionMergeRow> rows;

	@Override
	public Class<?> getColumnClass(int columnIndex){
		return columnClasses[columnIndex];
	}

	public NodeConnectionMergeTableModel(List<NodeConnectionMergeRow> rows){
		this.rows = rows;
	}

	public void setData(List<NodeConnectionMergeRow> rows){
		this.rows = rows;
		fireTableDataChanged();
	}

	public int getColumnCount(){
		return columnsNames.length;
	}

	public int getRowCount(){
		if (rows == null)
			return 0;
		return rows.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		Object value = null;
		final NodeConnectionMergeRow row = rows.get(rowIndex);
		switch (columnIndex){
			case EDGE_COLUMN_INDEX:
				final DBObject edge = row.getEdge();
				final Attribute attr = edge.getEntity().getAttribute(Attribute.CONNECTIONTYPE_ATTRIBUTE_NAME);
				final Object val = edge.getValue(attr.ID);
				value = attr.displayValue(val);
				break;
			case NODE_COLUMN_INDEX:
				value = row.getNode().getLabel();
				break;
			case SELECTION_COLUMN_INDEX:
				value = row.isSelected();
				break;
			default:
				break;
		}
		return value;
	}

	@Override
	public String getColumnName(int column){
		return columnsNames[column];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		return columnIndex == SELECTION_COLUMN_INDEX;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		final NodeConnectionMergeRow row = rows.get(rowIndex);
		switch (columnIndex){
			case SELECTION_COLUMN_INDEX:
				final Boolean selected = (Boolean) aValue;
				row.setSelected(selected);
				break;
			default:
				break;
		}
	}

	public List<NodeConnectionMergeRow> getRows(){
		return rows;
	}

}
