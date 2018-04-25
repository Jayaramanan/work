/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.domain.metaphor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import com.ni3.ag.navigator.client.domain.cache.IconCache;

public class NumericMetaphor implements Comparable<NumericMetaphor>{
	public static final Font FONT = new Font("Arial", Font.BOLD, 10);
	public static final Color FONT_COLOR = new Color(193, 0, 0);;
	public static final Image INITIAL_IMAGE = IconCache.getImage(IconCache.MAP_PIN_TOGGLE);;

	private int index;

	public NumericMetaphor(int index){
		this.index = index;
	}

	public int getIndex(){
		return index;
	}

	@Override
	public int compareTo(NumericMetaphor o){
		return o != null ? index - o.index : 1;
	}

}
