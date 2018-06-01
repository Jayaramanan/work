/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class MetaphorData implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Metaphor metaphor;
	private ObjectAttribute attribute;
	private PredefinedAttribute data;

	MetaphorData(){
	}

	public MetaphorData(Metaphor metaphor, ObjectAttribute attribute, PredefinedAttribute data){
		setMetaphor(metaphor);
		setAttribute(attribute);
		setData(data);
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public Metaphor getMetaphor(){
		return metaphor;
	}

	public void setMetaphor(Metaphor metaphor){
		this.metaphor = metaphor;
	}

	public ObjectAttribute getAttribute(){
		return attribute;
	}

	public void setAttribute(ObjectAttribute attribute){
		this.attribute = attribute;
	}

	public PredefinedAttribute getData(){
		return data;
	}

	public void setData(PredefinedAttribute data){
		this.data = data;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof MetaphorData))
			return false;
		if (o == this)
			return true;
		MetaphorData dt = (MetaphorData) o;
		if (getId() == null || dt.getId() == null)
			return false;
		return getId().equals(dt.getId());
	}

}