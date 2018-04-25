/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.domain;

import java.awt.Image;

public class MetaphorIcon{

	private String iconName;
	private Integer priority;
	private Image icon;

	public MetaphorIcon(String iconName, Integer priority){
		this.iconName = iconName;
		this.priority = priority;
	}

	public String getIconName(){
		return iconName;
	}

	public Integer getPriority(){
		return priority;
	}

	public Image getIcon(){
		return icon;
	}

	public void setIcon(Image icon){
		this.icon = icon;
	}

	public void setIconName(String iconName){
		this.iconName = iconName;
	}

	public void setPriority(Integer priority){
		this.priority = priority;
	}
}
