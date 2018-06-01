/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import static com.ni3.ag.adminconsole.client.view.Translation.get;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class PredefinedAttributeTableModel extends ACTableModel{
	private static final long serialVersionUID = 1L;

	public static final int ATTRIBUTE_COLOR_COLUMN_INDEX = 8;
	public static final int ATTRIBUTE_PARENT_COLOR_INDEX = 6;

	private Logger log = Logger.getLogger(PredefinedAttributeTableModel.class);
	private ObjectAttribute currentAttribute;
	private List<PredefinedAttribute> allPredefineds;

	public PredefinedAttributeTableModel(){
		addColumn(Translation.get(TextID.ID), false, Integer.class, false);
		addColumn(Translation.get(TextID.Value), true, String.class, true);
		addColumn(Translation.get(TextID.Label), true, String.class, true);
		addColumn(Translation.get(TextID.Translation), false, String.class, false);
		addColumn(Translation.get(TextID.ToUse), true, Boolean.class, false);
		addColumn(Translation.get(TextID.Sort), true, Integer.class, false);
		addColumn(Translation.get(TextID.Parent), true, Integer.class, false);
		addColumn(Translation.get(TextID.SrcID), true, String.class, false);
		addColumn(Translation.get(TextID.HaloColor), true, String.class, false);
	}

	public void setData(ObjectAttribute currentAttribute, List<PredefinedAttribute> allPredefineds){
		this.currentAttribute = currentAttribute;
		this.allPredefineds = allPredefineds;
	}

	public int getRowCount(){
		if (currentAttribute != null && currentAttribute.getPredefinedAttributes() != null)
			return currentAttribute.getPredefinedAttributes().size();
		return 0;
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		if (currentAttribute == null || currentAttribute.getPredefinedAttributes() == null)
			return null;
		PredefinedAttribute pa = currentAttribute.getPredefinedAttributes().get(rowIndex);
		switch (columnIndex){
			case 0:
				return pa.getId();
			case 1:
				return pa.getValue();
			case 2:
				return pa.getLabel();
			case 3:
				return pa.getTranslation();
			case 4:
				return pa.getToUse();
			case 5:
				return pa.getSort();
			case 6:
				return pa.getParent() == null ? null : pa.getParent().getId();
			case 7:
				return pa.getSrcID();
			case 8:
				return pa.getHaloColor();
			default:
				return null;
		}
	}

	void freeParent(PredefinedAttribute pa){
		if (pa.getParent() == null)
			return;
		int parentIndex = allPredefineds.indexOf(pa.getParent());
		if (parentIndex == -1)
			return;
		PredefinedAttribute parent = allPredefineds.get(parentIndex);
		if (parent.getChildren() != null)
			parent.getChildren().remove(pa);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		if (currentAttribute == null || currentAttribute.getPredefinedAttributes() == null)
			return;
		aValue = validateValue(aValue);
		super.setValueAt(aValue, rowIndex, columnIndex);
		PredefinedAttribute pa = currentAttribute.getPredefinedAttributes().get(rowIndex);

		log.debug("predefAttr: " + pa);
		switch (columnIndex){
			case 0:
				break;
			case 1:
				log.debug("setValue: " + aValue);
				pa.setValue((String) aValue);
				break;
			case 2:
				log.debug("setLabel: " + aValue);
				pa.setLabel((String) aValue);
				break;
			case 3:
				break;
			case 4:
				log.debug("setToUse: " + aValue);
				pa.setToUse((Boolean) aValue);
				break;
			case 5:
				log.debug("setSort: " + aValue);
				pa.setSort((Integer) aValue);
				break;
			case 6:
				log.debug("setParent: " + aValue);
				// free previous parent
				freeParent(pa);
				if (aValue == null){
					pa.setParent(null);
				} else{

					// set new parent
					PredefinedAttribute parent = new PredefinedAttribute();
					parent.setId((Integer) aValue);
					int index = allPredefineds.indexOf(parent);
					if (index > -1){
						parent = allPredefineds.get(index);
						pa.setNested(false);
						pa.setNestedTo(null);
					}
					// else shows error on update
					if (parent.getChildren() == null)
						parent.setChildren(new ArrayList<PredefinedAttribute>());
					parent.getChildren().add(pa);
					pa.setParent(parent);
				}

				break;
			case 7:
				log.debug("setSrcId: " + aValue);
				pa.setSrcID((String) aValue);
				break;
			case 8:
				String value = (String) aValue;
				pa.setHaloColor(value != null ? value.toUpperCase() : null);
				pa.setNested(false);
				pa.setNestedTo(null);
				break;

		}
		// update all predefined references
		int paindex = allPredefineds.indexOf(pa);
		if (paindex > -1)
			allPredefineds.set(paindex, pa);
	}

	public int indexOf(PredefinedAttribute newAttribute){
		return currentAttribute.getPredefinedAttributes().indexOf(newAttribute);
	}

	@Override
	public String getToolTip(int row, int column){
		if (isCellEditable(row, column)){
			return null;
		}

		String colName = getColumnName(column);
		if (Translation.get(TextID.ID).equals(colName))
			return get(TextID.ReadonlyFilledAutomatically);
		else if (Translation.get(TextID.HaloColor).equals(colName))
			return get(TextID.CantSetHaloForAttributeNotInFilter);
		else
			return get(TextID.ReadonlyConfigurableOnOtherScreen, new String[] { get(TextID.Languages) });
	}

	public boolean isCellEditable(int row, int column){
		return super.isCellEditable(row, column);
	}

	public PredefinedAttribute getPredefinedAttribute(int idx){
		if (currentAttribute.getPredefinedAttributes() == null || idx < 0
		        || idx >= currentAttribute.getPredefinedAttributes().size())
			return null;
		return currentAttribute.getPredefinedAttributes().get(idx);
	}

	public List<PredefinedAttribute> getAllPredefinedAttributes(){
		return allPredefineds;
	}
}