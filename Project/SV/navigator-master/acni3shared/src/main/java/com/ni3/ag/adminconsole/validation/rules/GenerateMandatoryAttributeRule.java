/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import static com.ni3.ag.adminconsole.domain.ObjectAttribute.COMMENT_ATTRIBUTE_NAME;
import static com.ni3.ag.adminconsole.domain.ObjectAttribute.CONNECTION_TYPE_ATTRIBUTE_NAME;
import static com.ni3.ag.adminconsole.domain.ObjectAttribute.DIRECTED_ATTRIBUTE_NAME;
import static com.ni3.ag.adminconsole.domain.ObjectAttribute.FAVORITE_ID_ATTRIBUTE_NAME;
import static com.ni3.ag.adminconsole.domain.ObjectAttribute.FROM_ID_ATTRIBUTE_NAME;
import static com.ni3.ag.adminconsole.domain.ObjectAttribute.ICONNAME_ATTRIBUTE_NAME;
import static com.ni3.ag.adminconsole.domain.ObjectAttribute.INPATH_ATTRIBUTE_NAME;
import static com.ni3.ag.adminconsole.domain.ObjectAttribute.LAT_ATTRIBUTE_NAME;
import static com.ni3.ag.adminconsole.domain.ObjectAttribute.LON_ATTRIBUTE_NAME;
import static com.ni3.ag.adminconsole.domain.ObjectAttribute.SRCID_ATTRIBUTE_NAME;
import static com.ni3.ag.adminconsole.domain.ObjectAttribute.STRENGTH_ATTRIBUTE_NAME;
import static com.ni3.ag.adminconsole.domain.ObjectAttribute.TO_ID_ATTRIBUTE_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class GenerateMandatoryAttributeRule implements ACValidationRule{

	private static final String[] edgeFixedNames = ObjectAttribute.getFixedEdgeAttributeNames(false);
	private static final String[] edgeFixedLabels = ObjectAttribute.getFixedEdgeAttributeLabels(false);

	private static final String[] nodeFixedNames = ObjectAttribute.getFixedNodeAttributeNames(false);
	private static final String[] nodeFixedLabels = ObjectAttribute.getFixedNodeAttributeLabels(false);

	public ObjectAttribute createAttribute(String name, String label, ObjectDefinition object){
		ObjectAttribute newAttribute = new ObjectAttribute(object);
		newAttribute.setName(name);
		newAttribute.setLabel(label);
		newAttribute.setInMetaphor(false);
		if (name.equals(DIRECTED_ATTRIBUTE_NAME) || name.equals(INPATH_ATTRIBUTE_NAME)){
			newAttribute.setPredefined(true);
			createBooleanPredefinedAttributes(newAttribute);
		} else if (name.equals(CONNECTION_TYPE_ATTRIBUTE_NAME)){
			newAttribute.setPredefined(true);
			PredefinedAttribute pa = new PredefinedAttribute();
			pa.setObjectAttribute(newAttribute);
			pa.setValue("1");
			pa.setLabel("default");
			newAttribute.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
			newAttribute.getPredefinedAttributes().add(pa);
		} else{
			newAttribute.setPredefined(false);
		}

		newAttribute.setInContext(name.equals(FAVORITE_ID_ATTRIBUTE_NAME));

		newAttribute.setCreated(new Date());

		newAttribute.setInExport(false);
		newAttribute.setInFilter(false);
		newAttribute.setInLabel(false);
		newAttribute.setInPrefilter(false);
		newAttribute.setInSimpleSearch(false);
		newAttribute.setInAdvancedSearch(false);
		newAttribute.setInToolTip(false);
		newAttribute.setLabelBold(false);
		newAttribute.setLabelItalic(false);
		newAttribute.setLabelUnderline(false);
		newAttribute.setContentBold(false);
		newAttribute.setContentItalic(false);
		newAttribute.setContentUnderline(false);
		newAttribute.setInTable(object.getTableName());

		fixAttributeDataType(newAttribute, object);

		setNextSorts(newAttribute, object);

		if (object.getObjectAttributes() == null){
			object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		}
		object.getObjectAttributes().add(newAttribute);

		return newAttribute;
	}

	public void createBooleanPredefinedAttributes(ObjectAttribute attr){
		PredefinedAttribute pa0 = new PredefinedAttribute();
		pa0.setObjectAttribute(attr);
		pa0.setValue("0");
		pa0.setLabel("No");
		pa0.setSort(1);

		PredefinedAttribute pa1 = new PredefinedAttribute();
		pa1.setObjectAttribute(attr);
		pa1.setValue("1");
		pa1.setLabel("Yes");
		pa1.setSort(2);

		attr.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		attr.getPredefinedAttributes().add(pa0);
		attr.getPredefinedAttributes().add(pa1);
	}

	public DataType getDataType(String name){
		DataType dt = DataType.TEXT;
		if (name.equals(COMMENT_ATTRIBUTE_NAME) || name.equals(SRCID_ATTRIBUTE_NAME) || name.equals(ICONNAME_ATTRIBUTE_NAME)){
			dt = DataType.TEXT;
		} else if (name.equals(STRENGTH_ATTRIBUTE_NAME) || name.equals(CONNECTION_TYPE_ATTRIBUTE_NAME)
		        || name.equals(DIRECTED_ATTRIBUTE_NAME) || name.equals(INPATH_ATTRIBUTE_NAME)
		        || name.equals(FROM_ID_ATTRIBUTE_NAME) || name.equals(TO_ID_ATTRIBUTE_NAME)
		        || name.equals(FAVORITE_ID_ATTRIBUTE_NAME)){
			dt = DataType.INT;
		} else if (name.equals(LON_ATTRIBUTE_NAME) || name.equals(LAT_ATTRIBUTE_NAME))
			dt = DataType.DECIMAL;
		return dt;
	}

	@Override
	public boolean performCheck(AbstractModel model){
		ObjectDefinition object = ((SchemaAdminModel) model).getCurrentObjectDefinition();
		if (object == null)
			return false;
		String[] fixedNames = null;
		String[] fixedLabels = null;
		if (object.isEdge()){
			fixedNames = edgeFixedNames;
			fixedLabels = edgeFixedLabels;
		} else if (object.isNode()){
			fixedNames = nodeFixedNames;
			fixedLabels = nodeFixedLabels;
		}
		if (fixedNames == null)
			return false;

		if (object.hasContextAttributes()){
			fixedNames = concatArray(fixedNames, FAVORITE_ID_ATTRIBUTE_NAME);
			fixedLabels = concatArray(fixedLabels, FAVORITE_ID_ATTRIBUTE_NAME);
		}

		for (int i = 0; i < fixedNames.length; i++){
			String fixedName = fixedNames[i];
			String fixedLabel = fixedLabels[i];
			boolean found = false;
			ObjectAttribute attrToFix = null;
			for (ObjectAttribute attribute : object.getObjectAttributes()){
				if (fixedName.equalsIgnoreCase(attribute.getName())){
					attrToFix = attribute;
					found = true;
					break;
				}
			}
			if (!found)
				createAttribute(fixedName, fixedLabel, object);
			else
				fixAttributeDataType(attrToFix, object);
		}

		return true;
	}

	private void fixAttributeDataType(ObjectAttribute attrToFix, ObjectDefinition object){
		DataType dType = getDataType(attrToFix.getName());
		DataType current = attrToFix.getDataType();
		if (current == null)
			attrToFix.setDataType(dType);
		else if (dType != current)
			attrToFix.setDataType(dType);
	}

	public void setNextSorts(ObjectAttribute attribute, ObjectDefinition object){
		int nextSort = 1;
		int nextLabelSort = 1;
		int nextFilterSort = 1;
		int nextSearchSort = 1;
		int nextMatrixSort = 1;
		if (object != null && object.getObjectAttributes() != null){
			for (ObjectAttribute attr : object.getObjectAttributes()){
				if (attr.getSort() != null && attr.getSort() >= nextSort){
					nextSort = attr.getSort() + 1;
				}
				if (attr.getLabelSort() != null && attr.getLabelSort() >= nextLabelSort){
					nextLabelSort = attr.getLabelSort() + 1;
				}
				if (attr.getFilterSort() != null && attr.getFilterSort() >= nextFilterSort){
					nextFilterSort = attr.getFilterSort() + 1;
				}
				if (attr.getSearchSort() != null && attr.getSearchSort() >= nextSearchSort){
					nextSearchSort = attr.getSearchSort() + 1;
				}
				if (attr.getMatrixSort() != null && attr.getMatrixSort() >= nextMatrixSort){
					nextMatrixSort = attr.getMatrixSort() + 1;
				}
			}
		}
		attribute.setSort(nextSort);
		attribute.setLabelSort(nextLabelSort);
		attribute.setFilterSort(nextFilterSort);
		attribute.setSearchSort(nextSearchSort);
		attribute.setMatrixSort(nextMatrixSort);
	}

	private String[] concatArray(String[] array, String appendix){
		String[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[newArray.length - 1] = appendix;
		return newArray;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return new ArrayList<ErrorEntry>();
	}
}
