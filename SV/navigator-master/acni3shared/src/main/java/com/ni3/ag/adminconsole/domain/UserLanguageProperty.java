/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class UserLanguageProperty implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final String PROPERTY = "property";
	public static final String LANGUAGE = "language";
	private Language language;
	private String property;
	private String value;

	private transient boolean isNew = false;

	@SuppressWarnings("unused")
	private UserLanguageProperty(){
	}

	public UserLanguageProperty(Language language){
		setLanguage(language);
		setNew(true);
	}

	public Language getLanguage(){
		return language;
	}

	public void setLanguage(Language language){
		this.language = language;
	}

	public String getProperty(){
		return property;
	}

	public void setProperty(String property){
		this.property = property;
	}

	public String getValue(){
		return value;
	}

	public void setValue(String value){
		this.value = value;
	}

	public boolean isNew(){
		return isNew;
	}

	public void setNew(boolean isNew){
		this.isNew = isNew;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof UserLanguageProperty))
			return false;
		if (o == this)
			return true;
		UserLanguageProperty ulp = (UserLanguageProperty) o;
		if (getLanguage() == null || ulp.getLanguage() == null)
			return false;
		if (getProperty() == null || ulp.getProperty() == null)
			return false;
		return getLanguage().equals(ulp.getLanguage()) && getProperty().equals(ulp.getProperty());
	}

}
