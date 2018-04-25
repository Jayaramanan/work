/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.domain.DataType;

public class DataTypeListCellRenderer extends DefaultListCellRenderer{

	private static final long serialVersionUID = 3987474132861259139L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		Object dataType = value;
		if (value instanceof DataType)
			dataType = Translation.get(((DataType) value).getTextId());
		return super.getListCellRendererComponent(list, dataType, index, isSelected, cellHasFocus);
	}
}
