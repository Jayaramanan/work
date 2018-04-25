/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.domain;

import java.awt.BasicStroke;
import java.awt.Color;

public class InvisibleStroke extends BasicStroke{
	private final Color transparentColor;

	public InvisibleStroke(float width){
		super(width);
		transparentColor = new Color(0, 0, 0, 0);
	}

	public Color getColor(){
		return transparentColor;
	}
}
