/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.domain;

import java.io.Serializable;
import java.util.List;

import com.ni3.ag.navigator.server.type.PredefinedType;
import com.ni3.ag.navigator.shared.domain.DataType;

public class Attribute implements Serializable{
	private static final long serialVersionUID = -416923167250595922L;
	public static final String STRENGTH_ATTRIBUTE_NAME = "Strength";
	public static final String SRCID_ATTRIBUTE_NAME = "SrcId";

	private PredefinedType predefined;

	private int id;
	private int Sort;
	private String name;
	private String label;

	private Integer predefined_;
	private Integer inFilter_;
	private Integer inLabel_;
	private Integer inToolTip_;
	private Integer inSimpleSearch_;
	private Integer inAdvancedSearch_;
	private Integer inMetaphor_;
	private Integer labelBold_;
	private Integer labelItalic_;
	private Integer labelUnderline_;
	private Integer contentBold_;
	private Integer contentItalic_;
	private Integer contentUnderline_;
	private Integer inExport_;
	private Integer inPrefilter_;
	private Integer isAggregable;
	private Boolean multivalue;
	private Integer inContext_;
	private Integer dataType_;

	private List<AttributeGroup> attributeGroups;

	private ObjectDefinition entity;

	private List<PredefinedAttribute> values;

	private String description;
	private int inMatrix;
	private int sortLabel;
	private int sortFilter;
	private int sortSearch;
	private int sortMatrix;
	private String format;
	private String editFormat;
	private String validCharacters;
	private String invalidCharacters;
	private String minValue;
	private String maxValue;
	private String regExpression;
	private String valueDescription;
	private int editLocked;
	private int editUnlocked;
	private String dataSource;
	private Formula formula_;

	public Attribute(){
	}

	public Attribute(int id){
		this.id = id;
	}

	public boolean canGroupsRead(final int groupsID[]){
		if (groupsID == null || attributeGroups == null)
			return false;
		for (final int i : groupsID){
			AttributeGroup ag = getForGroup(i);
			if (ag == null)
				return false;
			if (ag.getCanRead()){
				return true;
			}
		}

		return false;
	}

	private AttributeGroup getForGroup(int groupId){
		if (attributeGroups == null || attributeGroups.isEmpty())
			return null;
		for (AttributeGroup ag : attributeGroups)
			if (ag.getGroupId() == groupId)
				return ag;
		return null;
	}

	public boolean canGroupRead(int groupID){
		return canGroupsRead(new int[] { groupID });
	}

	public String[] formatValueBind(final String val, final String operator){
		String ret[];
		switch (getDatabaseDatatype()){
			case TEXT:
				ret = formatValueBindText(val, operator);
				break;
			case INT:
				ret = formatValueBindInt(val, operator);
				break;
			case DECIMAL:
				ret = formatValueBindDecimal(val, operator);
				break;
			default:
				ret = new String[2];
				ret[0] = " ILIKE ?";
				ret[1] = "%" + val + "%";
				break;
		}
		return ret;
	}

	private String[] formatValueBindDecimal(String val, String operator){
		String[] ret = new String[2];
		if (val == null || val.isEmpty())
			ret[0] = operator + "0";
		else
			ret[0] = operator + Double.valueOf(val);
		return ret;
	}

	private String[] formatValueBindInt(String val, String operator){
		String[] ret = new String[2];
		if (val == null || val.isEmpty()){
			ret[0] = operator + "0";
		} else if (val.contains(",")) // multiple predefined attributes are selected in search
		{
			if ("=".equals(operator)){
				ret[0] = " IN ";
			} else{
				ret[0] = " NOT IN ";
			}
			ret[0] += "(" + val.replaceAll("[^0-9,-]", "") + ")";
		} else
			ret[0] = operator + Integer.valueOf(val);
		return ret;
	}

	private String[] formatValueBindText(String val, String operator){
		String[] ret = new String[2];
		if (val == null || val.isEmpty()){
			ret[0] = " IS NULL";
		} else{
			if ("~".equals(operator)){
				ret[0] = " ILIKE ?";
				if (isMultivalue()){
					ret[1] = "%{%" + val + "%}%";
				} else{
					ret[1] = "%" + val + "%";
				}
			} else if ("<>".equals(operator) || "=".equals(operator)){
				if ("<>".equals(operator)){
					ret[0] = " NOT ILIKE ?";
				} else{
					ret[0] = " ILIKE ?";
				}
				if (isMultivalue()){
					ret[1] = "%{" + val + "}%";
				} else{
					ret[1] = val;
				}
			} else{
				ret[0] = operator + " ?";
				ret[1] = val;
			}
		}
		return ret;
	}

	public DataType getDataType(){
		return dataType_ != null ? DataType.fromInt(dataType_) : null;
	}

	public void setDataType(DataType dataType){
		this.dataType_ = (dataType != null ? dataType.toInt() : null);
	}

	public DataType getDatabaseDatatype(){
		DataType dbDataType = getDataType();
		if (isMultivalue()){
			dbDataType = DataType.TEXT;
		} else if (isPredefined()){
			dbDataType = DataType.INT;
		} else if (getDataType() == DataType.DATE || getDataType() == DataType.URL){
			dbDataType = DataType.TEXT;
		} else if (getDataType() == DataType.BOOL){
			dbDataType = DataType.INT;
		}
		return dbDataType;
	}

	public ObjectDefinition getEntity(){
		return entity;
	}

	public String getFormula(){
		return formula_ != null ? formula_.getFormula() : null;
	}

	public int getId(){
		return id;
	}

	public String getLabel(){
		return label;
	}

	public String getName(){
		return name;
	}

	public PredefinedType getPredefined(){
		return predefined;
	}

	public int getSort(){
		return Sort;
	}

	public PredefinedAttribute getValue(final int ID){
		if (isPredefined()){
			for (final PredefinedAttribute v : getValues()){
				if (v.getId() == ID){
					return v;
				}
			}
		}

		return null;
	}

	public PredefinedAttribute getPredefinedValueByValue(final String value){
		if (isPredefined()){
			if (value == null || "null".equals(value) || value.isEmpty()){
				return null;
			}

			for (final PredefinedAttribute v : getValues()){
				if (v.getValue().equals(value)){
					return v;
				}
			}
		}

		return null;
	}

	public List<PredefinedAttribute> getValues(){
		return values;
	}

	public boolean isFormula(){
		return PredefinedType.Formula.equals(predefined) || PredefinedType.FormulaPredefined.equals(predefined);
	}

	public Boolean isInFilter(){
		return getInFilter_() != null && getInFilter_() == 1;
	}

	public void setInFilter(Boolean inFilter){
		setInFilter_(inFilter ? 1 : 0);
	}

	public Boolean isInLabel(){
		return getInLabel_() != null && getInLabel_() == 1;
	}

	public void setInLabel(Boolean inLabel){
		setInLabel_(inLabel ? 1 : 0);
	}

	public Boolean isInToolTip(){
		return getInToolTip_() != null && getInToolTip_() == 1;
	}

	public void setInToolTip(Boolean inToolTip){
		setInToolTip_(inToolTip ? 1 : 0);
	}

	public Boolean isInAdvancedSearch(){
		return getInAdvancedSearch_() != null && getInAdvancedSearch_() == 1;
	}

	public void setInAdvancedSearch(Boolean inAdvancedSearch){
		setInAdvancedSearch_(inAdvancedSearch ? 1 : 0);
	}

	public Boolean isInMetaphor(){
		return getInMetaphor_() != null && getInMetaphor_() == 1;
	}

	public void setInMetaphor(Boolean inMetaphor){
		setInMetaphor_(inMetaphor ? 1 : 0);
	}

	public Boolean isLabelBold(){
		return getLabelBold_() != null && getLabelBold_() == 1;
	}

	public void setLabelBold(Boolean labelBold){
		setLabelBold_(labelBold ? 1 : 0);
	}

	public Boolean isLabelItalic(){
		return getLabelItalic_() != null && getLabelItalic_() == 1;
	}

	public void setLabelItalic(Boolean labelItalic){
		setLabelItalic_(labelItalic ? 1 : 0);
	}

	public Boolean isLabelUnderline(){
		return getLabelUnderline_() != null && getLabelUnderline_() == 1;
	}

	public void setLabelUnderline(Boolean labelUnderline){
		setLabelUnderline_(labelUnderline ? 1 : 0);
	}

	public Boolean isContentBold(){
		return getContentBold_() != null && getContentBold_() == 1;
	}

	public void setContentBold(Boolean contentBold){
		setContentBold_(contentBold ? 1 : 0);
	}

	public Boolean isContentItalic(){
		return getContentItalic_() != null && getContentItalic_() == 1;
	}

	public void setContentItalic(Boolean contentItalic){
		setContentItalic_(contentItalic ? 1 : 0);
	}

	public Boolean isContentUnderline(){
		return getContentUnderline_() != null && getContentUnderline_() == 1;
	}

	public void setContentUnderline(Boolean contentUnderline){
		setContentUnderline_(contentUnderline ? 1 : 0);
	}

	public Boolean isInExport(){
		return getInExport_() != null && getInExport_() == 1;
	}

	public void setInExport(Boolean inExport){
		setInExport_(inExport ? 1 : 0);
	}

	public Boolean isInSimpleSearch(){
		return getInSimpleSearch_() != null && getInSimpleSearch_() == 1;
	}

	public void setInSimpleSearch(Boolean inSimpleSearch){
		setInSimpleSearch_(inSimpleSearch ? 1 : 0);
	}

	public Boolean isInPrefilter(){
		return getInPrefilter_() != null && getInPrefilter_() == 1;
	}

	public void setInPrefilter(Boolean inPrefilter){
		setInPrefilter_(inPrefilter ? 1 : 0);
	}

	private void setIsAggregable(Integer isAggregable){
		this.isAggregable = isAggregable;
	}

	private Integer getIsAggregable(){
		return isAggregable;
	}

	public boolean isAggregable(){
		return getIsAggregable() != null && getIsAggregable() == 1;
	}

	public void setAggregable(Boolean aggregable){
		setIsAggregable(aggregable ? 1 : 0);
	}

	public void setInContext(Boolean inContext){
		setInContext_(inContext ? 1 : 0);
	}

	public Boolean isInContext(){
		return getInContext_() != null && getInContext_() == 1;
	}

	public boolean isMultivalue(){
		return multivalue != null && multivalue;
	}

	public boolean isPredefined(){
		return PredefinedType.Predefined.equals(predefined) || PredefinedType.FormulaPredefined.equals(predefined);
	}

	public void setDatatype(final DataType datatype){
		this.dataType_ = datatype.toInt();
	}

	public void setEntity(final ObjectDefinition entity){
		this.entity = entity;
	}

	public void setId(final int iD){
		id = iD;
	}

	public void setLabel(final String label){
		this.label = label;
	}

	public void setName(final String name){
		this.name = name;
	}

	public void setPredefined(final PredefinedType predefined){
		this.predefined = predefined;
	}

	public void setSort(final Integer sort){
		Sort = sort != null ? sort : 0;
	}

	public void setValues(final List<PredefinedAttribute> values){
		this.values = values;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public int getInMatrix(){
		return inMatrix;
	}

	public void setInMatrix(Integer inMatrix){
		this.inMatrix = inMatrix != null ? inMatrix : 0;
	}

	public int getSortLabel(){
		return sortLabel;
	}

	public void setSortLabel(int sortLabel){
		this.sortLabel = sortLabel;
	}

	public int getSortFilter(){
		return sortFilter;
	}

	public void setSortFilter(int sortFilter){
		this.sortFilter = sortFilter;
	}

	public int getSortSearch(){
		return sortSearch;
	}

	public void setSortSearch(int sortSearch){
		this.sortSearch = sortSearch;
	}

	public int getSortMatrix(){
		return sortMatrix;
	}

	public void setSortMatrix(int sortMatrix){
		this.sortMatrix = sortMatrix;
	}

	public String getFormat(){
		return format;
	}

	public void setFormat(String format){
		this.format = format;
	}

	public String getEditFormat(){
		return editFormat;
	}

	public void setEditFormat(String editFormat){
		this.editFormat = editFormat;
	}

	public String getValidCharacters(){
		return validCharacters;
	}

	public void setValidCharacters(String validCharacters){
		this.validCharacters = validCharacters;
	}

	public String getInvalidCharacters(){
		return invalidCharacters;
	}

	public void setInvalidCharacters(String invalidCharacters){
		this.invalidCharacters = invalidCharacters;
	}

	public String getMinValue(){
		return minValue;
	}

	public void setMinValue(String minValue){
		this.minValue = minValue;
	}

	public String getMaxValue(){
		return maxValue;
	}

	public void setMaxValue(String maxValue){
		this.maxValue = maxValue;
	}

	public String getRegExpression(){
		return regExpression;
	}

	public void setRegExpression(String regExpression){
		this.regExpression = regExpression;
	}

	public String getValueDescription(){
		return valueDescription;
	}

	public void setValueDescription(String valueDescription){
		this.valueDescription = valueDescription;
	}

	public int getEditLocked(){
		return editLocked;
	}

	public void setEditLocked(int editLocked){
		this.editLocked = editLocked;
	}

	public int getEditUnlocked(){
		return editUnlocked;
	}

	public void setEditUnlocked(int editUnlocked){
		this.editUnlocked = editUnlocked;
	}

	public String getDataSource(){
		return dataSource;
	}

	public void setDataSource(String dataSource){
		this.dataSource = dataSource;
	}

	public Attribute clone(){
		Attribute attribute = new Attribute();
		attribute.setId(getId());
		attribute.setName(getName());
		attribute.setLabel(getLabel());
		attribute.setPredefined(getPredefined());
		attribute.setInFilter(isInFilter());
		attribute.setInLabel(isInLabel());
		attribute.setInToolTip(isInToolTip());
		attribute.setInSimpleSearch(isInSimpleSearch());
		attribute.setInAdvancedSearch(isInAdvancedSearch());
		attribute.setInMetaphor(isInMetaphor());
		attribute.setInExport(isInExport());
		attribute.setInPrefilter(isInPrefilter());
		attribute.setInContext(isInContext());
		attribute.setDatatype(getDataType());
		attribute.setMultivalue(getMultivalue());
		attribute.setDescription(getDescription());
		attribute.setInMatrix(getInMatrix());
		attribute.setSort(getSort());
		attribute.setSortLabel(getSortLabel());
		attribute.setSortFilter(getSortFilter());
		attribute.setSortSearch(getSortSearch());
		attribute.setSortMatrix(getSortMatrix());
		attribute.setFormat(getFormat());
		attribute.setEditFormat(getEditFormat());
		attribute.setValidCharacters(getValidCharacters());
		attribute.setInvalidCharacters(getInvalidCharacters());
		attribute.setMinValue(getMinValue());
		attribute.setMaxValue(getMaxValue());
		attribute.setRegExpression(getRegExpression());
		attribute.setValueDescription(getValueDescription());
		attribute.setAggregable(isAggregable());
		attribute.setAttributeGroups(getAttributeGroups());
		attribute.setLabelBold(isLabelBold());
		attribute.setLabelUnderline(isLabelUnderline());
		attribute.setLabelItalic(isLabelItalic());
		attribute.setContentBold(isContentBold());
		attribute.setContentUnderline(isContentUnderline());
		attribute.setContentItalic(isContentItalic());
		return attribute;
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj){
			return true;
		}
		if (!(obj instanceof Attribute)){
			return false;
		}

		return getId() == ((Attribute) obj).getId();
	}

	@Override
	public int hashCode(){
		return id;
	}

	public List<AttributeGroup> getAttributeGroups(){
		return attributeGroups;
	}

	public void setAttributeGroups(List<AttributeGroup> attributeGroups){
		this.attributeGroups = attributeGroups;
	}

	// @Override
	// public String toString(){
	// return "Attribute{" +
	// "predefined=" + predefined +
	// ", id=" + id +
	// ", Sort=" + Sort +
	// ", name='" + name + '\'' +
	// ", label='" + label + '\'' +
	// ", predefined_=" + predefined_ +
	// ", inFilter_=" + inFilter_ +
	// ", inLabel_=" + inLabel_ +
	// ", inToolTip_=" + inToolTip_ +
	// ", inSimpleSearch_=" + inSimpleSearch_ +
	// ", inAdvancedSearch_=" + inAdvancedSearch_ +
	// ", inMetaphor_=" + inMetaphor_ +
	// ", labelBold_=" + labelBold_ +
	// ", labelItalic_=" + labelItalic_ +
	// ", labelUnderline_=" + labelUnderline_ +
	// ", contentBold_=" + contentBold_ +
	// ", contentItalic_=" + contentItalic_ +
	// ", contentUnderline_=" + contentUnderline_ +
	// ", inExport_=" + inExport_ +
	// ", inPrefilter_=" + inPrefilter_ +
	// ", isAggregable=" + isAggregable +
	// ", multivalue=" + multivalue +
	// ", inContext_=" + inContext_ +
	// ", dataType_=" + dataType_ +
	// ", entity=" + entity.getName() +
	// ", values=" + values +
	// ", formula='" + getFormula() + '\'' +
	// ", description='" + description + '\'' +
	// ", inMatrix=" + inMatrix +
	// ", sortLabel=" + sortLabel +
	// ", sortFilter=" + sortFilter +
	// ", sortSearch=" + sortSearch +
	// ", sortMatrix=" + sortMatrix +
	// ", format='" + format + '\'' +
	// ", editFormat='" + editFormat + '\'' +
	// ", validCharacters='" + validCharacters + '\'' +
	// ", invalidCharacters='" + invalidCharacters + '\'' +
	// ", minValue='" + minValue + '\'' +
	// ", maxValue='" + maxValue + '\'' +
	// ", regExpression='" + regExpression + '\'' +
	// ", valueDescription='" + valueDescription + '\'' +
	// ", editLocked=" + editLocked +
	// ", editUnlocked=" + editUnlocked +
	// ", dataSource='" + dataSource + '\'' +
	// '}';
	// }

	public Integer getPredefined_(){
		return predefined_;
	}

	public void setPredefined_(Integer predefined_){
		this.predefined_ = predefined_;
		predefined = PredefinedType.getById(predefined_);
	}

	public Integer getInFilter_(){
		return inFilter_;
	}

	public void setInFilter_(Integer inFilter_){
		this.inFilter_ = inFilter_;
	}

	public Integer getInLabel_(){
		return inLabel_;
	}

	public void setInLabel_(Integer inLabel_){
		this.inLabel_ = inLabel_;
	}

	public Integer getInToolTip_(){
		return inToolTip_;
	}

	public void setInToolTip_(Integer inToolTip_){
		this.inToolTip_ = inToolTip_;
	}

	public Integer getInSimpleSearch_(){
		return inSimpleSearch_;
	}

	public void setInSimpleSearch_(Integer inSimpleSearch_){
		this.inSimpleSearch_ = inSimpleSearch_;
	}

	public Integer getInAdvancedSearch_(){
		return inAdvancedSearch_;
	}

	public void setInAdvancedSearch_(Integer inAdvancedSearch_){
		this.inAdvancedSearch_ = inAdvancedSearch_;
	}

	public Integer getInMetaphor_(){
		return inMetaphor_;
	}

	public void setInMetaphor_(Integer inMetaphor_){
		this.inMetaphor_ = inMetaphor_;
	}

	public Integer getLabelBold_(){
		return labelBold_;
	}

	public void setLabelBold_(Integer labelBold_){
		this.labelBold_ = labelBold_;
	}

	public Integer getLabelItalic_(){
		return labelItalic_;
	}

	public void setLabelItalic_(Integer labelItalic_){
		this.labelItalic_ = labelItalic_;
	}

	public Integer getLabelUnderline_(){
		return labelUnderline_;
	}

	public void setLabelUnderline_(Integer labelUnderline_){
		this.labelUnderline_ = labelUnderline_;
	}

	public Integer getContentBold_(){
		return contentBold_;
	}

	public void setContentBold_(Integer contentBold_){
		this.contentBold_ = contentBold_;
	}

	public Integer getContentItalic_(){
		return contentItalic_;
	}

	public void setContentItalic_(Integer contentItalic_){
		this.contentItalic_ = contentItalic_;
	}

	public Integer getContentUnderline_(){
		return contentUnderline_;
	}

	public void setContentUnderline_(Integer contentUnderline_){
		this.contentUnderline_ = contentUnderline_;
	}

	public Integer getInExport_(){
		return inExport_;
	}

	public void setInExport_(Integer inExport_){
		this.inExport_ = inExport_;
	}

	public Integer getInPrefilter_(){
		return inPrefilter_;
	}

	public void setInPrefilter_(Integer inPrefilter_){
		this.inPrefilter_ = inPrefilter_;
	}

	public Integer getAggregable(){
		return isAggregable;
	}

	public void setAggregable(Integer aggregable){
		isAggregable = aggregable;
	}

	public Boolean getMultivalue(){
		return multivalue;
	}

	public void setMultivalue(Boolean multivalue){
		this.multivalue = multivalue != null ? multivalue : false;
	}

	public Integer getInContext_(){
		return inContext_;
	}

	public void setInContext_(Integer inContext_){
		this.inContext_ = inContext_;
	}

	public Integer getDataType_(){
		return dataType_;
	}

	public void setDataType_(Integer dataType_){
		this.dataType_ = dataType_;
	}

	public Formula getFormula_(){
		return formula_;
	}

	public void setFormula_(Formula formula_){
		this.formula_ = formula_;
	}
}
