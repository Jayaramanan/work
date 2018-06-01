/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;
import javax.swing.Icon;
import javax.swing.JLabel;

public class ACMandatoryLabel extends JLabel{
	private static final long serialVersionUID = 1L;

	public ACMandatoryLabel(){
		this("", null, LEADING);
	}

	public ACMandatoryLabel(Icon image, int horizontalAlignment){
		this(null, image, horizontalAlignment);
	}

	public ACMandatoryLabel(Icon image){
		this(null, image, CENTER);
	}

	public ACMandatoryLabel(String text, int horizontalAlignment){
		this(text, null, horizontalAlignment);
	}

	public ACMandatoryLabel(String text){
		this(text, null, LEADING);
	}

	public ACMandatoryLabel(String text, Icon icon, int horizontalAlignment){
		super(text, icon, horizontalAlignment);
		setForeground(Color.RED);
	}
}
