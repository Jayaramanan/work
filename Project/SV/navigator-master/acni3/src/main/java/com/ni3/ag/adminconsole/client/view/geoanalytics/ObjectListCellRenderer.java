/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.geoanalytics;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public class ObjectListCellRenderer extends DefaultListCellRenderer{

	private static final long serialVersionUID = -6377274426593581323L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		String userName = value != null ? ((ObjectDefinition) value).getName() : null;
		return super.getListCellRendererComponent(list, userName, index, isSelected, cellHasFocus);
	}
}
