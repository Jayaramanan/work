/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.List;

public class Language implements Serializable, Comparable<Language>{
	private static final long serialVersionUID = 1L;
	public static final Integer DEFAULT_ID = 1;

	// constant criteria for db columns - please adjust if necessary
	public static final String NAME = "language";

	private Integer id;
	private String language;
	private List<UserLanguageProperty> properties;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getLanguage(){
		return language;
	}

	public void setLanguage(String language){
		this.language = language;
	}

	public List<UserLanguageProperty> getProperties(){
		return properties;
	}

	public void setProperties(List<UserLanguageProperty> properties){
		this.properties = properties;
	}

	public int compareTo(Language o){
		if (this.getLanguage() == null && (o == null || o.getLanguage() == null)){
			return 0;
		} else if (this.getLanguage() == null){
			return -1;
		} else if (o == null || o.getLanguage() == null){
			return 1;
		}
		return getLanguage().compareTo(o.getLanguage());
	}

	@Override
	public boolean equals(java.lang.Object obj){
		if (this == obj){
			return true;
		}
		if (obj == null || !(obj instanceof Language)){
			return false;
		}
		if (getId() == null || ((Language) obj).getId() == null){
			return false;
		}
		return getId().intValue() == ((Language) obj).getId().intValue();

	}

	@Override
	public String toString(){
		return getLanguage();
	}

}
