/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class InMatrixListCellRenderer extends DefaultListCellRenderer{

	private static final long serialVersionUID = -7147299338762675191L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	}

}
