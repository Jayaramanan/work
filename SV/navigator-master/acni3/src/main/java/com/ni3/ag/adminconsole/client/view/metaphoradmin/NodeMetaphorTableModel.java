/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.metaphoradmin;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.domain.MetaphorData;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class NodeMetaphorTableModel extends ACTableModel{
	private static final long serialVersionUID = -1689673005563889536L;

	private List<Metaphor> metaphors;
	private List<ObjectAttribute> attributes;

	public static final int FIRST_DYNAMIC_COLUMN = 4;

	public NodeMetaphorTableModel(){
		this(new ArrayList<ObjectAttribute>(), new ArrayList<Metaphor>());
	}

	public NodeMetaphorTableModel(List<ObjectAttribute> attributes, List<Metaphor> metaphors){
		addColumn(Translation.get(TextID.Icon), true, Icon.class, true);
		addColumn(Translation.get(TextID.Priority), true, Integer.class, true);
		addColumn(Translation.get(TextID.MetaphorSet), true, String.class, false);
		addColumn(Translation.get(TextID.Description), true, String.class, false);
		setData(metaphors, attributes);
	}

	public void setData(List<Metaphor> metaphors){
		this.metaphors = metaphors;
	}

	private void removeAttributeColumns(){
		List<String> columnsToRemove = new ArrayList<String>();
		for (int i = 0; i < getColumnCount(); i++){
			String colName = getColumnName(i);
			if (!Translation.get(TextID.Icon).equals(colName) && !Translation.get(TextID.Priority).equals(colName)
			        && !Translation.get(TextID.MetaphorSet).equals(colName)
			        && !Translation.get(TextID.Description).equals(colName))
				columnsToRemove.add(colName);
		}
		removeColumns(columnsToRemove);
	}

	public void setData(List<Metaphor> metaphors, List<ObjectAttribute> attributes){
		this.metaphors = metaphors;
		this.attributes = attributes;
		removeAttributeColumns();
		for (int i = 0; i < attributes.size(); i++){
			String colName = attributes.get(i).getLabel();
			addColumn(colName, true, PredefinedAttribute.class, false);
		}

	}

	public int getRowCount(){
		return metaphors.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		Metaphor metaphor = metaphors.get(rowIndex);
		if (columnIndex >= FIRST_DYNAMIC_COLUMN){
			return getPredefinedAttribute(metaphor, columnIndex);
		} else{
			switch (columnIndex){
				case 0:
					return metaphor.getIcon();
				case 1:
					return metaphor.getPriority();
				case 2:
					return metaphor.getMetaphorSet();
				case 3:
					return metaphor.getDescription();
				default:
					return null;
			}
		}
	}

	PredefinedAttribute getPredefinedAttribute(Metaphor metaphor, int columnIndex){
		ObjectAttribute attribute = attributes.get(columnIndex - FIRST_DYNAMIC_COLUMN);
		for (MetaphorData md : metaphor.getMetaphorData()){
			if (attribute.equals(md.getAttribute())){
				return md.getData();
			}
		}
		return null;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex){
		value = validateValue(value);
		super.setValueAt(value, rowIndex, columnIndex);
		Metaphor metaphor = metaphors.get(rowIndex);
		if (columnIndex >= FIRST_DYNAMIC_COLUMN){
			setPredefinedAttribute(value, metaphor, columnIndex);
		} else{
			switch (columnIndex){
				case 0:
					Icon icon = (Icon) value;
					metaphor.setIcon(icon);
					metaphor.setIconName(icon != null ? icon.getIconName() : null);
					break;
				case 1:
					metaphor.setPriority((Integer) value);
					break;
				case 2:
					metaphor.setMetaphorSet((String) value);
					break;
				case 3:
					metaphor.setDescription((String) value);
					break;
				default:
					break;
			}
		}
	}

	void setPredefinedAttribute(Object value, Metaphor metaphor, int columnIndex){
		ObjectAttribute attribute = attributes.get(columnIndex - FIRST_DYNAMIC_COLUMN);
		MetaphorData found = null;
		for (MetaphorData md : metaphor.getMetaphorData()){
			if (attribute.equals(md.getAttribute())){
				found = md;
				break;
			}
		}
		if (found == null && value != null){
			found = new MetaphorData(metaphor, attribute, null);
			metaphor.getMetaphorData().add(found);
		}
		if (value != null)
			found.setData((PredefinedAttribute) value);
		else
			metaphor.getMetaphorData().remove(found);
	}

	public Object getRowIdentifier(int rowIndex){
		return metaphors.get(rowIndex).getId();
	}

	public Metaphor getSelectedRowData(int rowIndex){
		if (rowIndex >= 0 && rowIndex < metaphors.size()){
			return metaphors.get(rowIndex);
		}
		return null;
	}

	public int indexOf(Integer identifier){
		for (int i = 0; i < metaphors.size(); i++){
			if (metaphors.get(i).getId() == identifier){
				return i;
			}
		}
		return -1;
	}

	public int indexOf(Metaphor row){
		return metaphors.indexOf(row);
	}

}
