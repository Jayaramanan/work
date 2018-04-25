/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.connection;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.ni3.ag.adminconsole.domain.LineWeight;

public class LineWeightListCellRenderer extends DefaultListCellRenderer{

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		String weight = value != null ? ((LineWeight) value).getLabel() : null;
		return super.getListCellRendererComponent(list, weight, index, isSelected, cellHasFocus);
	}
}