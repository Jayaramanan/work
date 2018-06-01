/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */

package com.ni3.ag.navigator.client.gui.datamerge;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.NodeMergeRow;
import com.ni3.ag.navigator.client.domain.UserSettings;

public class NodeMergeTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	public static final int SELECTION_COLUMN_INDEX = 3;
	private static final int ATTRIBUTE_COLUMN_INDEX = 0;
	public static final int VALUE_TO_COLUMN_INDEX = 1;
	public static final int VALUE_FROM_COLUMN_INDEX = 2;

	private static final String[] columnsNames = new String[] { UserSettings.getWord("Attribute"),
	        UserSettings.getWord("OldValue"), UserSettings.getWord("NewValue"), UserSettings.getWord("ApplyNewValue") };
	private static final Class<?>[] columnClasses = new Class[] { String.class, String.class, String.class, Boolean.class };

	private List<NodeMergeRow> rows;

	@Override
	public Class<?> getColumnClass(int columnIndex){
		return columnClasses[columnIndex];
	}

	public NodeMergeTableModel(List<NodeMergeRow> rows){
		this.rows = rows;
	}

	public void setData(List<NodeMergeRow> rows){
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
		final NodeMergeRow row = rows.get(rowIndex);
		Attribute attr = row.getAttribute();
		switch (columnIndex){
			case ATTRIBUTE_COLUMN_INDEX:
				value = attr.toString();
				break;
			case VALUE_TO_COLUMN_INDEX:
				value = attr.displayValue(row.getToValue());
				break;
			case VALUE_FROM_COLUMN_INDEX:
				value = attr.displayValue(row.getFromValue());
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
		boolean editable = false;
		if (columnIndex == SELECTION_COLUMN_INDEX){
			final NodeMergeRow row = rows.get(rowIndex);
			final Object fromValue = row.getFromValue();
			final Object toValue = row.getToValue();
			editable = (fromValue != null && !fromValue.equals(toValue)) || (toValue != null && !toValue.equals(fromValue));
		}
		return editable;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		final NodeMergeRow row = rows.get(rowIndex);
		switch (columnIndex){
			case SELECTION_COLUMN_INDEX:
				final Boolean selected = (Boolean) aValue;
				row.setSelected(selected);
				fireTableRowsUpdated(rowIndex, rowIndex);
				break;
			default:
				break;
		}
	}

	public List<NodeMergeRow> getRows(){
		return rows;
	}

}
