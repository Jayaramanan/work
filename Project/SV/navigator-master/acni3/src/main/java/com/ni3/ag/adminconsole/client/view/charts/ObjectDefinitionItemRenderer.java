/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;

class ObjectDefinitionItemRenderer extends DefaultListCellRenderer{

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		String name = value != null ? ((ObjectDefinition) value).getName() : null;
		return super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
	}

}
