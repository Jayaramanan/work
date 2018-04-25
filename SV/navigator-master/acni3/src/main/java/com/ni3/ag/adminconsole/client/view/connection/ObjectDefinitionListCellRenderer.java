/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.connection;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public class ObjectDefinitionListCellRenderer extends DefaultListCellRenderer{

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		String objectDefinitionName = value != null ? ((ObjectDefinition) value).getName() : null;
		return super.getListCellRendererComponent(list, objectDefinitionName, index, isSelected, cellHasFocus);
	}
}