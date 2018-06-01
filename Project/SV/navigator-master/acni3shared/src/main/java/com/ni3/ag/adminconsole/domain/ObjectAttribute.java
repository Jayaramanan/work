/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ObjectAttribute implements Serializable, Cloneable, Comparable<ObjectAttribute>{
	private static final long serialVersionUID = 1L;
	public static final String PREDEFINED = "Predefined";
	public static final String NUMERIC_ATTRIBUTES_FILTER = "NumericAttributes";
	public static final String NOT_FIXED_NODE_ATTRIBUTE_FILTER = "NotFixedNodeAttributes";

	// constant for Criteria in DAO objects - please adjust accordingly is the
	// field name is changed
	public static final String OBJECT_DEFINITION = "objectDefinition";
	public static final String NAME_DB_COLUMN = "name";
	public static final String PREDEFINED_DB_COLUMN = "predefined_";

	public static final String CONTEXT_TABLE_SUFFIX = "_CTXT";

	public static final String COMMENT_ATTRIBUTE_NAME = "Cmnt";
	public static final String DIRECTED_ATTRIBUTE_NAME = "Directed";
	public static final String STRENGTH_ATTRIBUTE_NAME = "Strength";
	public static final String INPATH_ATTRIBUTE_NAME = "InPath";
	public static final String CONNECTION_TYPE_ATTRIBUTE_NAME = "ConnectionType";
	public static final String FROM_ID_ATTRIBUTE_NAME = "FromID";
	public static final String TO_ID_ATTRIBUTE_NAME = "ToID";
	public static final String SRCID_ATTRIBUTE_NAME = "srcid";
	public static final String LON_ATTRIBUTE_NAME = "lon";
	public static final String LAT_ATTRIBUTE_NAME = "lat";
	public static final String ICONNAME_ATTRIBUTE_NAME = "iconname";
	public static final String FAVORITE_ID_ATTRIBUTE_NAME = "favoritesid";

	public static final String CONNECTION_TYPE_ATTRIBUTE_LABEL = "Connection Type";
	public static final String COMMENT_ATTRIBUTE_LABEL = "Comment";

	private static final String[] FIXED_EDGE_ATTRIBUTE_NAMES = new String[] { COMMENT_ATTRIBUTE_NAME,
			DIRECTED_ATTRIBUTE_NAME, STRENGTH_ATTRIBUTE_NAME, INPATH_ATTRIBUTE_NAME, CONNECTION_TYPE_ATTRIBUTE_NAME,
			FROM_ID_ATTRIBUTE_NAME, TO_ID_ATTRIBUTE_NAME, SRCID_ATTRIBUTE_NAME };

	private static final String[] FIXED_EDGE_ATTRIBUTE_LABELS = new String[] { COMMENT_ATTRIBUTE_LABEL,
			DIRECTED_ATTRIBUTE_NAME, STRENGTH_ATTRIBUTE_NAME, INPATH_ATTRIBUTE_NAME, CONNECTION_TYPE_ATTRIBUTE_LABEL,
			FROM_ID_ATTRIBUTE_NAME, TO_ID_ATTRIBUTE_NAME, SRCID_ATTRIBUTE_NAME };

	private static final String[] FIXED_NODE_ATTRIBUTE_NAMES = new String[] { LAT_ATTRIBUTE_NAME, LON_ATTRIBUTE_NAME,
			ICONNAME_ATTRIBUTE_NAME, SRCID_ATTRIBUTE_NAME };

	private static String[] SYSTEM_NOTEDITABLE_ATTRIBUTE_NAMES = { FROM_ID_ATTRIBUTE_NAME, TO_ID_ATTRIBUTE_NAME,
			SRCID_ATTRIBUTE_NAME, ICONNAME_ATTRIBUTE_NAME, LAT_ATTRIBUTE_NAME, LON_ATTRIBUTE_NAME,
			FAVORITE_ID_ATTRIBUTE_NAME };

	private Integer id;
	private ObjectDefinition objectDefinition;
	private Integer sort;
	private String name;
	private String label;
	private String description;
	private Integer dataType_;

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
	private Integer multivalue;
	private Integer inContext_;

	private Date created;
	private User createdBy;
	private String inTable;
	private String exportLabel;
	private String format;
	private Integer labelSort;
	private Integer filterSort;
	private Integer searchSort;
	private Integer matrixSort;
	private Integer inMatrix;
	private Formula formula;

	private String minValue;
	private String maxValue;
	private String editFormat;
	private String formatValidCharacters;
	private String formatInvalidCharacters;

	private List<PredefinedAttribute> predefinedAttributes;
	private List<AttributeGroup> attributeGroups;
	private List<ContextAttribute> contextAttributes;
	private String dataSource;

	public ObjectAttribute(){
		setContentBold(Boolean.FALSE);
		setContentItalic(Boolean.FALSE);
		setContentUnderline(Boolean.FALSE);
		setInSimpleSearch(Boolean.FALSE);
		setInAdvancedSearch(Boolean.FALSE);
		setInExport(Boolean.FALSE);
		setInFilter(Boolean.FALSE);
		setInLabel(Boolean.FALSE);
		setInMetaphor(Boolean.FALSE);
		setInPrefilter(Boolean.FALSE);
		setInToolTip(Boolean.FALSE);
		setPredefined(Boolean.FALSE);
		setExportLabel(null);
		setFormat(null);
		setInTable(null);
		setSort(0);
		setLabelSort(0);
		setFilterSort(0);
		setSearchSort(0);
	}

	public ObjectAttribute(ObjectDefinition parent){
		setObjectDefinition(parent);
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer iD){
		id = iD;
	}

	public void setFormula(Formula formula){
		this.formula = formula;
	}

	public Formula getFormula(){
		return formula;
	}

	public ObjectDefinition getObjectDefinition(){
		return objectDefinition;
	}

	public void setObjectDefinition(ObjectDefinition objectDefinition){
		this.objectDefinition = objectDefinition;
	}

	public Integer getSort(){
		return sort;
	}

	public void setSort(Integer sort){
		this.sort = sort;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getLabel(){
		return label;
	}

	public void setLabel(String label){
		this.label = label;
	}

	public Boolean isPredefined(){
		return Formula.isPredefined(getPredefined_());
	}

	public Boolean isFormulaAttribute(){
		return Formula.isFormula(getPredefined_());
	}

	public void setPredefined(Boolean predefined){
		setPredefined_(predefined ? 1 : 0);
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public Integer getDataType_(){
		return dataType_;
	}

	public void setDataType_(Integer dataType){
		dataType_ = dataType;
	}

	public DataType getDataType(){
		return dataType_ != null ? DataType.fromInt(dataType_) : null;
	}

	public void setDataType(DataType dataType){
		this.dataType_ = (dataType != null ? dataType.toInt() : null);
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

	public Date getCreated(){
		return created;
	}

	public void setCreated(Date created){
		this.created = created;
	}

	public User getCreatedBy(){
		return createdBy;
	}

	public void setCreatedBy(User createdBy){
		this.createdBy = createdBy;
	}

	public String getInTable(){
		return inTable;
	}

	public void setInTable(String inTable){
		this.inTable = inTable;
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

	public String getExportLabel(){
		return exportLabel;
	}

	public void setExportLabel(String exportLabel){
		this.exportLabel = exportLabel;
	}

	public Boolean isInPrefilter(){
		return getInPrefilter_() != null && getInPrefilter_() == 1;
	}

	public void setInPrefilter(Boolean inPrefilter){
		setInPrefilter_(inPrefilter ? 1 : 0);
	}

	public Integer getFilterSort(){
		return filterSort;
	}

	public void setFilterSort(Integer filterSort){
		this.filterSort = filterSort;
	}

	public String getFormat(){
		return format;
	}

	public void setFormat(String format){
		this.format = format;
	}

	public Integer getLabelSort(){
		return labelSort;
	}

	public void setLabelSort(Integer labelSort){
		this.labelSort = labelSort;
	}

	public Integer getSearchSort(){
		return searchSort;
	}

	public void setSearchSort(Integer searchSort){
		this.searchSort = searchSort;
	}

	public Integer getPredefined_(){
		return predefined_;
	}

	public void setPredefined_(Integer predefined){
		predefined_ = predefined;
	}

	private Integer getInFilter_(){
		return inFilter_;
	}

	private void setInFilter_(Integer inFilter){
		inFilter_ = inFilter;
	}

	private Integer getInLabel_(){
		return inLabel_;
	}

	private void setInLabel_(Integer inLabel){
		inLabel_ = inLabel;
	}

	private Integer getInToolTip_(){
		return inToolTip_;
	}

	private void setInToolTip_(Integer inToolTip){
		inToolTip_ = inToolTip;
	}

	private Integer getInAdvancedSearch_(){
		return inAdvancedSearch_;
	}

	private void setInAdvancedSearch_(Integer inAdvancedSearch){
		inAdvancedSearch_ = inAdvancedSearch;
	}

	private Integer getInMetaphor_(){
		return inMetaphor_;
	}

	private void setInMetaphor_(Integer inMetaphor){
		inMetaphor_ = inMetaphor;
	}

	private Integer getLabelBold_(){
		return labelBold_;
	}

	private void setLabelBold_(Integer labelBold){
		labelBold_ = labelBold;
	}

	private Integer getLabelItalic_(){
		return labelItalic_;
	}

	private void setLabelItalic_(Integer labelItalic){
		labelItalic_ = labelItalic;
	}

	private Integer getLabelUnderline_(){
		return labelUnderline_;
	}

	private void setLabelUnderline_(Integer labelUnderline){
		labelUnderline_ = labelUnderline;
	}

	private Integer getContentBold_(){
		return contentBold_;
	}

	private void setContentBold_(Integer contentBold){
		contentBold_ = contentBold;
	}

	private Integer getContentItalic_(){
		return contentItalic_;
	}

	private void setContentItalic_(Integer contentItalic){
		contentItalic_ = contentItalic;
	}

	private Integer getContentUnderline_(){
		return contentUnderline_;
	}

	private void setContentUnderline_(Integer contentUnderline){
		contentUnderline_ = contentUnderline;
	}

	private Integer getInExport_(){
		return inExport_;
	}

	private void setInExport_(Integer inExport){
		inExport_ = inExport;
	}

	private Integer getInSimpleSearch_(){
		return inSimpleSearch_;
	}

	private void setInSimpleSearch_(Integer inSimpleSearch){
		inSimpleSearch_ = inSimpleSearch;
	}

	private Integer getInPrefilter_(){
		return inPrefilter_;
	}

	private void setInPrefilter_(Integer inPrefilter){
		inPrefilter_ = inPrefilter;
	}

	public Boolean getIsMultivalue(){
		return getMultivalue() != null && getMultivalue() == 1;
	}

	private Integer getMultivalue(){
		return multivalue;
	}

	private void setMultivalue(Integer multivalue){
		this.multivalue = multivalue;
	}

	public void setIsMultivalue(Boolean multivalue){
		setMultivalue(multivalue ? 1 : 0);
	}

	private Integer getInContext_(){
		return inContext_;
	}

	private void setInContext_(Integer inContext){
		this.inContext_ = inContext;
	}

	public void setInContext(Boolean inContext){
		setInContext_(inContext ? 1 : 0);
	}

	public Boolean isInContext(){
		return getInContext_() != null && getInContext_() == 1;
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

	public Integer getMatrixSort(){
		return matrixSort;
	}

	public void setMatrixSort(Integer matrixSort){
		this.matrixSort = matrixSort;
	}

	public Integer getInMatrix(){
		return inMatrix;
	}

	public void setInMatrix(Integer inMatrix){
		this.inMatrix = inMatrix;
	}

	public List<PredefinedAttribute> getPredefinedAttributes(){
		return predefinedAttributes;
	}

	public void setPredefinedAttributes(List<PredefinedAttribute> predefinedAttributes){
		this.predefinedAttributes = predefinedAttributes;
	}

	public List<AttributeGroup> getAttributeGroups(){
		return attributeGroups;
	}

	public void setAttributeGroups(List<AttributeGroup> attributeGroups){
		this.attributeGroups = attributeGroups;
	}

	public void setMinValue(String minValue){
		this.minValue = minValue;
	}

	public String getMinValue(){
		return minValue;
	}

	public void setMaxValue(String maxValue){
		this.maxValue = maxValue;
	}

	public String getMaxValue(){
		return maxValue;
	}

	public void setEditFormat(String editFormat){
		this.editFormat = editFormat;
	}

	public String getEditFormat(){
		return editFormat;
	}

	public String getFormatValidCharacters(){
		return formatValidCharacters;
	}

	public void setFormatValidCharacters(String formatValidCharacters){
		this.formatValidCharacters = formatValidCharacters;
	}

	public String getFormatInvalidCharacters(){
		return formatInvalidCharacters;
	}

	public List<ContextAttribute> getContextAttributes(){
		return contextAttributes;
	}

	public void setContextAttributes(List<ContextAttribute> contextAttributes){
		this.contextAttributes = contextAttributes;
	}

	public void setFormatInvalidCharacters(String formatInvalidCharacters){
		this.formatInvalidCharacters = formatInvalidCharacters;
	}

	public String getDataSource(){
		return dataSource;
	}

	public void setDataSource(String dataSource){
		this.dataSource = dataSource;
	}

	public static boolean isFixedEdgeAttribute(ObjectAttribute oa, boolean onlyCisAttributes){
		return isFixedAttribute(oa, getFixedEdgeAttributeNames(onlyCisAttributes));
	}

	public static boolean isFixedNodeAttribute(ObjectAttribute oa, boolean onlyCisAttributes){
		return isFixedAttribute(oa, getFixedNodeAttributeNames(onlyCisAttributes));
	}

	private static boolean isFixedAttribute(ObjectAttribute oa, String[] fixedNames){
		if (oa.getName() == null)
			return false;

		for (int i = 0; i < fixedNames.length; i++)
			if (oa.getName().equalsIgnoreCase(fixedNames[i]))
				return true;
		return false;
	}

	public static boolean isSystemNotEditableAttribute(ObjectAttribute oa){
		for (String s : SYSTEM_NOTEDITABLE_ATTRIBUTE_NAMES){
			if (s.equalsIgnoreCase(oa.getName()))
				return true;
		}
		return false;
	}

	public static String[] getFixedEdgeAttributeNames(boolean onlyCisAttributes){
		return getFixedAttributeNames(FIXED_EDGE_ATTRIBUTE_NAMES, onlyCisAttributes);
	}

	public static String[] getFixedEdgeAttributeLabels(boolean onlyCisAttributes){
		return getFixedAttributeNames(FIXED_EDGE_ATTRIBUTE_LABELS, onlyCisAttributes);
	}

	public static String[] getFixedNodeAttributeNames(boolean onlyCisAttributes){
		return getFixedAttributeNames(FIXED_NODE_ATTRIBUTE_NAMES, onlyCisAttributes);
	}

	public static String[] getFixedNodeAttributeLabels(boolean onlyCisAttributes){
		return getFixedNodeAttributeNames(onlyCisAttributes);
	}

	private static String[] getFixedAttributeNames(String[] names, boolean onlyCisAttributes){
		if (onlyCisAttributes){
			return Arrays.copyOf(names, names.length - 1); // without srcid
		} else{
			return names;
		}
	}

	public boolean isTextDataType(){
		return getDataType() == DataType.TEXT;
	}

	public boolean isIntDataType(){
		return getDataType() == DataType.INT;
	}

	public boolean isBoolDataType(){
		return getDataType() == DataType.BOOL;
	}

	public boolean isURLDataType(){
		return getDataType() == DataType.URL;
	}

	public boolean isDecimalDataType(){
		return getDataType() == DataType.DECIMAL;
	}

	public boolean isDateDataType(){
		return getDataType() == DataType.DATE;
	}

	public DataType getDatabaseDataType(){
		DataType dbDataType = DataType.TEXT;
		if (getIsMultivalue()){
			dbDataType = DataType.TEXT;
		} else if (isPredefined()){
			dbDataType = DataType.INT;
		} else if (isTextDataType() || isDateDataType() || isURLDataType()){
			dbDataType = DataType.TEXT;
		} else if (isIntDataType() || isBoolDataType()){
			dbDataType = DataType.INT;
		} else if (isDecimalDataType()){
			dbDataType = DataType.DECIMAL;
		}
		return dbDataType;
	}

	@Override
	public ObjectAttribute clone() throws CloneNotSupportedException{
		return (ObjectAttribute) super.clone();
	}

	public ObjectAttribute clone(Integer id, ObjectDefinition od, Formula formula,
			List<PredefinedAttribute> predefinedAttrs, List<AttributeGroup> attributeGroups,
			List<ContextAttribute> contextAttrs) throws CloneNotSupportedException{
		ObjectAttribute oa = clone();
		oa.setId(id);
		oa.setObjectDefinition(od);
		oa.setFormula(formula);
		oa.setPredefinedAttributes(predefinedAttrs);
		oa.setContextAttributes(contextAttrs);
		oa.setAttributeGroups(attributeGroups);
		return oa;
	}

	public int compareTo(ObjectAttribute o){
		if (this.getLabel() == null && (o == null || o.getLabel() == null)){
			return 0;
		} else if (this.getLabel() == null){
			return -1;
		} else if (o == null || o.getLabel() == null){
			return 1;
		}
		return getLabel().compareTo(o.getLabel());
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj){
			return true;
		}
		if (getId() == null || !(obj instanceof ObjectAttribute)){
			return false;
		}

		return getId().equals(((ObjectAttribute) obj).getId());
	}

	public void clearFormatFields(){
		setFormat(null);
		setMinValue(null);
		setMaxValue(null);
		setEditFormat(null);
		setFormatInvalidCharacters(null);
		setFormatValidCharacters(null);
	}

	@Override
	public String toString(){
		return label;
	}

}
