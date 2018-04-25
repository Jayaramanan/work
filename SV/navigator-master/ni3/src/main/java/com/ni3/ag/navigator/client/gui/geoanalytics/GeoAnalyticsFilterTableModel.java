/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.geoanalytics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.shared.domain.GeoTerritory;

public class GeoAnalyticsFilterTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	static final int SELECTION_COLUMN_INDEX = 0;
	private static final int TERRITORY_COLUMN_INDEX = 1;

	private static final String[] columnsNames = new String[] { "", UserSettings.getWord("Territory") };
	private static final Class<?>[] columnClasses = new Class[] { Boolean.class, String.class };

	private List<GeoTerritory> territories;
	private Set<GeoTerritory> filteredOutSet;

	@Override
	public Class<?> getColumnClass(int columnIndex){
		return columnClasses[columnIndex];
	}

	public GeoAnalyticsFilterTableModel(List<GeoTerritory> territories){
		this.territories = territories;
		filteredOutSet = new HashSet<GeoTerritory>();
	}

	public void setData(List<GeoTerritory> territories, Set<GeoTerritory> filteredOutSet){
		this.territories = territories;
		this.filteredOutSet.clear();
		this.filteredOutSet.addAll(filteredOutSet);
		fireTableDataChanged();
	}

	public int getColumnCount(){
		return columnsNames.length;
	}

	public int getRowCount(){
		if (territories == null)
			return 0;
		return territories.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		Object value = null;
		final GeoTerritory territory = territories.get(rowIndex);
		switch (columnIndex){
			case SELECTION_COLUMN_INDEX:
				value = !filteredOutSet.contains(territory);
				break;
			case TERRITORY_COLUMN_INDEX:
				value = territory.getName();
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
		final GeoTerritory territory = territories.get(rowIndex);
		switch (columnIndex){
			case SELECTION_COLUMN_INDEX:
				final Boolean selected = (Boolean) aValue;
				if (selected){
					filteredOutSet.remove(territory);
				} else{
					filteredOutSet.add(territory);
				}
				break;
			default:
				break;
		}
	}

	public void clearSelection(){
		filteredOutSet.clear();
		fireTableDataChanged();
	}

	public Set<GeoTerritory> getFilteredOutTerritories(){
		return filteredOutSet;
	}

	public void setAllSelected(boolean checked){
		if (checked){
			filteredOutSet.clear();
		} else{
			filteredOutSet.addAll(territories);
		}
	}
}
