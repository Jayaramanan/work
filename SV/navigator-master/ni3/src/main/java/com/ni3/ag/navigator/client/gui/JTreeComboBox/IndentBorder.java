/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.jtreecombobox;

import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
class IndentBorder extends EmptyBorder{
	int indent = UIManager.getInt("Tree.leftChildIndent");

	public IndentBorder(){
		super(0, 0, 0, 0);
	}

	public void setDepth(int depth){
		left = indent * depth;
	}
}
