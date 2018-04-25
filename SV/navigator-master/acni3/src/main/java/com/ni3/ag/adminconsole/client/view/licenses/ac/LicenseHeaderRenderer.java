/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses.ac;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import com.ni3.ag.adminconsole.license.ACModuleDescription;

class LicenseHeaderRenderer extends DefaultTableCellRenderer{
	private static final long serialVersionUID = -8486779118161376932L;

	public LicenseHeaderRenderer(){
		setHorizontalAlignment(SwingConstants.CENTER);
		setOpaque(true);
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	}

	public void updateUI(){
		super.updateUI();
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row,
	        int column){
		JTableHeader h = table != null ? table.getTableHeader() : null;

		if (h != null){
			setEnabled(h.isEnabled());
			setComponentOrientation(h.getComponentOrientation());

			setForeground(h.getForeground());
			setBackground(h.getBackground());
			setFont(h.getFont());
		} else{
			setEnabled(true);
			setComponentOrientation(ComponentOrientation.UNKNOWN);

			setForeground(UIManager.getColor("TableHeader.foreground"));
			setBackground(UIManager.getColor("TableHeader.background"));
			setFont(UIManager.getFont("TableHeader.font"));
		}

		UserACEditionTableModel model = (UserACEditionTableModel) table.getModel();
		ACModuleDescription md = model.getModuleDescription(table.convertColumnIndexToModel(column));
		if (md != null){
			if (md.getUserCount() < md.getUsedUserCount()){
				setForeground(Color.red);
			} else{
				setForeground(Color.black);
			}
			setValue(md.getFullColumnName());
		} else{
			setValue(value);
		}

		return this;
	}
}
