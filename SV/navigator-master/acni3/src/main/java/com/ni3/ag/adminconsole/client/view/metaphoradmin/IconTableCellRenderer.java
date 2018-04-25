/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.metaphoradmin;

import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import com.ni3.ag.adminconsole.client.view.common.StrongTableCellRenderer;
import com.ni3.ag.adminconsole.domain.Icon;

public class IconTableCellRenderer extends JLabel implements StrongTableCellRenderer{

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column){

		Icon icon = (Icon) value;
		ImageIcon imageIcon = null;
		if (icon != null){
			Image scaledImage = new ImageIcon(icon.getIcon()).getImage().getScaledInstance(16, 16, Image.SCALE_FAST);
			imageIcon = new ImageIcon(scaledImage);
		}
		setIcon(imageIcon);
		setText(icon != null ? icon.getIconName() : "");

		return this;
	}
}