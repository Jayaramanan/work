/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

public class ColorTableCellRenderer extends JLabel implements TableCellRenderer{
	private static final long serialVersionUID = 1L;

	private Border selectedBorder;
	private Border unselectedBorder;

	public ColorTableCellRenderer(){
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		Color c = null;
		if (value instanceof Color){
			c = (Color) value;
		}

		setBackground(c);

		if (isSelected){
			if (selectedBorder == null){
				selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getSelectionBackground());
			}
			setBorder(selectedBorder);
		} else{
			if (unselectedBorder == null){
				unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getBackground());
			}
			setBorder(unselectedBorder);
		}

		setToolTipText(c != null ? (c.getRed() + ", " + c.getGreen() + ", " + c.getBlue()) : null);

		return this;
	}
}
