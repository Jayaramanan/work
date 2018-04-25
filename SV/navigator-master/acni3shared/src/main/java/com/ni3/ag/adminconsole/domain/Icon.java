/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class Icon implements Serializable, Comparable<Icon>{

	// constant for Criteria in DAO objects - please adjust accordingly is the field name is changed
	public static final String NAME_DB_COLUMN = "iconName";

	private Integer id;
	private String iconName;
	private byte[] icon;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getIconName(){
		return iconName;
	}

	public void setIconName(String iconName){
		this.iconName = iconName;
	}

	public byte[] getIcon(){
		return icon;
	}

	public void setIcon(byte[] icon){
		this.icon = icon;
	}

	public int compareTo(Icon o){
		if (this.getIconName() == null && (o == null || o.getIconName() == null)){
			return 0;
		} else if (this.getIconName() == null){
			return -1;
		} else if (o == null || o.getIconName() == null){
			return 1;
		}
		return getIconName().compareTo(o.getIconName());
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof Icon))
			return false;
		if (o == this)
			return true;
		Icon icon = (Icon) o;
		if (getId() == null || icon.getId() == null)
			return false;
		return getId().equals(icon.getId());
	}
}
