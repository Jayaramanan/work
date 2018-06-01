/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class ApplicationSetting implements Serializable, Setting{
	private static final long serialVersionUID = -5268429062985355420L;

	private String section;
	private String prop;
	private String value;
	private transient boolean isNew;

	public ApplicationSetting(){

	}

	public ApplicationSetting(String section, String prop, String value){
		this.section = section;
		this.prop = prop;
		this.value = value;
	}

	public void setNew(boolean isNew){
		this.isNew = isNew;
	}

	public boolean isNew(){
		return isNew;
	}

	public String getSection(){
		return section;
	}

	public void setSection(String section){
		this.section = section;
	}

	public String getProp(){
		return prop;
	}

	public void setProp(String prop){
		this.prop = prop;
	}

	public String getValue(){
		return value;
	}

	public void setValue(String value){
		this.value = value;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof ApplicationSetting))
			return false;
		ApplicationSetting src = (ApplicationSetting) o;
		if (src.getSection() == null && getSection() != null)
			return false;
		if (src.getSection() != null && getSection() == null)
			return false;
		if (getSection() != null && !getSection().equals(src.getSection()))
			return false;
		if (src.getProp() == null && getProp() != null)
			return false;
		if (src.getProp() != null && getProp() == null)
			return false;
		if (getProp() != null && !getProp().equals(src.getProp()))
			return false;
		if (getValue() == null && src.getValue() != null)
			return false;
		if (getValue() != null && src.getValue() == null)
			return false;
		if (getValue() != null && !getValue().equals(src.getValue()))
			return false;
		return true;
	}

	public static boolean isMandatory(Setting set){
		for (int i = 0; i < mandatorySettings.length; i++)
			if (mandatorySettings[i].equals(set.getProp()))
				return true;
		return false;
	}

}
