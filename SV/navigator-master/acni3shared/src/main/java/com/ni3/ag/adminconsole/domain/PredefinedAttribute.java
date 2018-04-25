/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.List;

public class PredefinedAttribute implements Serializable, Comparable<PredefinedAttribute>, Cloneable{
	private static final long serialVersionUID = 1L;

	public static final String OBJECT_ATTRIBUTE_ID = "objectAttribute.id";

	public static final String VALUE = "value";
	public static final String LABEL = "label";

	private Integer id;
	private ObjectAttribute objectAttribute;
	private String value;
	private String label;
	private Integer toUse_;
	private Integer sort;
	private PredefinedAttribute parent;
	private String srcID;
	private String haloColor;
	private List<GroupPrefilter> predefAttributeGroups;
	private List<PredefinedAttribute> children;

	private transient String translation;
	/** Level of this predefined attribute in it's parent hierarchy; 0 defines it is a top-parent */
	private transient Integer level;
	/** indicates that halo should be reset to empty on update */
	private transient boolean isNested;
	/** predefined to which this predefined is nested */
	private transient PredefinedAttribute nestedTo;

	public PredefinedAttribute(){
		this.sort = 0;
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public ObjectAttribute getObjectAttribute(){
		return objectAttribute;
	}

	public void setObjectAttribute(ObjectAttribute objectAttribute){
		this.objectAttribute = objectAttribute;
	}

	public String getValue(){
		return value;
	}

	public void setValue(String value){
		this.value = value;
	}

	public String getLabel(){
		return label;
	}

	public void setLabel(String label){
		this.label = label;
	}

	public Boolean getToUse(){
		return getToUse_() != null && getToUse_() == 1;
	}

	public void setToUse(Boolean toUse){
		setToUse_(toUse ? 1 : 0);
	}

	private Integer getToUse_(){
		return toUse_;
	}

	private void setToUse_(Integer toUse){
		toUse_ = toUse;
	}

	public Integer getSort(){
		return sort;
	}

	public void setSort(Integer sort){
		this.sort = sort;
	}

	public PredefinedAttribute getParent(){
		return parent;
	}

	public void setParent(PredefinedAttribute parent){
		this.parent = parent;
	}

	public String getSrcID(){
		return srcID;
	}

	public void setSrcID(String srcID){
		this.srcID = srcID;
	}

	public List<GroupPrefilter> getPredefAttributeGroups(){
		return predefAttributeGroups;
	}

	public void setPredefAttributeGroups(List<GroupPrefilter> predefAttributeGroups){
		this.predefAttributeGroups = predefAttributeGroups;
	}

	public String getTranslation(){
		return translation;
	}

	public void setTranslation(String translation){
		this.translation = translation;
	}

	public String getHaloColor(){
		return haloColor;
	}

	public void setHaloColor(String haloColor){
		this.haloColor = haloColor;
	}

	public void setLevel(Integer level){
		this.level = level;
	}

	public Integer getLevel(){
		return level;
	}

	public void setChildren(List<PredefinedAttribute> children){
		this.children = children;
	}

	public List<PredefinedAttribute> getChildren(){
		return children;
	}

	public int compareTo(PredefinedAttribute o){
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
	public boolean equals(java.lang.Object obj){
		if (this == obj){
			return true;
		}
		if (obj == null || !(obj instanceof PredefinedAttribute)){
			return false;
		}
		if (getId() == null || ((PredefinedAttribute) obj).getId() == null){
			return false;
		}
		return getId().equals(((PredefinedAttribute) obj).getId());
	}

	@Override
	public PredefinedAttribute clone() throws CloneNotSupportedException{
		return (PredefinedAttribute) super.clone();
	}

	public PredefinedAttribute clone(Integer id, ObjectAttribute oa, List<GroupPrefilter> predefAttributeGroups)
	        throws CloneNotSupportedException{
		PredefinedAttribute pa = clone();
		pa.setId(id);
		pa.setObjectAttribute(oa);
		pa.setPredefAttributeGroups(predefAttributeGroups);
		return pa;
	}

	public void setNested(boolean isNested){
		this.isNested = isNested;
	}

	public boolean isNested(){
		return isNested;
	}

	public void setNestedTo(PredefinedAttribute nestedTo){
		this.nestedTo = nestedTo;
	}

	public PredefinedAttribute getNestedTo(){
		return nestedTo;
	}

	@Override
	public String toString(){
		return label;
	}
}
