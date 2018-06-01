/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ObjectTableModel extends ACTableModel{

	private static final long serialVersionUID = 1L;

	private List<ObjectDefinition> objectDefinitions = new ArrayList<ObjectDefinition>();
	private static final int OBJECT_NAME_INDEX = 0;
	private static final int OBJECT_TYPE_INDEX = 1;
	private static final int OBJECT_SORT_INDEX = 2;
	private static final int DESCRIPTION_INDEX = 3;
	private static final int USER_INDEX = 4;
	private static final int CREATION_DATE_INDEX = 5;

	public ObjectTableModel(List<ObjectDefinition> objectDefinitions){
		super();
		setData(objectDefinitions);
		init();
	}

	private void init(){
		addColumn(Translation.get(TextID.ObjectName), false, String.class, false);
		addColumn(Translation.get(TextID.ObjectType), false, ObjectType.class, false);
		addColumn(Translation.get(TextID.Sort), true, Integer.class, false);
		addColumn(Translation.get(TextID.Description), true, String.class, false);
		addColumn(Translation.get(TextID.CreatedBy), false, String.class, false);
		addColumn(Translation.get(TextID.CreationDate), false, String.class, false);
	}

	public boolean isChanged(int row){
		for (int i = 0; i < getColumnCount(); i++)
			if (isChanged(row, i))
				return true;
		return false;
	}

	public List<ObjectDefinition> getChangedObjects(){
		List<ObjectDefinition> ret = new ArrayList<ObjectDefinition>();
		for (int i = 0; i < getRowCount(); i++)
			if (isChanged(i))
				ret.add(objectDefinitions.get(i));
		return ret;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex){
		ObjectDefinition object = objectDefinitions.get(rowIndex);
		switch (columnIndex){
			case OBJECT_NAME_INDEX:
				return object.getName();
			case OBJECT_TYPE_INDEX:
				return object.getObjectType();
			case OBJECT_SORT_INDEX:
				return object.getSort();
			case DESCRIPTION_INDEX:
				return object.getDescription();
			case USER_INDEX:
				User user = object.getCreatedBy();
				return user != null ? user.getUserName() : null;
			case CREATION_DATE_INDEX:
				return object.getCreationDate() != null ? object.getCreationDate().toString() : null;
			default:
				return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		aValue = validateValue(aValue);
		super.setValueAt(aValue, rowIndex, columnIndex);
		ObjectDefinition object = objectDefinitions.get(rowIndex);
		switch (columnIndex){
			case OBJECT_NAME_INDEX:
				object.setName((String) aValue);
				break;
			case OBJECT_TYPE_INDEX:
				object.setObjectType((ObjectType) aValue);
				break;
			case OBJECT_SORT_INDEX:
				object.setSort((Integer) aValue);
				break;
			case DESCRIPTION_INDEX:
				object.setDescription((String) aValue);
				break;
			default:
				break;
		}
	}

	@Override
	public int getRowCount(){
		if (objectDefinitions == null)
			return 0;
		return objectDefinitions.size();
	}

	public ObjectDefinition getSelectedObject(int index){
		if (index >= 0)
			return objectDefinitions.get(index);
		return null;
	}

	public int indexOf(ObjectDefinition object){
		return objectDefinitions.indexOf(object);
	}

	@Override
	public String getToolTip(int row, int column){
		if (isCellEditable(row, column)){
			return null;
		}
		if (column < OBJECT_SORT_INDEX)
			return Translation.get(TextID.ReadonlySelectObjectToEdit);
		else
			return Translation.get(TextID.ReadonlyFilledAutomatically);
	}

	public void setData(List<ObjectDefinition> objectDefinitions){
		this.objectDefinitions = objectDefinitions;
	}
}
