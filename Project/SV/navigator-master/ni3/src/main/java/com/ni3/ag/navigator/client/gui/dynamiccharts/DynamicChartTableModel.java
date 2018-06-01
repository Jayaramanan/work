/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.dynamiccharts;

import java.awt.Color;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.ni3.ag.navigator.client.domain.DynamicChartAttribute;
import com.ni3.ag.navigator.client.domain.Palette;
import com.ni3.ag.navigator.client.domain.UserSettings;

public class DynamicChartTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	public static final int SELECTION_COLUMN_INDEX = 0;
	private static final int ATTRIBUTE_COLUMN_INDEX = 1;
	public static final int COLOR_COLUMN_INDEX = 2;

	private static final String[] columnsNames = new String[] { "", UserSettings.getWord("Attribute"),
	        UserSettings.getWord("Color") };
	private static final Class<?>[] columnClasses = new Class[] { Boolean.class, String.class, Color.class };

	private List<DynamicChartAttribute> attributes;
	private Palette colorPalette;

	@Override
	public Class<?> getColumnClass(int columnIndex){
		return columnClasses[columnIndex];
	}

	public DynamicChartTableModel(List<DynamicChartAttribute> attributes){
		this.attributes = attributes;
	}

	public void setData(List<DynamicChartAttribute> attributes, Palette colorPalette){
		this.attributes = attributes;
		this.colorPalette = colorPalette;
		fireTableDataChanged();
	}

	public int getColumnCount(){
		return columnsNames.length;
	}

	public int getRowCount(){
		if (attributes == null)
			return 0;
		return attributes.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		Object value = null;
		final DynamicChartAttribute attribute = attributes.get(rowIndex);
		switch (columnIndex){
			case SELECTION_COLUMN_INDEX:
				value = attribute.isSelected();
				break;
			case ATTRIBUTE_COLUMN_INDEX:
				value = attribute.toString();
				break;
			case COLOR_COLUMN_INDEX:
				value = attribute.getColor();
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
		return columnIndex == SELECTION_COLUMN_INDEX
		        || (columnIndex == COLOR_COLUMN_INDEX && attributes.get(rowIndex).isSelected());
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		final DynamicChartAttribute attribute = attributes.get(rowIndex);
		switch (columnIndex){
			case SELECTION_COLUMN_INDEX:
				final Boolean selected = (Boolean) aValue;
				attribute.setSelected(selected);
				Color color = selected ? getNextColor() : null;
				attribute.setColor(color);
				fireTableRowsUpdated(rowIndex, rowIndex);
				break;
			case COLOR_COLUMN_INDEX:
				attribute.setColor((Color) aValue);
				break;
			default:
				break;
		}
	}

	private Color getNextColor(){
		Color color = null;
		for (int i = 0; i < colorPalette.getColorCount(); i++){
			Color c = colorPalette.nextColor();
			if (!isColorUsed(c)){
				color = c;
				break;
			}
		}
		if (color == null){
			color = colorPalette.nextColor();
		}
		return color;
	}

	private boolean isColorUsed(Color color){
		boolean used = false;
		for (DynamicChartAttribute attr : attributes){
			if (attr.getColor() != null && attr.getColor().equals(color)){
				used = true;
				break;
			}
		}
		return used;
	}

	public void clearSelection(){
		for (DynamicChartAttribute attr : attributes){
			attr.setSelected(false);
			attr.setColor(null);
		}
		fireTableDataChanged();
	}

}
