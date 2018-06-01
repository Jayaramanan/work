/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.geoanalytics;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.shared.domain.Cluster;

public class ClusterTableModel extends AbstractTableModel{
	private static final long serialVersionUID = -3824214345927796231L;
	protected static final int COLOR_COLUMN_INDEX = 1;
    private static final int DESCRIPTION_COLUMN_INDEX = 4;
	private String[] columnNames = { UserSettings.getWord("Range"), UserSettings.getWord("Color"),
	        UserSettings.getWord("TerritoryCount"), UserSettings.getWord("NodeCount"),  UserSettings.getWord("Description")};
	private static final Class<?>[] columnClasses = new Class[] { String.class, Color.class, Integer.class, Integer.class, String.class };
	private List<Cluster> clusters;
	private DecimalFormat df;

	public ClusterTableModel(List<Cluster> clusters){
		this.clusters = clusters;
		df = new DecimalFormat("#,##0.##");
	}

	public void setData(List<Cluster> clusters){
		this.clusters = clusters;
	}

	@Override
	public int getColumnCount(){
		return columnNames.length;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex){
		return columnClasses[columnIndex];
	}

	@Override
	public int getRowCount(){
		return clusters != null ? clusters.size() : 0;
	}

	@Override
	public Object getValueAt(int row, int column){
		Cluster cluster = clusters.get(row);
		Object result = null;
		switch (column){
			case 0:
				result = getRange(cluster);
				break;
			case COLOR_COLUMN_INDEX:
				result = cluster.getColor();
				break;
			case 2:
				result = cluster.getTerritoryCount();
				break;
			case 3:
				result = cluster.getObjectCount();
				break;
            case DESCRIPTION_COLUMN_INDEX:
                result = cluster.getDescription();
                break;
			default:
				break;
		}
		return result;
	}

	@Override
	public String getColumnName(int columnIndex){
		return columnNames[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
        return columnIndex == COLOR_COLUMN_INDEX || columnIndex == DESCRIPTION_COLUMN_INDEX;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column){
		Cluster cluster = clusters.get(row);
		if (column == COLOR_COLUMN_INDEX){
			cluster.setColor((Color) aValue);
		} else if(column == DESCRIPTION_COLUMN_INDEX){
            cluster.setDescription((String) aValue);
        }
	}

	private String getRange(Cluster cluster){
		final Double from = cluster.getFrom();
		final Double to = cluster.getTo();

		if (from.equals(to)){
			return df.format(from);
		} else{
			return df.format(from) + "-" + df.format(to);
		}
	}
}
