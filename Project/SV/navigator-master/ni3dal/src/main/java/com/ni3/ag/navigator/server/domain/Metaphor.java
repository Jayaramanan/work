/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.domain;

import java.io.Serializable;
import java.util.List;

public class Metaphor implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer schemaId;
	private Integer objectDefinitionId;
	private Integer priority;
	private String metaphorSet;
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

	public String getIconName(){
		return iconName;
	}

	public void setIconName(String iconName){
		this.iconName = iconName;
	}

	public Integer getSchemaId(){
		return schemaId;
	}

	public void setSchemaId(Integer schemaId){
		this.schemaId = schemaId;
	}

	public Integer getObjectDefinitionId(){
		return objectDefinitionId;
	}

	public void setObjectDefinitionId(Integer objectDefinitionId){
		this.objectDefinitionId = objectDefinitionId;
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
}
