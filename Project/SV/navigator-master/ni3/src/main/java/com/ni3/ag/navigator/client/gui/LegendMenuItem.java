/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui;

import javax.swing.JCheckBoxMenuItem;

import com.ni3.ag.navigator.client.domain.Entity;

public class LegendMenuItem extends JCheckBoxMenuItem{
	private static final long serialVersionUID = 7937593373524753249L;
	private Entity entity;

	public LegendMenuItem(Entity entity, String text){
		super(text);
		this.entity = entity;
	}

	public Entity getEntity(){
		return entity;
	}

	public void setEntity(Entity entity){
		this.entity = entity;
	}

}
