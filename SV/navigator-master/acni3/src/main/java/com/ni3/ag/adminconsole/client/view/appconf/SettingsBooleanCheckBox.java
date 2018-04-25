/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class SettingsBooleanCheckBox extends JCheckBox{

	private static final long serialVersionUID = -810349449230187358L;
	private Border changedBorder;

	public SettingsBooleanCheckBox(){
		setHorizontalAlignment(SwingConstants.CENTER);
		changedBorder = BorderFactory.createLineBorder(Color.BLUE);
		setBorderPainted(false);
	}

	public void setChanged(boolean changed){
		if (changed){
			setBorder(changedBorder);
		}
		setBorderPainted(changed);
	}

}
