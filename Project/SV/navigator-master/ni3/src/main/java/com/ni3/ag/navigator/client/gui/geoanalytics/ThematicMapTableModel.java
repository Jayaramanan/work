/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.geoanalytics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.shared.domain.ThematicMap;

public class ThematicMapTableModel extends AbstractTableModel{

	private static final long serialVersionUID = 1L;
	static final int SELECTION_COLUMN_INDEX = 0;
	private static final int THEMATICMAP_COLUMN_INDEX = 1;

	private static final String[] columnsNames = new String[] { "", UserSettings.getWord("ThematicMap") };
	private static final Class<?>[] columnClasses = new Class[] { Boolean.class, String.class };

	private List<ThematicMap> thematicMaps;
	private Set<ThematicMap> selectedMaps;

	@Override
	public Class<?> getColumnClass(int columnIndex){
		return columnClasses[columnIndex];
	}

	public ThematicMapTableModel(List<ThematicMap> thematicMaps){
		this.thematicMaps = thematicMaps;
		selectedMaps = new HashSet<ThematicMap>();
	}

	public void setData(List<ThematicMap> thematicMaps){
		this.thematicMaps = thematicMaps;
		selectedMaps = new HashSet<ThematicMap>();
	}

	public int getColumnCount(){
		return columnsNames.length;
	}

	public int getRowCount(){
		if (thematicMaps == null)
			return 0;
		return thematicMaps.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		Object value = null;
		final ThematicMap tm = thematicMaps.get(rowIndex);
		switch (columnIndex){
			case SELECTION_COLUMN_INDEX:
				value = selectedMaps.contains(tm);
				break;
			case THEMATICMAP_COLUMN_INDEX:
				value = tm.getName();
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
		final ThematicMap tm = thematicMaps.get(rowIndex);
		switch (columnIndex){
			case SELECTION_COLUMN_INDEX:
				final Boolean selected = (Boolean) aValue;
				if (selected){
					selectedMaps.add(tm);
				} else{
					selectedMaps.remove(tm);
				}
				break;
			default:
				break;
		}
	}

	public Set<ThematicMap> getSelectedThematicMaps(){
		return selectedMaps;
	}

}
