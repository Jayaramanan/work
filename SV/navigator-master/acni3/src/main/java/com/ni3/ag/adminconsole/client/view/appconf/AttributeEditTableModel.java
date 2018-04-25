/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import static com.ni3.ag.adminconsole.client.view.Translation.get;

import java.security.InvalidParameterException;
import java.util.List;

import com.ni3.ag.adminconsole.client.controller.appconf.attributes.AttributeTableModelValueChangeListener;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.InMatrixType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class AttributeEditTableModel extends ACTableModel{
	private static final long serialVersionUID = 678378785372992101L;
	private static final int INCONTEXT_COLUMN_INDEX = 17;
	public static final int INMATRIX_COLUMN_INDEX = 18;
	public static final int AGGREGABLE_COLUMN_INDEX = 19;
	public static final int MULTIVALUE_COLUMN_INDEX = 20;

	private List<ObjectAttribute> objectAttributes;

	private AttributeTableModelValueChangeListener listener;
	private boolean advancedView = true;

	public AttributeEditTableModel(List<ObjectAttribute> objectAttributes){
		createColumns();
		this.objectAttributes = objectAttributes;
	}

	private void createColumns(){
		addColumn(get(TextID.Label), false, String.class, false);
		addColumn(get(TextID.InMetaphor), false, Boolean.class, true);
		addColumn(get(TextID.Sort), true, Integer.class, true);
		addColumn(get(TextID.Predefined), false, Boolean.class, true);
		addColumn(get(TextID.InFilter), true, Boolean.class, true);
		addColumn(get(TextID.InLabel), true, Boolean.class, true);
		addColumn(get(TextID.InToolTip), true, Boolean.class, true);
		addColumn(get(TextID.InSimpleSearch), true, Boolean.class, true);
		addColumn(get(TextID.LabelBold), true, Boolean.class, true);
		addColumn(get(TextID.LabelItalic), true, Boolean.class, true);
		addColumn(get(TextID.LabelUnderline), true, Boolean.class, true);
		addColumn(get(TextID.ContentBold), true, Boolean.class, true);
		addColumn(get(TextID.ContentItalic), true, Boolean.class, true);
		addColumn(get(TextID.ContentUnderline), true, Boolean.class, true);
		addColumn(get(TextID.InExport), true, Boolean.class, true);
		addColumn(get(TextID.InAdvancedSearch), true, Boolean.class, true);
		addColumn(get(TextID.InPrefilter), true, Boolean.class, true);
		addColumn(get(TextID.InContext), true, Boolean.class, true);
		addColumn(get(TextID.InMatrix), true, InMatrixType.class, false);
		addColumn(get(TextID.Aggregable), true, Boolean.class, false);
		addColumn(get(TextID.Multivalue), true, Boolean.class, false);
		addColumn(get(TextID.Matrix_sort), true, Integer.class, true);
		addColumn(get(TextID.Label_sort), true, Integer.class, true);
		addColumn(get(TextID.Filter_sort), true, Integer.class, true);
		addColumn(get(TextID.Search_sort), true, Integer.class, true);
	}

	public void setData(List<ObjectAttribute> objectAttributes){
		super.resetChanges();
		this.objectAttributes = objectAttributes;
	}

	public void setAdvancedView(boolean advancedView){
		if (this.advancedView != advancedView){
			this.advancedView = advancedView;
			fireTableStructureChanged();
		}
	}

	public boolean isAdvancedView(){
		return advancedView;
	}

	@Override
	public int getColumnCount(){
		int count = super.getColumnCount();
		return advancedView ? count : count - 4;
	}

	public int getRowCount(){
		if (objectAttributes == null){
			return 0;
		}
		return objectAttributes.size();
	}

	public boolean isCellEditable(int rowIndex, int columnIndex){
		String colName = getColumnName(columnIndex);
		ObjectAttribute oa = objectAttributes.get(rowIndex);

		if (get(TextID.InFilter).equals(colName) || get(TextID.InPrefilter).equals(colName)){
			return oa.isPredefined();
		} else{
			if (get(TextID.Aggregable).equals(colName)){
				return oa.isIntDataType() || oa.isDecimalDataType();
			} else if (get(TextID.InSearch).equals(colName)){
				return !oa.isDateDataType();
			} else if (get(TextID.InExport).equals(colName)){
				return !oa.isInContext();
			} else if (get(TextID.Multivalue).equals(colName)){
				return !ObjectAttribute.isFixedEdgeAttribute(oa, false) && !ObjectAttribute.isFixedNodeAttribute(oa, false)
				        && !oa.isBoolDataType();
			}
		}
		return super.isCellEditable(rowIndex, columnIndex);
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		ObjectAttribute oa = objectAttributes.get(rowIndex);
		switch (columnIndex){
			case 0:
				return oa.getLabel();
			case 1:
				return oa.isInMetaphor();
			case 2:
				return oa.getSort();
			case 3:
				return oa.isPredefined();
			case 4:
				return oa.isInFilter();
			case 5:
				return oa.isInLabel();
			case 6:
				return oa.isInToolTip();
			case 7:
				return oa.isInSimpleSearch();
			case 8:
				return oa.isLabelBold();
			case 9:
				return oa.isLabelItalic();
			case 10:
				return oa.isLabelUnderline();
			case 11:
				return oa.isContentBold();
			case 12:
				return oa.isContentItalic();
			case 13:
				return oa.isContentUnderline();
			case 14:
				return oa.isInExport();
			case 15:
				return oa.isInAdvancedSearch();
			case 16:
				return oa.isInPrefilter();
			case INCONTEXT_COLUMN_INDEX:
				return oa.isInContext();
			case INMATRIX_COLUMN_INDEX:
				return oa.getInMatrix() != null ? InMatrixType.getInMatrixType(oa.getInMatrix()) : null;
			case AGGREGABLE_COLUMN_INDEX:
				return oa.isAggregable();
			case MULTIVALUE_COLUMN_INDEX:
				return oa.getIsMultivalue();
			case 21:
				return oa.getMatrixSort();
			case 22:
				return oa.getLabelSort();
			case 23:
				return oa.getFilterSort();
			case 24:
				return oa.getSearchSort();

			default:
				return null;
		}
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		ObjectAttribute oa = objectAttributes.get(rowIndex);
		super.setValueAt(aValue, rowIndex, columnIndex);
		switch (columnIndex){
			case 0:
				break;
			case 1:
				oa.setInMetaphor((Boolean) aValue);
				break;
			case 2:
				oa.setSort((Integer) aValue);
				break;
			case 3:
				oa.setPredefined((Boolean) aValue);
				break;
			case 4:
				oa.setInFilter((Boolean) aValue);
				break;
			case 5:
				oa.setInLabel((Boolean) aValue);
				break;
			case 6:
				oa.setInToolTip((Boolean) aValue);
				break;
			case 7:
				oa.setInSimpleSearch((Boolean) aValue);
				break;
			case 8:
				oa.setLabelBold((Boolean) aValue);
				break;
			case 9:
				oa.setLabelItalic((Boolean) aValue);
				break;
			case 10:
				oa.setLabelUnderline((Boolean) aValue);
				break;
			case 11:
				oa.setContentBold((Boolean) aValue);
				break;
			case 12:
				oa.setContentItalic((Boolean) aValue);
				break;
			case 13:
				oa.setContentUnderline((Boolean) aValue);
				break;
			case 14:
				oa.setInExport((Boolean) aValue);
				break;
			case 15:
				oa.setInAdvancedSearch((Boolean) aValue);
				break;
			case 16:
				oa.setInPrefilter((Boolean) aValue);
				break;
			case INCONTEXT_COLUMN_INDEX:
				oa.setInContext((Boolean) aValue);
				if (oa.isInContext()){
					oa.setInExport(false);
				}
				fireTableRowsUpdated(rowIndex, rowIndex);
				break;
			case INMATRIX_COLUMN_INDEX:
				if (listener != null && !listener.canChangeValue(aValue, oa, columnIndex))
					break;
				InMatrixType type = (InMatrixType) aValue;
				oa.setInMatrix(type != null ? type.getValue() : null);
				break;
			case AGGREGABLE_COLUMN_INDEX:
				oa.setAggregable((Boolean) aValue);
				break;
			case MULTIVALUE_COLUMN_INDEX:
				boolean multivalue = (Boolean) aValue;
				oa.setIsMultivalue(multivalue);
				break;
			case 21:
				oa.setMatrixSort((Integer) aValue);
				break;
			case 22:
				oa.setLabelSort((Integer) aValue);
				break;
			case 23:
				oa.setFilterSort((Integer) aValue);
				break;
			case 24:
				oa.setSearchSort((Integer) aValue);
				break;

			default:
				throw new InvalidParameterException("invalid column index given");
		}
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

	public boolean isColumnEditable(int columnIndex){
		return super.isCellEditable(0, columnIndex);
	}

	public void setValueChangeListener(AttributeTableModelValueChangeListener listener){
		this.listener = listener;
	}

	public boolean isPhysicalDataTypeChanged(){
		for (int row = 0; row < objectAttributes.size(); row++)
			if (super.isChanged(row, INCONTEXT_COLUMN_INDEX) || super.isChanged(row, MULTIVALUE_COLUMN_INDEX))
				return true;
		return false;
	}

	@Override
	public String getToolTip(int row, int column){
		if (isCellEditable(row, column)){
			return null;
		}
		String colName = getColumnName(column);
		ObjectAttribute oa = objectAttributes.get(row);

		if ((get(TextID.InFilter).equals(colName) || get(TextID.InPrefilter).equals(colName)) && !oa.isPredefined()){
			return get(TextID.ReadonlyNotValueListAttribute);
		} else if (get(TextID.Aggregable).equals(colName)){
			return get(TextID.ReadonlyDataTypeNumerical);
		} else if (get(TextID.Multivalue).equals(colName)){
			return get(TextID.FixedAndBooleanAttributesCantBeMultivalue);
		}
		return get(TextID.ReadonlyConfigurableOnOtherScreen, new String[] { get(TextID.Schemas) });
	}
}