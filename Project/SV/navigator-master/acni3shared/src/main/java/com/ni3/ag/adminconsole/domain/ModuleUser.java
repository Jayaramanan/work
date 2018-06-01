/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class ModuleUser implements Serializable, Comparable<ModuleUser>{
	private static final long serialVersionUID = -7262976178196307135L;

	private Integer id;
	private User user;
	private Module current;
	private Module target;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public User getUser(){
		return user;
	}

	public void setUser(User user){
		this.user = user;
	}

	public Module getCurrent(){
		return current;
	}

	public void setCurrent(Module current){
		this.current = current;
	}

	public Module getTarget(){
		return target;
	}

	public void setTarget(Module target){
		this.target = target;
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj){
			return true;
		}
		if (obj == null || !(obj instanceof ModuleUser))
			return false;
		ModuleUser mu = (ModuleUser) obj;
		if (mu.getId() == null)
			return false;
		return mu.getId().equals(getId());
	}

	public String toString(){
		String valToRender = "";

		if (current != null)
			valToRender = current.getVersion();
		if ((current == null && target != null)
		        || (current != null && target != null && !target.getVersion().equals(current.getVersion())))
			valToRender += " -> " + target.getVersion();
		return valToRender;
	}

	@Override
	public int compareTo(ModuleUser o){
		return toString().compareTo(o.toString());
	}

}
