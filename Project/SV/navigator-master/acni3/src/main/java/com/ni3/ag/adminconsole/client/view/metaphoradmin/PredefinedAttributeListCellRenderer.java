/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.metaphoradmin;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

public class PredefinedAttributeListCellRenderer extends DefaultListCellRenderer{

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){

		String label = (value != null && value instanceof PredefinedAttribute) ? ((PredefinedAttribute) value).getLabel()
		        : " ";

		return super.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
	}
}
