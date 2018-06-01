/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DefaultFormatterFactory;

import com.ni3.ag.navigator.client.controller.charts.SNA.SNAAttribute;
import com.ni3.ag.navigator.client.domain.datatype.Ni3Datatype;
import com.ni3.ag.navigator.shared.constants.DynamicAttributeOperation;
import com.ni3.ag.navigator.shared.constants.EditingOption;
import com.ni3.ag.navigator.shared.domain.DataType;
import com.ni3.ag.navigator.shared.proto.NResponse.AttributeValue;

public class Attribute{
	public static final String ID_ATTRIBUTE_NAME = "id";
	public static final String LON_ATTRIBUTE_NAME = "lon";
	public static final String LAT_ATTRIBUTE_NAME = "lat";
	public static final String FAVORITEID_ATTRIBUTE_NAME = "favoritesid";
	public static final String CONNECTIONTYPE_ATTRIBUTE_NAME = "connectionType";
	public static final String ICONNAME_ATTRIBUTE_NAME = "iconname";

	public static final String FROMID_ATTRIBUTE_NAME = "FromID";
	public static final String TOID_ATTRIBUTE_NAME = "ToID";
	public static final String STRENGTH_ATTRIBUTE_NAME = "Strength";

	public enum EDynamicAttributeScope{
		None, Graph, Matrix, Database;

		@Override
		public String toString(){
			return name();
		}
	}

	public int ID;
	private int sort;
	private int sortLabel, sortFilter, sortSearch, sortDisplay, sortMatrix;
	private DataType dType;
	public String name;
	public String label;
	public String description;
	public boolean predefined;
	private boolean formula;
	public boolean multivalue;
	public boolean inFilter;
	public boolean inLabel;
	public boolean inToolTip;
	private boolean inAdvancedSearch;
	public boolean inMetaphor;
	public boolean inExport;
	public boolean inPrefilter;
	public boolean inContext;
	private boolean aggregable;
	private int inMatrix;

	private boolean isDynamic;
	private SNAAttribute snaAttribute;
	private EDynamicAttributeScope dynamicScope;
	private Entity dynamicFromEntity;
	private Attribute dynamicFromAttribute;
	private DynamicAttributeOperation dynamicOperation;

	private boolean labelBold;
	private boolean labelItalic;
	private boolean labelUnderline;
	private boolean contentBold;
	private boolean contentItalic;
	private boolean contentUnderline;

	private boolean canRead;
	private EditingOption editingUnlock;
	private EditingOption editingLock;

	public String validchars, invalidchars;
	public Object minVal, maxVal;

	public String regularExpression, valueDescription;
	public DefaultFormatterFactory formatFactory;

	private AbstractFormatter displayFormatter; // MaskFormatter or Format
	private AbstractFormatter editFormatter; // MaskFormatter or Format
	public Pattern regEx;

	public Entity ent;

	private List<Value> values;
	private List<Value> valuesToUse;

	public static Value nullValue = new Value(0, 0, "null", " ");

	private Ni3Datatype datatype;

	public Attribute(){
	}

	public Attribute(Entity entity, com.ni3.ag.navigator.shared.proto.NResponse.Attribute attribute){
		this.ent = entity;

		this.ID = attribute.getId();
		this.name = attribute.getName();
		this.label = attribute.getLabel();
		this.description = attribute.getDescription();
		if (this.description == null){
			this.description = "";
		}
		this.predefined = attribute.getPredefined();
		this.formula = attribute.getFormula();
		this.inFilter = attribute.getInFilter();
		this.inLabel = attribute.getInLabel();
		this.inToolTip = attribute.getInToolTip();
		this.inAdvancedSearch = attribute.getInAdvancedSearch();
		this.inMatrix = attribute.getInMatrix();
		this.inMetaphor = attribute.getInMetaphor();
		this.inExport = attribute.getInExport();
		this.inPrefilter = attribute.getInPrefilter();
		this.inContext = attribute.getInContext();
		this.dType = DataType.fromInt(attribute.getDataTypeId());
		this.sort = attribute.getSort();
		this.sortLabel = attribute.getSortLabel();
		this.sortFilter = attribute.getSortFilter();
		this.sortSearch = attribute.getSortSearch();
		this.sortMatrix = attribute.getSortMatrix();
		this.sortDisplay = this.sort;
		this.labelBold = attribute.getLabelBold();
		this.labelItalic = attribute.getLabelItalic();
		this.labelUnderline = attribute.getLabelUnderline();
		this.contentBold = attribute.getContentBold();
		this.contentItalic = attribute.getContentItalic();
		this.contentUnderline = attribute.getContentUnderline();
		this.validchars = attribute.getValidCharacters();
		this.invalidchars = attribute.getInvalidCharacters();
		this.regularExpression = attribute.getRegularExpression();
		this.valueDescription = attribute.getValueDescription();
		this.multivalue = attribute.getMultivalue();
		this.aggregable = attribute.getAggregable();
		this.canRead = attribute.getCanRead();
		this.editingLock = EditingOption.fromProtoBufValue(attribute.getEditLock());
		this.editingUnlock = EditingOption.fromProtoBufValue(attribute.getEditUnlock());

		this.isDynamic = false;
		this.dynamicScope = EDynamicAttributeScope.None;

		datatype = Ni3Datatype.createDatatype(this);
		if (predefined){
			values = new ArrayList<Value>(10);
			valuesToUse = new ArrayList<Value>(10);
		}

		if (!predefined){
			String Format = attribute.getFormat();
			String sminVal = attribute.getMinValue();
			String smaxVal = attribute.getMaxValue();
			String editFormat = attribute.getEditFormat();

			minVal = datatype.getValue(sminVal);
			maxVal = datatype.getValue(smaxVal);

			if (Format.length() == 0 || "0".equals(Format) || "null".equalsIgnoreCase(Format))
				displayFormatter = datatype.getDefaultDisplayFormatter();
			else
				displayFormatter = datatype.createDisplayFormatter(Format);

			if (editFormat.length() == 0 || "0".equals(editFormat) || "null".equalsIgnoreCase(editFormat))
				editFormatter = datatype.getDefaultEditFormatter();
			else
				editFormatter = datatype.createEditFormatter(editFormat);

			if (displayFormatter != null && editFormatter == null)
				editFormatter = displayFormatter;

			if (displayFormatter == null && editFormatter != null)
				displayFormatter = editFormatter;

			if (displayFormatter != null){
				formatFactory = new DefaultFormatterFactory();
				formatFactory.setDefaultFormatter(displayFormatter);
				formatFactory.setEditFormatter(editFormatter);
				formatFactory.setDisplayFormatter(displayFormatter);
			} else
				formatFactory = null;

			if (null == regularExpression || "0".equals(regularExpression) || "null".equalsIgnoreCase(regularExpression)
					|| regularExpression.isEmpty())
				regEx = null;
			else
				regEx = Pattern.compile(regularExpression);

			if ("0".equals(valueDescription) || "null".equalsIgnoreCase(valueDescription))
				valueDescription = null;
		}

	}

	/**
	 * Constructor for dynamic attribute
	 * 
	 * @param from
	 *            ,src - Entity and attribute to aggregate
	 */
	public Attribute(Entity ent, Entity from, Attribute src, DynamicAttributeOperation operation,
			EDynamicAttributeScope scope){
		this.ent = ent;

		dynamicOperation = operation;
		this.dynamicScope = scope;

		inToolTip = true;
		inMatrix = 2;
		dType = DataType.DECIMAL;
		datatype = Ni3Datatype.createDatatype(this);

		sort = ent.getAllAttributes().size();
		sortLabel = ent.getInLabelAttributes().size();
		sortFilter = ent.getAttributesSortedForFilter().size();
		sortSearch = ent.getInAdvancedSearchAttributes().size();
		sortMatrix = ent.getAttributesSortedForMatrix().size();
		sortDisplay = sort;

		validchars = src.validchars;
		invalidchars = src.invalidchars;
		regularExpression = src.regularExpression;
		valueDescription = src.valueDescription;
		aggregable = true;
		setCanRead(src.isCanRead());

		isDynamic = true;
		dynamicFromEntity = from;
		dynamicFromAttribute = src;

		ID = -ent.getAllAttributes().size();
		name = src.name + "(aggregated)";
		createLabel();

		description = src.description + "(aggregated)";

		this.editingLock = EditingOption.NotVisible;
		this.editingUnlock = EditingOption.NotVisible;

		initDefaultFormatters();
	}

	public Attribute(Entity ent, SNAAttribute snaAttribute){
		this.ent = ent;
		ID = snaAttribute.getId();
		name = snaAttribute.getLabel();
		label = UserSettings.getWord(snaAttribute.getLabel());
		this.snaAttribute = snaAttribute;

		inMatrix = 2;
		dType = DataType.DECIMAL;
		datatype = Ni3Datatype.createDatatype(this);

		sort = ent.getAllAttributes().size();
		sortLabel = ent.getInLabelAttributes().size();
		sortFilter = ent.getAttributesSortedForFilter().size();
		sortSearch = ent.getInAdvancedSearchAttributes().size();
		sortMatrix = ent.getAttributesSortedForMatrix().size();
		sortDisplay = sort;
		canRead = true;

		this.editingLock = EditingOption.NotVisible;
		this.editingUnlock = EditingOption.NotVisible;

		initDefaultFormatters();
	}

	private void initDefaultFormatters(){
		displayFormatter = datatype.getDefaultDisplayFormatter();
		editFormatter = datatype.getDefaultEditFormatter();

		if (displayFormatter != null && editFormatter == null)
			editFormatter = displayFormatter;

		if (displayFormatter == null && editFormatter != null)
			displayFormatter = editFormatter;

		if (displayFormatter != null){
			formatFactory = new DefaultFormatterFactory();
			formatFactory.setDefaultFormatter(displayFormatter);
			formatFactory.setEditFormatter(editFormatter);
			formatFactory.setDisplayFormatter(displayFormatter);
		} else
			formatFactory = null;
	}

	public boolean isLabelBold(){
		return labelBold;
	}

	public boolean isLabelItalic(){
		return labelItalic;
	}

	public boolean isLabelUnderline(){
		return labelUnderline;
	}

	public boolean isContentBold(){
		return contentBold;
	}

	public boolean isContentItalic(){
		return contentItalic;
	}

	public boolean isContentUnderline(){
		return contentUnderline;
	}

	public int getInMatrix(){
		return inMatrix;
	}

	public List<Value> getValues(){
		return values;
	}

	public List<Value> getValuesToUse(){
		return valuesToUse;
	}

	public void createLabel(){
		label = dynamicFromAttribute.label + "(aggregated)";
		description = dynamicFromAttribute.description + "(aggregated)";
		switch (dynamicScope){
			case Graph:
				label += "(G)";
				description += "(G)";
				break;

			case Database:
				label += "(D)";
				description += "(D)";
				break;
		}
	}

	public int getSortLabel(){
		return sortLabel;
	}

	public int getSortFilter(){
		return sortFilter;
	}

	public int getSortSearch(){
		return sortSearch;
	}

	public int getSortDisplay(){
		return sortDisplay;
	}

	public int getSortMatrix(){
		return sortMatrix;
	}

	public void setSortMatrix(int sortMatrix){
		this.sortMatrix = sortMatrix;
	}

	public DynamicAttributeOperation getDynamicOperation(){
		return dynamicOperation;
	}

	public Attribute getDynamicFromAttribute(){
		return dynamicFromAttribute;
	}

	public Entity getDynamicFromEntity(){
		return dynamicFromEntity;
	}

	public boolean isDynamic(){
		return isDynamic;
	}

	public EDynamicAttributeScope getDynamicScope(){
		return dynamicScope;
	}

	public void setDynamicScope(EDynamicAttributeScope dynamicScope){
		this.dynamicScope = dynamicScope;
	}

	public boolean isSnaAttribute(){
		return snaAttribute != null;
	}

	public SNAAttribute getSnaAttribute(){
		return snaAttribute;
	}

	public int getSort(){
		return sort;
	}

	public void setSort(int sort){
		this.sort = sort;
	}

	public String displayValueAsPartOfHTML(Object val){
		return displayValue(val, true, false);
	}

	public String displayValueAsFullHTML(Object val){
		return displayValue(val, true, true);
	}

	public String displayValue(Object val){
		return displayValue(val, false, false);
	}

	private String displayValue(Object val, boolean isHTML, boolean withHTMLTag){
		String result;
		if (predefined){
			if (val == null || "".equals(val))
				result = "";
			else if (multivalue){
				StringBuilder ret = new StringBuilder();

				for (Value v : (Value[]) val){
					if (ret.length() > 0){
						ret.append(";");
					}
					ret.append(v.getLabel());
				}

				result = ret.toString();
			} else{
				result = ((Value) val).getLabel();
			}
			if (isURLAttribute() && isHTML && withHTMLTag)
				result = displaySingleValue(result, isHTML, withHTMLTag);
		} else if (multivalue){
			result = displayMultivalue(val, isHTML, withHTMLTag);
		} else{
			result = displaySingleValue(val, isHTML, withHTMLTag);
		}
		return result;
	}

	private String displaySingleValue(Object val, boolean isHTML, boolean withHTMLTag){
		String result = datatype.displayValue(val, isHTML);
		if (isURLAttribute() && isHTML && withHTMLTag)
			result = appendWithHtmlTag(result);

		return result;
	}

	private String displayMultivalue(Object val, boolean isHTML, boolean withHTMLTag){
		if (val == null)
			return "";

		StringBuilder ret = new StringBuilder();

		boolean notfirst = false;
		for (Object s : (Object[]) val){
			if (notfirst){
				ret.append(";");
			}
			ret.append(displaySingleValue(s, isHTML, false));

			notfirst = true;
		}

		if (isURLAttribute() && isHTML && withHTMLTag)
			return appendWithHtmlTag(ret);

		return ret.toString();
	}

	private String appendWithHtmlTag(Object val){
		return "<HTML>" + val + "</HTML>";
	}

	public Value getValue(String val){
		if (predefined){
			if (val == null || "null".equals(val) || val.length() == 0)
				return null;

			int id = Integer.valueOf(val);
			for (Value v : values){
				if (v.getId() == id)
					return v;
			}
		}

		return null;
	}

	public Value getValue(int ID){
		if (predefined){
			for (Value v : values){
				if (v.getId() == ID)
					return v;
			}
		}

		return null;
	}

	public String toString(){
		return label;
	}

	public String displayInCSV(Object object){
		if (multivalue){
			if (isURLAttribute())
				return displayMultiURLPlain(object);
			else
				return displayMultivalue(object, false, false);
		} else
			return displayValue(object, false, false);
	}

	private String displayMultiURLPlain(Object object){
		if (object == null)
			return null;
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Object s : (Object[]) object){
			if (!first)
				sb.append(";");
			else
				first = false;
			sb.append(s);
		}

		return sb.toString();
	}

	public boolean containsChildPredefineds(){
		boolean result = false;
		if (values != null){
			for (Value v : values){
				if (v.getParentId() > 0){
					result = true;
					break;
				}
			}
		}
		return result;
	}

	public DataType getDType(){
		return dType;
	}

	public Ni3Datatype getDataType(){
		return datatype;
	}

	public boolean isInAdvancedSearch(){
		return inAdvancedSearch;
	}

	public void setInAdvancedSearch(boolean inAdvancedSearch){
		this.inAdvancedSearch = inAdvancedSearch;
	}

	public void setCanRead(boolean canRead){
		this.canRead = canRead;
	}

	public boolean isCanRead(){
		return canRead;
	}

	private EditingOption getEditingOption(boolean locked){
		return (locked ? editingLock : editingUnlock);
	}

	public boolean isDisplayableOnEdit(boolean locked){
		return isCanRead() && (getEditingOption(locked) != EditingOption.NotVisible);
	}

	public boolean isEditable(boolean locked){
		return isCanRead() && getEditingOption(locked).getValue() > EditingOption.ReadOnly.getValue();
	}

	public boolean isMandatoryOnEdit(boolean locked){
		return isCanRead() && getEditingOption(locked) == EditingOption.Mandatory;
	}

	public boolean isNumericAttribute(){
		return dType == DataType.INT || dType == DataType.DECIMAL;
	}

	public boolean isURLAttribute(){
		return dType == DataType.URL;
	}

	public boolean isDateAttribute(){
		return dType == DataType.DATE;
	}

	public boolean isTextAttribute(){
		return dType == DataType.TEXT;
	}

	public boolean isBooleanAttribute(){
		return dType == DataType.BOOL;
	}

	public boolean isFormula(){
		return formula;
	}

	public boolean isSystemAttribute(){
		if (ent.isNode())
			return LON_ATTRIBUTE_NAME.equalsIgnoreCase(name) || LAT_ATTRIBUTE_NAME.equalsIgnoreCase(name)
					|| FAVORITEID_ATTRIBUTE_NAME.equalsIgnoreCase(name) || ICONNAME_ATTRIBUTE_NAME.equalsIgnoreCase(name)
					|| ID_ATTRIBUTE_NAME.equalsIgnoreCase(name);

		if (ent.isEdge())
			return FROMID_ATTRIBUTE_NAME.equalsIgnoreCase(name) || TOID_ATTRIBUTE_NAME.equalsIgnoreCase(name)
					|| STRENGTH_ATTRIBUTE_NAME.equalsIgnoreCase(name);

		return false;
	}

	public void loadValues(com.ni3.ag.navigator.shared.proto.NResponse.Attribute wireAttribute){
		if (values == null){
			values = new ArrayList<Value>(10);
			valuesToUse = new ArrayList<Value>();
		}

		final List<AttributeValue> valuesList = wireAttribute.getValuesList();
		for (AttributeValue attributeValue : valuesList){
			final Value value = new Value(attributeValue, this);
			values.add(value);
		}
		Collections.sort(values, new Comparator<Value>(){
			@Override
			public int compare(Value o1, Value o2){
				return o1.getSort() - o2.getSort();
			}
		});
		for (Value value : values){
			if (value.isToUse()){
				valuesToUse.add(value);
			}
			Schema.PredefinedAttributesValue.put(value.getId(), value);
			Schema.PredefinedAttributesSort.put(value.getId(), value.getSort());
		}
	}

	public boolean isAggregable(){
		return aggregable;
	}
}
