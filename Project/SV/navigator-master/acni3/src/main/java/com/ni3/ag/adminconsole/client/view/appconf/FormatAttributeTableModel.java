/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import static com.ni3.ag.adminconsole.client.view.Translation.get;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class FormatAttributeTableModel extends ACTableModel{

	private static final long serialVersionUID = 1L;

	private List<ObjectAttribute> objectAttributes = new ArrayList<ObjectAttribute>();

	public FormatAttributeTableModel(){
		addColumn(Translation.get(TextID.Label), false, String.class, false);
		addColumn(Translation.get(TextID.Datatype), false, String.class, false);
		addColumn(Translation.get(TextID.Sort), false, Integer.class, false);
		addColumn(Translation.get(TextID.Format), true, String.class, false);
		addColumn(Translation.get(TextID.MinValue), true, String.class, false);
		addColumn(Translation.get(TextID.MaxValue), true, String.class, false);
		addColumn(Translation.get(TextID.EditFormat), true, String.class, false);
		addColumn(Translation.get(TextID.FormatValidCharacters), true, String.class, false);
		addColumn(Translation.get(TextID.FormatInvalidCharacters), true, String.class, false);
	}

	@Override
	public int getRowCount(){
		return objectAttributes.size();
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		ObjectAttribute oa = objectAttributes.get(rowIndex);
		aValue = validateValue(aValue);
		super.setValueAt(aValue, rowIndex, columnIndex);
		String colName = getColumnName(columnIndex);
		if (Translation.get(TextID.Format).equals(colName)){
			oa.setFormat((String) aValue);
		} else if (Translation.get(TextID.MinValue).equals(colName)){
			try{
				if (aValue instanceof String){
					Double.valueOf((String) aValue);
					oa.setMinValue((String) aValue);
				}
			} catch (NumberFormatException e){
				// ignore
			}
		} else if (Translation.get(TextID.MaxValue).equals(colName)){
			try{
				if (aValue instanceof String){
					Double.valueOf((String) aValue);
					oa.setMaxValue((String) aValue);
				}
			} catch (NumberFormatException e){
				// ignore
			}
		} else if (Translation.get(TextID.EditFormat).equals(colName)){
			oa.setEditFormat((String) aValue);
		} else if (Translation.get(TextID.FormatValidCharacters).equals(colName)){
			oa.setFormatValidCharacters((String) aValue);
		} else if (Translation.get(TextID.FormatInvalidCharacters).equals(colName)){
			oa.setFormatInvalidCharacters((String) aValue);
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex){
		ObjectAttribute attr = objectAttributes.get(rowIndex);
		String colName = getColumnName(columnIndex);

		Object ret = null;
		if (Translation.get(TextID.Label).equals(colName)){
			ret = attr.getLabel();
		} else if (Translation.get(TextID.Datatype).equals(colName)){
			ret = Translation.get(attr.getDataType().getTextId());
		} else if (Translation.get(TextID.Sort).equals(colName)){
			ret = attr.getSort();
		} else if (Translation.get(TextID.Format).equals(colName)){
			ret = attr.getFormat();
		} else if (Translation.get(TextID.MinValue).equals(colName)){
			ret = attr.getMinValue();
		} else if (Translation.get(TextID.MaxValue).equals(colName)){
			ret = attr.getMaxValue();
		} else if (Translation.get(TextID.EditFormat).equals(colName)){
			ret = attr.getEditFormat();
		} else if (Translation.get(TextID.FormatValidCharacters).equals(colName)){
			ret = attr.getFormatValidCharacters();
		} else if (Translation.get(TextID.FormatInvalidCharacters).equals(colName)){
			ret = attr.getFormatInvalidCharacters();
		}
		return ret;
	}

	public void setData(List<ObjectAttribute> objectAttributes){
		super.resetChanges();
		this.objectAttributes = objectAttributes;
	}

	public int indexOf(ObjectAttribute attribute){
		return objectAttributes.indexOf(attribute);
	}

	public ObjectAttribute getSelectedAttribute(int rowIndex){
		if (rowIndex >= 0 && rowIndex < objectAttributes.size()){
			return objectAttributes.get(rowIndex);
		}
		return null;
	}

	@Override
	public String getToolTip(int row, int column){
		if (isCellEditable(row, column)){
			return "";
		}
		final ObjectAttribute oa = objectAttributes.get(row);
		String colName = getColumnName(column);
		if (Translation.get(TextID.Label).equals(colName) || Translation.get(TextID.Datatype).equals(colName)){
			return get(TextID.ReadonlyConfigurableOnOtherScreen, new String[] { get(TextID.Schemas) });
		} else if (Translation.get(TextID.Sort).equals(colName)){
			return get(TextID.ReadonlyConfigurableOnOtherScreen, new String[] { get(TextID.Attributes) });
		} else if (oa.isFormulaAttribute()){
			return get(TextID.MsgFormatDefinedByFormula);
		} else{
			return get(TextID.ReadonlyNotConfigurableForDatatype);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		final String colName = getColumnName(columnIndex);
		final ObjectAttribute oa = objectAttributes.get(rowIndex);
		boolean editable = false;
		if (oa.isPredefined() || oa.isFormulaAttribute()){
			editable = false;
		} else if (get(TextID.Format).equals(colName) || get(TextID.EditFormat).equals(colName)){
			editable = oa.isTextDataType() || oa.isIntDataType() || oa.isDecimalDataType() || oa.isDateDataType();
		} else if (get(TextID.MinValue).equals(colName) || get(TextID.MaxValue).equals(colName)){
			editable = oa.isIntDataType() || oa.isDecimalDataType();
		} else if (get(TextID.FormatValidCharacters).equals(colName) || get(TextID.FormatInvalidCharacters).equals(colName)){
			editable = oa.isTextDataType();
		}

		return editable;
	}
}
