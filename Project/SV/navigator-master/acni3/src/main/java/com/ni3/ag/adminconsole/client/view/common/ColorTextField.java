/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class ColorTextField extends ACTextField{
	private static final long serialVersionUID = -4409758134566291266L;
	private boolean enabled = false;
	private RgbColorConverter converter;

	public ColorTextField(){
		super();
		setEditable(false);
		converter = new RgbColorConverter();
		addMouseListener(new ColorMouseListener(this));
	}

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}

	public void setColor(String colorStr, boolean enabled){
		setText(colorStr);
		Color color = converter.getColor(colorStr);
		setBackground(color);
		setForeground(getBackground());
		setToolTipText(colorStr);
		setEnabled(enabled);
	}

	class ColorMouseListener implements MouseListener{

		private ColorChooser chooser = null;

		public ColorMouseListener(Component owner){
			chooser = new ColorChooser(owner);
		}

		@Override
		public void mousePressed(MouseEvent e){
			if (!enabled){
				return;
			}
			Color color = converter.getColor(getText());
			Color newColor = chooser.chooseColor(color);
			String newColorStr = converter.getColorString(newColor);
			setColor(newColorStr, true);
		}

		@Override
		public void mouseReleased(MouseEvent e){
		}

		@Override
		public void mouseClicked(MouseEvent e){
		}

		@Override
		public void mouseEntered(MouseEvent e){
		}

		@Override
		public void mouseExited(MouseEvent e){
		}

	}

}
