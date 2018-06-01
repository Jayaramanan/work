/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.vers;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.ni3.ag.adminconsole.domain.ModuleUser;

public class UserModuleCellRenderer extends DefaultTableCellRenderer{
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		String valToRender = "";
		if (value == null)
			valToRender = "-";
		else{
			ModuleUser mu = (ModuleUser) value;
			valToRender = mu.toString();
		}
		return super.getTableCellRendererComponent(table, valToRender, isSelected, hasFocus, row, column);
	}

}
