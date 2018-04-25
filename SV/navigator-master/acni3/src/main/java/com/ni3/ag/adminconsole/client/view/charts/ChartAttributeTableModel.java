/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ChartAttributeTableModel extends ACTableModel{
	private static final long serialVersionUID = 1L;

	private static final int ATTRIBUTE_INDEX = 0;
	private static final int RGB_INDEX = 1;

	private List<ChartAttribute> chartAttributes;

	public ChartAttributeTableModel(List<ChartAttribute> cattrs){
		setData(cattrs);

		addColumn(Translation.get(TextID.Attribute), true, ObjectAttribute.class, true);
		addColumn(Translation.get(TextID.RGB), true, String.class, true);
	}

	@Override
	public int getRowCount(){
		return chartAttributes.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex){
		ChartAttribute cca = chartAttributes.get(rowIndex);
		switch (columnIndex){
			case ATTRIBUTE_INDEX:
				return cca.getAttribute();
			case RGB_INDEX:
				return cca.getRgb();
			default:
				return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		aValue = validateValue(aValue);
		super.setValueAt(aValue, rowIndex, columnIndex);
		ChartAttribute cca = chartAttributes.get(rowIndex);
		switch (columnIndex){
			case ATTRIBUTE_INDEX:
				cca.setAttribute((ObjectAttribute) aValue);
				break;
			case RGB_INDEX:
				cca.setRgb((String) aValue);
				break;
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	public int indexOf(ChartAttribute cca){
		// can't use array.indexOf couse all new object has id == null
		// and for equals method they are the same
		for (int i = 0; i < chartAttributes.size(); i++)
			if (chartAttributes.get(i) == cca)
				return i;
		return -1;
	}

	/**
	 * <b>Use only for attributes that were stored previously.</b> For finding new attributes (not after update) use
	 * indexOf(ChartAttribute cca) instead. Attribute is unique for dynamic attributes, so we search for it, and not for
	 * id, because id is null before update and we wont find this attribute after update.
	 * 
	 * @param cca
	 * @return
	 */
	public int indexOfUpdated(ChartAttribute cca){
		if (cca.getAttribute() != null){
			for (int i = 0; i < chartAttributes.size(); i++){
				ObjectAttribute attr = chartAttributes.get(i).getAttribute();
				if (attr != null && attr.equals(cca.getAttribute()))
					return i;
			}
		}
		return -1;
	}

	public ChartAttribute getDynamicAttribute(int index){
		return chartAttributes.get(index);
	}

	public ChartAttribute getChartAttribute(int index){
		return chartAttributes.get(index);
	}

	public void setData(List<ChartAttribute> cattrs){
		chartAttributes = cattrs;
		if (chartAttributes == null)
			chartAttributes = new ArrayList<ChartAttribute>();
	}
}
