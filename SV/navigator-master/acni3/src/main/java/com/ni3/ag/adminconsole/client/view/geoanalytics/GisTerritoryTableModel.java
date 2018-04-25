/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.geoanalytics;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.GisTerritory;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class GisTerritoryTableModel extends ACTableModel{
	private static final long serialVersionUID = 1L;
	private List<GisTerritory> territories = new ArrayList<GisTerritory>();

	public GisTerritoryTableModel(){
		addColumn(Translation.get(TextID.Territory), true, String.class, true);
		addColumn(Translation.get(TextID.Label), true, String.class, true);
		addColumn(Translation.get(TextID.Sort), true, Integer.class, false);
		addColumn(Translation.get(TextID.TableName), true, String.class, false);
		addColumn(Translation.get(TextID.DisplayColumn), true, String.class, false);
		addColumn(Translation.get(TextID.Version), true, Integer.class, true);
	}

	public GisTerritoryTableModel(List<GisTerritory> territories){
		this();
		this.territories = territories;
	}

	public int getRowCount(){
		return territories != null ? territories.size() : 0;
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		GisTerritory territory = territories.get(rowIndex);
		switch (columnIndex){
			case 0:
				return territory.getTerritory();
			case 1:
				return territory.getLabel();
			case 2:
				return territory.getSort();
			case 3:
				return territory.getTableName();
			case 4:
				return territory.getDisplayColumn();
			case 5:
				return territory.getVersion();

			default:
				return null;
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex){
		value = validateValue(value);
		super.setValueAt(value, rowIndex, columnIndex);
		GisTerritory territory = territories.get(rowIndex);
		switch (columnIndex){
			case 0:
				territory.setTerritory((String) value);
				break;
			case 1:
				territory.setLabel((String) value);
				break;
			case 2:
				territory.setSort((Integer) value);
				break;
			case 3:
				territory.setTableName((String) value);
				break;
			case 4:
				territory.setDisplayColumn((String) value);
				break;
			case 5:
				territory.setVersion((Integer) value);
				break;

			default:
				break;
		}
	}

	public GisTerritory getSelectedTerritory(int rowIndex){
		if (rowIndex >= 0 && rowIndex < territories.size()){
			return territories.get(rowIndex);
		}
		return null;
	}

	public int indexOf(GisTerritory territory){
		return territories.indexOf(territory);
	}

	public void setData(List<GisTerritory> territories){
		this.territories = territories;
	}

}
