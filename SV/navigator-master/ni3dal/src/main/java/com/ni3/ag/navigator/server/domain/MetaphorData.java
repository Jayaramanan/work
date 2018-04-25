/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.domain;

import java.io.Serializable;

public class MetaphorData implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Metaphor metaphor;
	private Integer attributeId;
	private Integer data;

	public MetaphorData(){
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

	public Integer getAttributeId(){
		return attributeId;
	}

	public void setAttributeId(Integer attributeId){
		this.attributeId = attributeId;
	}

	public Integer getData(){
		return data;
	}

	public void setData(Integer data){
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