/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

import static com.ni3.ag.adminconsole.client.view.Translation.get;
import static com.ni3.ag.adminconsole.domain.ObjectAttribute.COMMENT_ATTRIBUTE_NAME;
import static com.ni3.ag.adminconsole.domain.ObjectAttribute.STRENGTH_ATTRIBUTE_NAME;
import static com.ni3.ag.adminconsole.shared.language.TextID.*;

public class ObjectAttributeTableModel extends ACTableModel{

	private static final int DATA_TYPE_COLUMN_INDEX = 2;

	private static final long serialVersionUID = 1L;
	private List<ObjectAttribute> objectAttributes;
	private boolean isEdgeObject;
	private ACValidationRule attributeUsedInMetaphorRule;
	private ErrorRenderer errorRenderer;

	public ObjectAttributeTableModel(List<ObjectAttribute> objectAttributes, ErrorRenderer errorRenderer){
		this(objectAttributes, false, errorRenderer);
	}

	public ObjectAttributeTableModel(List<ObjectAttribute> objectAttributes, boolean isEdgeObject,
									 ErrorRenderer errorRenderer){
		setData(objectAttributes, isEdgeObject);
		this.errorRenderer = errorRenderer;
		attributeUsedInMetaphorRule = ACSpringFactory.getInstance().getAttributeUsedInMetaphorRule();
	}

	@Override
	public int getRowCount(){
		if (objectAttributes == null)
			return 0;
		return objectAttributes.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex){
		ObjectAttribute objectAttribute = objectAttributes.get(rowIndex);
		switch (columnIndex){
			case 0:
				return objectAttribute.getName();
			case 1:
				return objectAttribute.getLabel();
			case 2:
				return objectAttribute.getDataType();
			case 3:
				return objectAttribute.getDescription();
			case 4:
				return ValueListType.valueOf(objectAttribute.getPredefined_());
			case 5:
				return objectAttribute.getDataSource();
			case 6:
				return (isEdgeObject ? objectAttribute.getPredefined_() : objectAttribute.isInMetaphor());
			default:
				return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		ObjectAttribute oa = objectAttributes.get(rowIndex);
		if (isEdgeObject)
			return isCellEditableForEdgeType(oa, columnIndex);
		return isCellEditableForNodeType(oa, columnIndex);
	}

	private boolean isCellEditableForEdgeType(ObjectAttribute oa, int columnIndex){
		String colName = getColumnName(columnIndex);
		if (get(Name).equals(colName) || get(Predefined).equals(colName)){
			return !ObjectAttribute.isFixedEdgeAttribute(oa, false)
					|| STRENGTH_ATTRIBUTE_NAME.equalsIgnoreCase(oa.getName());
		} else if (get(Datatype).equals(colName)){
			return !ObjectAttribute.isFixedEdgeAttribute(oa, false) || COMMENT_ATTRIBUTE_NAME.equalsIgnoreCase(oa.getName());
		}
		return true;
	}

	private boolean isCellEditableForNodeType(ObjectAttribute oa, int columnIndex){
		String colName = getColumnName(columnIndex);
		if (get(Name).equals(colName) || get(Predefined).equals(colName)){
			return !ObjectAttribute.isFixedNodeAttribute(oa, false);
		} else if (get(Datatype).equals(colName)){
			return !ObjectAttribute.isFixedNodeAttribute(oa, false);
		} else if (get(InMetaphor).equals(colName)){
			return oa.isPredefined();
		}
		return true;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		aValue = validateValue(aValue);
		boolean updateCanceled = false;
		ObjectAttribute objectAttribute = objectAttributes.get(rowIndex);
		boolean needRefresh = false;
		switch (columnIndex){
			case 0:
				objectAttribute.setName((String) aValue);
				break;
			case 1:
				objectAttribute.setLabel((String) aValue);
				break;
			case 2:{
				DataType type = (DataType) aValue;
				objectAttribute.setDataType(type);
				objectAttribute.setAggregable(type == DataType.INT || type == DataType.DECIMAL);
				if (type == DataType.DATE)
					objectAttribute.setInSimpleSearch(false);
				if (type == DataType.BOOL)
					objectAttribute.setIsMultivalue(false);
			}
			break;
			case 3:
				objectAttribute.setDescription((String) aValue);
				break;
			case 4:
				ValueListType newVal = (ValueListType) aValue;
				objectAttribute.setPredefined_(newVal.getType());
				if (!objectAttribute.isPredefined()){
					objectAttribute.setInFilter(Boolean.FALSE);
					objectAttribute.setInPrefilter(Boolean.FALSE);
				}
				if (!isEdgeObject){
					if (!objectAttribute.isPredefined()){
						objectAttribute.setInMetaphor(Boolean.FALSE);
					}
					needRefresh = true;
				}
				break;
			case 5:
				objectAttribute.setDataSource(aValue.toString());
				break;
			case 6:
				if (isEdgeObject)
					break;
				if (!(Boolean) aValue){
					SchemaAdminModel model = new SchemaAdminModel();
					model.setAttributeToValidate(objectAttribute);
					if (!attributeUsedInMetaphorRule.performCheck(model)){
						errorRenderer.renderErrors(attributeUsedInMetaphorRule.getErrorEntries());
						updateCanceled = true;
						break;
					}
				}
				objectAttribute.setInMetaphor((Boolean) aValue);
				break;
			default:
				break;
		}
		if (!updateCanceled)
			super.setValueAt(aValue, rowIndex, columnIndex);
		if (needRefresh){
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
	}

	public ObjectAttribute getSelectedAttribute(int rowIndex){
		if (rowIndex >= 0 && rowIndex < objectAttributes.size()){
			return objectAttributes.get(rowIndex);
		}
		return null;
	}

	public List<ObjectAttribute> getSelectedAttributes(int[] rowIndexes){
		List<ObjectAttribute> ret = new ArrayList<ObjectAttribute>();
		for (int rowIndex : rowIndexes)
			if (rowIndex >= 0 && rowIndex < objectAttributes.size())
				ret.add(objectAttributes.get(rowIndex));
		return ret;
	}

	public int indexOf(ObjectAttribute newAttribute){
		return this.objectAttributes.indexOf(newAttribute);
	}

	public boolean isMandatoryAttribute(ObjectAttribute oa){
		if (oa.getName() == null)
			return false;

		if (isEdgeObject)
			return ObjectAttribute.isFixedEdgeAttribute(oa, false);
		return ObjectAttribute.isFixedNodeAttribute(oa, false);
	}

	@Override
	public String getToolTip(int row, int column){
		if (isCellEditable(row, column)){
			return null;
		}
		String colName = getColumnName(column);
		if (get(InMetaphor).equals(colName))
			return get(TextID.ReadonlyNotValueListAttribute);
		else
			return get(TextID.ReadonlyFixedAttribute);
	}

	public void setData(List<ObjectAttribute> objectAttributes, boolean isEdgeObject){
		this.objectAttributes = objectAttributes;
		if (this.isEdgeObject != isEdgeObject){
			List<String> columnsToRemove = new ArrayList<String>();
			for (int i = 0; i < getColumnCount(); i++){
				String colName = getColumnName(i);
				columnsToRemove.add(colName);
			}
			removeColumns(columnsToRemove);
		}
		this.isEdgeObject = isEdgeObject;
		if (isEdgeObject){
			addColumn(get(Name), false, String.class, true);
			addColumn(get(Label), false, String.class, true);
			addColumn(get(Datatype), false, DataType.class, true);
			addColumn(get(Description), false, String.class, false);
			addColumn(get(Predefined), true, ValueListType.class, false);
			addColumn(get(DataSource), true, String.class, true);
		} else{
			addColumn(get(Name), false, String.class, true);
			addColumn(get(Label), false, String.class, true);
			addColumn(get(Datatype), false, DataType.class, true);
			addColumn(get(Description), false, String.class, false);
			addColumn(get(Predefined), true, ValueListType.class, false);
			addColumn(get(DataSource), true, String.class, true);
			addColumn(get(InMetaphor), false, Boolean.class, false);
		}
	}

	public boolean isEdgeObjectModel(){
		return isEdgeObject;
	}

	public boolean isDataTypeChanged(){
		for (int row = 0; row < objectAttributes.size(); row++)
			if (super.isChanged(row, DATA_TYPE_COLUMN_INDEX))
				return true;
		return false;
	}
}
