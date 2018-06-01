/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common.calendar;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.ni3.ag.adminconsole.client.view.common.calendar.ACCalendarDialog.DisplayType;

public class ACDateRenderer extends JFormattedTextField implements TableCellRenderer{

	private static final long serialVersionUID = 1L;

	public ACDateRenderer(DisplayType displayType){
		super(new SimpleDateFormat(DisplayType.Date.equals(displayType) ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm"));
		setBorder(BorderFactory.createEmptyBorder());
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		Date val = (value instanceof Date) ? (Date) value : null;
		setValue(val);
		return this;
	}

}
