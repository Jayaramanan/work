/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.List;

public class Metaphor implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	public static final String ICON = "icon";
	private Integer id;
	private Schema schema;
	private ObjectDefinition objectDefinition;
	private Integer priority;
	private String metaphorSet;
	private Icon icon;
	private String iconName;
	private String description;
	private List<MetaphorData> metaphorData;

	public Metaphor(){
		this.metaphorSet = "Default";
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public Integer getPriority(){
		return priority;
	}

	public void setPriority(Integer priority){
		this.priority = priority;
	}

	public String getMetaphorSet(){
		return metaphorSet;
	}

	public void setMetaphorSet(String metaphorSet){
		this.metaphorSet = metaphorSet;
	}

	public Icon getIcon(){
		return icon;
	}

	public void setIcon(Icon icon){
		this.icon = icon;
	}

	public String getIconName(){
		return iconName;
	}

	public void setIconName(String iconName){
		this.iconName = iconName;
	}

	public Schema getSchema(){
		return schema;
	}

	public void setSchema(Schema schema){
		this.schema = schema;
	}

	public ObjectDefinition getObjectDefinition(){
		return objectDefinition;
	}

	public void setObjectDefinition(ObjectDefinition objectDefinition){
		this.objectDefinition = objectDefinition;
	}

	public List<MetaphorData> getMetaphorData(){
		return metaphorData;
	}

	public void setMetaphorData(List<MetaphorData> metaphorData){
		this.metaphorData = metaphorData;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof Metaphor))
			return false;
		if (o == this)
			return true;
		Metaphor dt = (Metaphor) o;
		if (getId() == null || dt.getId() == null)
			return false;
		return getId().equals(dt.getId());
	}

	public Metaphor clone() throws CloneNotSupportedException{
		return (Metaphor) super.clone();
	}

	public Metaphor clone(Integer id, Schema schema, ObjectDefinition od, List<MetaphorData> metaphorData)
	        throws CloneNotSupportedException{
		Metaphor m = clone();
		m.setId(id);
		m.setSchema(schema);
		m.setObjectDefinition(od);
		m.setMetaphorData(metaphorData);
		return m;
	}

}
