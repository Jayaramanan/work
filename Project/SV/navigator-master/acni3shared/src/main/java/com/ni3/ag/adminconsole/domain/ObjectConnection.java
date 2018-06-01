/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class ObjectConnection implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	public static final String CONNECTION_TYPE = "connectionType";
	public static final String OBJECT_ID = "object.id";
	public static final String OBJECT = "object";
	public static final String FROM_OBJECT = "fromObject";
	public static final String TO_OBJECT = "toObject";

	private Integer id;
	private ObjectDefinition fromObject;
	private ObjectDefinition toObject;
	private PredefinedAttribute connectionType;
	private Integer lineStyle_;
	private LineWeight lineWeight;
	private String rgb;
	private ObjectDefinition object;

	private transient boolean isHierarchical = false;

	public ObjectConnection(){

	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public ObjectDefinition getFromObject(){
		return fromObject;
	}

	public void setFromObject(ObjectDefinition fromObject){
		this.fromObject = fromObject;
	}

	public ObjectDefinition getToObject(){
		return toObject;
	}

	public void setToObject(ObjectDefinition toObject){
		this.toObject = toObject;
	}

	public PredefinedAttribute getConnectionType(){
		return connectionType;
	}

	public void setConnectionType(PredefinedAttribute connectionType){
		this.connectionType = connectionType;
	}

	public int getLineStyle_(){
		return lineStyle_;
	}

	public void setLineStyle_(int lineStyle){
		lineStyle_ = lineStyle;
	}

	public LineStyle getLineStyle(){
		return lineStyle_ != null ? LineStyle.fromInt(lineStyle_) : null;
	}

	public void setLineStyle(LineStyle lineStyle){
		this.lineStyle_ = (lineStyle != null ? lineStyle.toInt() : null);
	}

	public LineWeight getLineWeight(){
		return lineWeight;
	}

	public void setLineWeight(LineWeight lineWeight){
		this.lineWeight = lineWeight;
	}

	public String getRgb(){
		return rgb;
	}

	public void setRgb(String rgb){
		this.rgb = rgb;
	}

	public ObjectDefinition getObject(){
		return object;
	}

	public void setObject(ObjectDefinition object){
		this.object = object;
	}

	public boolean isHierarchical(){
		return isHierarchical;
	}

	public void setHierarchical(boolean isHierarchical){
		this.isHierarchical = isHierarchical;
	}

	@Override
	public boolean equals(java.lang.Object obj){
		if (this == obj){
			return true;
		}
		if (obj == null || !(obj instanceof ObjectConnection)){
			return false;
		}
		if (getId() == null || ((ObjectConnection) obj).getId() == null){
			return false;
		}
		return getId().intValue() == ((ObjectConnection) obj).getId().intValue();

	}

	public ObjectConnection clone() throws CloneNotSupportedException{
		return (ObjectConnection) super.clone();
	}

	public ObjectConnection clone(Integer id, PredefinedAttribute connType, ObjectDefinition from, ObjectDefinition to,
	        ObjectDefinition object) throws CloneNotSupportedException{
		ObjectConnection oc = clone();
		oc.setId(id);
		oc.setConnectionType(connType);
		oc.setObject(object);
		oc.setFromObject(from);
		oc.setToObject(to);
		return oc;
	}
}
