/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;

import org.apache.log4j.Logger;

public class ColorTableCellRenderer extends JLabel implements StrongTableCellRenderer{
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ColorTableCellRenderer.class);

	private Border selectedBorder;
	private Border unselectedBorder;
	private boolean storeAsHex = true;
	private RgbColorConverter converter = null;

	public ColorTableCellRenderer(){
		this(true);
	}

	public ColorTableCellRenderer(boolean storeAsHex){
		this.storeAsHex = storeAsHex;
		if (!storeAsHex){
			converter = new RgbColorConverter();
		}
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){
		String colorStr = (String) value;
		java.awt.Color c = null;
		if (colorStr != null){
			try{
				if (storeAsHex){
					c = java.awt.Color.decode(colorStr);
				} else{
					c = converter.getColor(colorStr);
				}
			} catch (NumberFormatException ex){
				log.warn("Not valid hex color: " + colorStr);
			}
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
