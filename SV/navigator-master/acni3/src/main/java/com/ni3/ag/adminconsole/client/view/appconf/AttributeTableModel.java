/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.Formula;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class AttributeTableModel extends ACTableModel{

	private static final long serialVersionUID = 1L;

	private class ColumnDescription{
		private String name;
		private Class<?> _class;

		public ColumnDescription(String name, Class<?> _class){
			this.name = name;
			this._class = _class;
		}
	}

	private List<ColumnDescription> columns = new ArrayList<ColumnDescription>();

	private ObjectDefinition currentObject;
	private List<ObjectAttribute> currentAttributesWithPredefineds;

	public AttributeTableModel(){
		columns.add(new ColumnDescription(Translation.get(TextID.Label), String.class));
		columns.add(new ColumnDescription(Translation.get(TextID.Description), String.class));
	}

	public ObjectAttribute getAttribute(int index){
		if (currentAttributesWithPredefineds != null)
			return currentAttributesWithPredefineds.get(index);
		return null;
	}

	public void setCurrentObject(ObjectDefinition od){
		currentObject = od;
		currentAttributesWithPredefineds = new ArrayList<ObjectAttribute>();
		if (currentObject != null && currentObject.getObjectAttributes() != null)
			for (ObjectAttribute attr : currentObject.getObjectAttributes()){
				if (attr.isPredefined() || Formula.FORMULA_BASED.equals(attr.getPredefined_()))
					currentAttributesWithPredefineds.add(attr);
			}
	}

	public ObjectDefinition getCurrentObjectDefinition(){
		return currentObject;
	}

	public void addTableModelListener(TableModelListener l){
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

	public int getRowCount(){
		if (currentAttributesWithPredefineds == null)
			return 0;
		return currentAttributesWithPredefineds.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		if (currentAttributesWithPredefineds == null)
			return null;
		ObjectAttribute oa = currentAttributesWithPredefineds.get(rowIndex);
		switch (columnIndex){
			case 0:
				return oa.getLabel();
			case 1:
				return oa.getDescription();
			default:
				return null;
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex){
		return false;
	}

	public void removeTableModelListener(TableModelListener l){
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	}

	public int getIndexOf(ObjectAttribute current){
		if (currentAttributesWithPredefineds == null)
			return -1;
		for (int i = 0; i < currentAttributesWithPredefineds.size(); i++){
			ObjectAttribute oa = currentAttributesWithPredefineds.get(i);
			if (oa.getId() != null && current.getId() != null && oa.getId().equals(current.getId()))
				return i;
		}

		return -1;
	}
}
