/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.metaphoradmin;

import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.ni3.ag.adminconsole.domain.Icon;

public class IconListCellRenderer extends JLabel implements ListCellRenderer{

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		Icon icon = (Icon) value;
		ImageIcon imageIcon = null;
		if (icon != null){
			Image scaledImage = new ImageIcon(icon.getIcon()).getImage().getScaledInstance(32, 32, Image.SCALE_FAST);
			imageIcon = new ImageIcon(scaledImage);
		}

		setIcon(imageIcon);
		setText(icon != null ? icon.getIconName() : "");

		return this;
	}

}
