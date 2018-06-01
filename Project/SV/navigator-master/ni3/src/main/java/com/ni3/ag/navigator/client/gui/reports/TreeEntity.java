/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.reports;

import com.ni3.ag.navigator.client.domain.Entity;

public class TreeEntity{
	private Entity entity;
	private boolean isSelected = true;

	public TreeEntity(Entity entity){
		this.entity = entity;
	}

	public boolean isSelected(){
		return isSelected;
	}

	public void setSelected(boolean isSelected){
		this.isSelected = isSelected;
	}

	public Entity getEntity(){
		return entity;
	}

	@Override
	public boolean equals(java.lang.Object obj){
		if (this == obj){
			return true;
		}
		if (obj == null || !(obj instanceof TreeEntity || entity == null || ((TreeEntity) obj).getEntity() == null)){
			return false;
		}
		return entity.ID == ((TreeEntity) obj).getEntity().ID;
	}

	@Override
	public String toString(){
		return entity.Name;
	}
}
