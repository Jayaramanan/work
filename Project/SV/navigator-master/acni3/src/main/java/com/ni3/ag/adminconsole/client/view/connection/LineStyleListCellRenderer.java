/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.connection;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.domain.LineStyle;

public class LineStyleListCellRenderer extends DefaultListCellRenderer{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		Object styleName = value;
		if (value instanceof LineStyle)
			styleName = Translation.get(((LineStyle) value).getTextId());
		return super.getListCellRendererComponent(list, styleName, index, isSelected, cellHasFocus);
	}
}