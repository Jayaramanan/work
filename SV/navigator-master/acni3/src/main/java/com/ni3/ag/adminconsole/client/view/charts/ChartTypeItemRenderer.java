/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.domain.ChartType;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

class ChartTypeItemRenderer extends DefaultListCellRenderer{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		String val = "" + value;
		if (value instanceof ChartType)
			val = Translation.get(((ChartType) value).getTextId());
		return super.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);
	}

}
