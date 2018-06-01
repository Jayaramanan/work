/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;

public class ACChangeableLabel extends JLabel implements ChangeResetable{

	private static final long serialVersionUID = 1L;

	private Icon startIcon;
	private Icon currentIcon;
	private Border emptyBorder;
	private Border blueBorder;

	public ACChangeableLabel(){
		this(null, null, JLabel.LEADING);
	}

	public ACChangeableLabel(String text, ImageIcon icon, int horizontalAlignment){
		super(text, icon, horizontalAlignment);
		setStartIcon(icon);
		blueBorder = BorderFactory.createLineBorder(Color.BLUE);
		emptyBorder = BorderFactory.createEmptyBorder();
	}

	public void setStartIcon(ImageIcon icon){
		startIcon = icon;
		setIcon(icon);
	}

	@Override
	public void setIcon(Icon icon){
		super.setIcon(icon);
		currentIcon = icon;
		setBorderColor();
	}

	private void setBorderColor(){
		if (isChanged()){
			this.setBorder(blueBorder);
		} else{
			this.setBorder(emptyBorder);
		}
	}

	@Override
	public boolean isChanged(){
		if ((startIcon == null && currentIcon == null) || (startIcon != null && startIcon.equals(currentIcon))){
			return false;
		}
		return true;
	}

	@Override
	public void resetChanges(){
		startIcon = currentIcon;
		setBorderColor();
	}

}
