/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class ContextAttribute implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private ObjectAttribute attribute;
	private Context context;

	public ContextAttribute(){
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public ObjectAttribute getAttribute(){
		return attribute;
	}

	public void setAttribute(ObjectAttribute attribute){
		this.attribute = attribute;
	}

	public Context getContext(){
		return context;
	}

	public void setContext(Context context){
		this.context = context;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof ContextAttribute))
			return false;
		if (o == this)
			return true;
		ContextAttribute dt = (ContextAttribute) o;
		if (getId() == null || dt.getId() == null)
			return false;
		return getId().equals(dt.getId());
	}
}