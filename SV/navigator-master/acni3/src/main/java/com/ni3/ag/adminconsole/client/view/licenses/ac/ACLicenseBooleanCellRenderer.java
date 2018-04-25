/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses.ac;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.client.view.licenses.LicenseBooleanCellRenderer;
import com.ni3.ag.adminconsole.domain.User;

public class ACLicenseBooleanCellRenderer extends LicenseBooleanCellRenderer{
	private static final long serialVersionUID = -3637817359360845249L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		if (row < table.getRowCount() && column < table.getColumnCount() && table.getModel() instanceof ACTableModel){
			int zrow = table.convertRowIndexToModel(row);
			int zcolumn = table.convertColumnIndexToModel(column);
			boolean b = table.getModel().isCellEditable(zrow, zcolumn);
			checkBox.setEnabled(b);

			UserACEditionTableModel tModel = (UserACEditionTableModel) table.getModel();
			if (b){
				if (tModel.isCellMarkedForExpiry(zrow, zcolumn))
					checkBox.setBackground(Color.yellow);
				else{
					checkBox.setBackground(table.getBackground());
				}
			}
			User user = tModel.getSelected(zrow);
			checkBox.setSelected(tModel.hasAccess(user, zcolumn));

			table.getTableHeader().repaint();
		}
		return this;
	}
}
