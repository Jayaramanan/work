/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class ColorButton extends JButton{
	private static final long serialVersionUID = 2658147975706405135L;
	private RgbColorConverter converter;

	public ColorButton(boolean enabled){
		converter = new RgbColorConverter();
		setBorderPainted(false);
		setContentAreaFilled(false);
		setOpaque(true);
		if (enabled){
			addActionListener(new ColorButtonListener(this));
		}
	}

	public void setColor(String colorStr){
		setText(colorStr);
		Color color = converter.getColor(colorStr);
		super.setBackground(color);
		super.setForeground(getBackground());
		setToolTipText(colorStr);
	}

	private class ColorButtonListener implements ActionListener{
		private ColorChooser chooser = null;

		public ColorButtonListener(Component owner){
			chooser = new ColorChooser(owner);
		}

		@Override
		public void actionPerformed(ActionEvent e){
			Color color = converter.getColor(getText());
			Color newColor = chooser.chooseColor(color);
			String newColorStr = converter.getColorString(newColor);
			setColor(newColorStr);
		}
	}

	@Override
	public void setBackground(Color bg){
		// do not set background if called outside setColor() method
	}

	@Override
	public void setForeground(Color fg){
		// do not set foreground if called outside setColor() method
	}
}
