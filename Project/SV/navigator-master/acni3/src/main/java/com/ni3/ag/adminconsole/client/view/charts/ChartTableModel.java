/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts;

import java.awt.Font;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.ChartDisplayOperation;
import com.ni3.ag.adminconsole.domain.ChartType;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ChartTableModel extends ACTableModel{
	private static final long serialVersionUID = 1L;
	private static final int ObjectDefinition_index = 0;
	private static final int MinValue_index = 1;
	private static final int MaxValue_index = 2;
	private static final int MinScale_index = 3;
	private static final int MaxScale_index = 4;
	private static final int LabelInUse_index = 5;
	private static final int LabelFont_index = 6;
	private static final int FontItalic_index = 7;
	private static final int FontBold_index = 8;
	private static final int FontSize_index = 9;
	public static final int FontColor_index = 10;
	private static final int NumberFormat_index = 11;
	private static final int DisplayOperation_index = 12;
	private static final int ChartType_index = 13;
	private static final int IsValueDisplayed_index = 14;

	private List<ObjectChart> charts;

	public ChartTableModel(List<ObjectChart> charts){
		setData(charts);
		addColumn(Translation.get(TextID.Object), true, ObjectDefinition.class, true);
		addColumn(Translation.get(TextID.MinValue), true, Integer.class, false);
		addColumn(Translation.get(TextID.MaxValue), true, Integer.class, false);
		addColumn(Translation.get(TextID.MinScale), true, BigDecimal.class, false);
		addColumn(Translation.get(TextID.MaxScale), true, BigDecimal.class, false);
		addColumn(Translation.get(TextID.LabelInUse), true, Boolean.class, true);
		addColumn(Translation.get(TextID.Font), true, String.class, true);
		addColumn(Translation.get(TextID.FontItalic), true, Boolean.class, false);
		addColumn(Translation.get(TextID.FontBold), true, Boolean.class, false);
		addColumn(Translation.get(TextID.FontSize), true, Integer.class, true);
		addColumn(Translation.get(TextID.FontColor), true, String.class, false);
		addColumn(Translation.get(TextID.NumberFormat), true, String.class, true);
		addColumn(Translation.get(TextID.DisplayOperation), true, ChartDisplayOperation.class, true);
		addColumn(Translation.get(TextID.ChartType), true, ChartType.class, true);
		addColumn(Translation.get(TextID.IsValueDisplayed), true, Boolean.class, true);
	}

	public int getRowCount(){
		return charts.size();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		boolean editable = super.isCellEditable(rowIndex, columnIndex);
		if (columnIndex == ObjectDefinition_index){
			final ObjectChart objectChart = charts.get(rowIndex);
			editable = objectChart.getId() == null;
		}
		return editable;
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		ObjectChart oc = charts.get(rowIndex);
		switch (columnIndex){
			case ObjectDefinition_index:
				return oc.getObject();
			case MinValue_index:
				return oc.getMinValue();
			case MaxValue_index:
				return oc.getMaxValue();
			case MinScale_index:
				return oc.getMinScale();
			case MaxScale_index:
				return oc.getMaxScale();
			case LabelInUse_index:
				return oc.getLabelInUse();
			case LabelFont_index:
				Font f = oc.getFont();
				if (f != null)
					return f.getFamily();
				return null;
			case FontBold_index:
				f = oc.getFont();
				if (f != null)
					return (Font.BOLD & f.getStyle()) == Font.BOLD;
				return false;
			case FontItalic_index:
				f = oc.getFont();
				if (f != null)
					return (Font.ITALIC & f.getStyle()) == Font.ITALIC;
				return false;
			case FontSize_index:
				f = oc.getFont();
				if (f != null)
					return f.getSize();
				return null;
			case FontColor_index:
				return oc.getFontColor();
			case NumberFormat_index:
				return oc.getNumberFormat();
			case DisplayOperation_index:
				return oc.getDisplayOperation();
			case ChartType_index:
				return oc.getChartType();
			case IsValueDisplayed_index:
				return oc.getIsValueDisplayed();
			default:
				return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		aValue = validateValue(aValue);
		super.setValueAt(aValue, rowIndex, columnIndex);
		ObjectChart oc = charts.get(rowIndex);
		switch (columnIndex){
			case ObjectDefinition_index:
				oc.setObject((ObjectDefinition) aValue);
				break;
			case MinValue_index:
				oc.setMinValue((Integer) aValue);
				break;
			case MaxValue_index:
				oc.setMaxValue((Integer) aValue);
				break;
			case MinScale_index:
				oc.setMinScale((BigDecimal) aValue);
				break;
			case MaxScale_index:
				oc.setMaxScale((BigDecimal) aValue);
				break;
			case LabelInUse_index:
				oc.setLabelInUse((Boolean) aValue);
				break;
			case LabelFont_index:
				Font f = oc.getFont();
				if (f == null)
					f = new Font((String) aValue, 0, 1);
				else
					f = new Font((String) aValue, f.getStyle(), f.getSize());
				oc.setFont(f);
				break;
			case FontBold_index:
				f = oc.getFont();
				Boolean b = (Boolean) aValue;
				if (f == null)
					f = new Font("Dialog", b ? Font.BOLD : Font.PLAIN, 1);
				else{
					int style = b ? Font.BOLD | f.getStyle() : Font.BOLD ^ f.getStyle();
					f = new Font(f.getFamily(), style, f.getSize());
				}
				oc.setFont(f);
				break;
			case FontItalic_index:
				f = oc.getFont();
				b = (Boolean) aValue;
				if (f == null)
					f = new Font("Dialog", b ? Font.ITALIC : Font.PLAIN, 1);
				else{
					int style = b ? Font.ITALIC | f.getStyle() : Font.ITALIC ^ f.getStyle();
					f = new Font(f.getFamily(), style, f.getSize());
				}
				oc.setFont(f);
				break;
			case FontSize_index:
				f = oc.getFont();
				Integer size = (Integer) aValue;
				if (size == null)
					size = 0;
				if (f == null)
					f = new Font("Dialog", 0, size);
				else
					f = new Font(f.getFamily(), f.getStyle(), size);
				oc.setFont(f);
				break;
			case FontColor_index:
				oc.setFontColor((String) aValue);
				break;
			case NumberFormat_index:
				oc.setNumberFormat((String) aValue);
				break;
			case DisplayOperation_index:
				oc.setDisplayOperation((ChartDisplayOperation) aValue);
				break;
			case ChartType_index:
				oc.setChartType((ChartType) aValue);
				break;
			case IsValueDisplayed_index:
				oc.setIsValueDisplayed((Boolean) aValue);
				break;
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	public ObjectChart getObjectChart(int index){
		return charts.get(index);
	}

	public int indexOf(ObjectChart oc){
		return charts.indexOf(oc);
	}

	public void setData(List<ObjectChart> objectCharts){
		if (objectCharts == null){
			objectCharts = new ArrayList<ObjectChart>();
		}
		this.charts = objectCharts;
	}

}
