/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.domain.ChartType;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class ChartTypeCellRenderer extends DefaultTableCellRenderer{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		Object toShow = value;
		if (value instanceof ChartType)
			toShow = Translation.get(((ChartType) value).getTextId());
		return super.getTableCellRendererComponent(table, toShow, isSelected, hasFocus, row, column);
	}

}
