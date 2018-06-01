/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.filter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.Value;
import com.ni3.ag.navigator.client.domain.cache.IconCache;

@SuppressWarnings({"serial"})
public class CheckNode extends DefaultMutableTreeNode{
	private Value value;
	private boolean isSelected;
	private boolean hasBox;
	private boolean isEnabled;
	private int xOffsetOfHaloCheck;
	private Icon icon;
	private Entity entity;

	public CheckNode(String name, Value val, boolean isSelected, Entity entity){
		super(name, true);

		if (entity != null && UserSettings.getBooleanAppletProperty("DataFilterTreeIcons", true)){
			if (entity.isEdge()) {
				this.icon = IconCache.getImageIcon(IconCache.FILTER_EDGE);
			} else {
				this.icon = IconCache.getImageIcon(IconCache.FILTER_NODE);
			}
		}

		this.isSelected = isSelected;
		this.isEnabled = true;

		if (val != null){
			this.value = val;
			this.hasBox = true;
		} else
			this.hasBox = false;
		this.entity = entity;
	}

	public void setSelected(boolean isSelected){
		this.isSelected = isSelected;
	}

	public boolean isSelected(){
		return isSelected;
	}

	public void setHaloSelected(boolean isSelected){
		if (value != null)
			value.setHaloColorSelected(isSelected);
	}

	public boolean isHaloSelected(){
		return value != null && value.isHaloColorSelected();
	}

	public boolean isEnabled(){
		return isEnabled;
	}

	public void setEnabled(boolean val){
		isEnabled = val;
	}

	public boolean hasCheckbox(){
		return hasBox;
	}

	public Value getPredefinedValue(){
		return value;
	}

	public Icon getIcon(){
		return icon;
	}

	public int getXOffsetOfHaloCheck(){
		return xOffsetOfHaloCheck;
	}

	public void setXOffsetOfHaloCheck(int xOffsetOfHaloCheck){
		this.xOffsetOfHaloCheck = xOffsetOfHaloCheck;
	}

	public Entity getEntity(){
		return entity;
	}
}
