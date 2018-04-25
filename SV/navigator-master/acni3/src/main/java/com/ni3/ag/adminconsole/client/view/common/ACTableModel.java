/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

public abstract class ACTableModel extends AbstractTableModel implements ChangeResetable{
	private static final Logger log = Logger.getLogger(ACTableModel.class);
	private static final long serialVersionUID = 1637500155905864722L;
	private Hashtable<Integer, Hashtable<Integer, Boolean>> changedField = new Hashtable<Integer, Hashtable<Integer, Boolean>>();
	private boolean changed = false;

	private class ColumnDescription{
		public String name;
		public boolean editable;
		public Class<?> _class;
		public boolean mandatory;

		public ColumnDescription(String name, boolean editable, Class<?> class1, boolean mandatory){
			this.name = name;
			this.editable = editable;
			_class = class1;
			this.mandatory = mandatory;
		}
	}

	private List<ColumnDescription> columns = new ArrayList<ColumnDescription>();

	public void addColumn(String name, boolean editable, Class<?> clazz, boolean mandatory){
		columns.add(new ColumnDescription(name, editable, clazz, mandatory));
	}

	protected void removeColumns(List<String> names){
		List<ColumnDescription> target = new ArrayList<ColumnDescription>();
		for (ColumnDescription cd : columns)
			if (names.contains(cd.name))
				target.add(cd);
		if (target != null)
			columns.removeAll(target);
	}

	public Class<?> getColumnClass(int columnIndex){
		return columns.get(columnIndex)._class;
	}

	public int getColumnCount(){
		return columns.size();
	}

	public String getColumnName(int columnIndex){
		return columns.get(columnIndex).name;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex){
		return columns.get(columnIndex).editable;
	}

	public boolean isChanged(int row, int column){
		if (!changedField.containsKey(row))
			return false;
		Hashtable<Integer, Boolean> hrow = changedField.get(row);
		if (hrow.containsKey(column))
			return hrow.get(column);
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		Object val = getValueAt(rowIndex, columnIndex);
		if (val != null && !val.equals(aValue))
			setChanged(rowIndex, columnIndex);
		else if (aValue != null && !aValue.equals(val))
			setChanged(rowIndex, columnIndex);
		super.setValueAt(aValue, rowIndex, columnIndex);
	}

	private void setChanged(int rowIndex, int columnIndex){
		if (!changedField.containsKey(rowIndex))
			changedField.put(rowIndex, new Hashtable<Integer, Boolean>());
		Hashtable<Integer, Boolean> row = changedField.get(rowIndex);
		row.put(columnIndex, true);
	}

	private void setChangedRow(int rowIndex){
		for (int col = 0; col < getColumnCount(); col++){
			Object value = getValueAt(rowIndex, col);
			if (isCellEditable(rowIndex, col) && value != null)
				setChanged(rowIndex, col);
		}
	}

	@Override
	public void resetChanges(){
		changedField.clear();
		changed = false;
	}

	@Override
	public boolean isChanged(){
		return !changedField.isEmpty() || changed;
	}

	@Override
	public void fireTableRowsDeleted(int firstRow, int lastRow){
		for (int i = firstRow; i <= lastRow; i++)
			if (changedField.containsKey(i))
				changedField.remove(i);
		Hashtable<Integer, Hashtable<Integer, Boolean>> backup = changedField;
		changedField = new Hashtable<Integer, Hashtable<Integer, Boolean>>();
		Enumeration<Integer> rowIndexes = backup.keys();
		while (rowIndexes.hasMoreElements()){
			Integer rowIndex = rowIndexes.nextElement();
			if (rowIndex < firstRow)
				changedField.put(rowIndex, backup.get(rowIndex));
			else{
				log.debug("Reindexed row: " + rowIndex + " -> " + (rowIndex - (lastRow - firstRow)));
				changedField.put(rowIndex - (lastRow - firstRow + 1), backup.get(rowIndex));
			}
		}
		super.fireTableRowsDeleted(firstRow, lastRow);
		changed = true;
	}

	@Override
	public void fireTableRowsInserted(int firstRow, int lastRow){
		super.fireTableRowsInserted(firstRow, lastRow);
		setChangedRow(firstRow);
		changed = true;
	}

	public boolean isColumnMandatory(int col){
		if (col < 0 || col >= columns.size())
			return false;
		ColumnDescription cd = columns.get(col);
		return cd.mandatory;
	}

	public String getToolTip(int rowModelIndex, int colModelIndex){
		return null;
	}

	public Object validateValue(Object aValue){
		if (aValue == null)
			return null;

		if (aValue instanceof String){
			String s = (String) aValue;
			return StringValidator.validate(s);
		}
		return aValue;
	}
}
