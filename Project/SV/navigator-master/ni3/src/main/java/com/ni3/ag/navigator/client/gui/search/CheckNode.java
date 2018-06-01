/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.search;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ni3.ag.navigator.client.domain.Value;

@SuppressWarnings({ "unchecked", "serial" })
public class CheckNode extends DefaultMutableTreeNode{
	public Value value = null;

	public boolean ConnectionType;

	private boolean isSelected;
	private boolean hasBox;

	private boolean isEnabled;

	public int XOffsetOfHaloCheck;

	public CheckNode(String name, Value val, boolean ConnectionType, boolean isSelected){
		super(name, true);

		XOffsetOfHaloCheck = 0;
		this.isSelected = isSelected;
		this.isEnabled = true;

		if (val != null){
			this.value = val;
			this.hasBox = true;
		} else
			this.hasBox = false;

		this.ConnectionType = ConnectionType;
	}

	public void setSelected(boolean isSelected){
		this.isSelected = isSelected;
	}

	public boolean isSelected(){
		return isSelected;
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
}
