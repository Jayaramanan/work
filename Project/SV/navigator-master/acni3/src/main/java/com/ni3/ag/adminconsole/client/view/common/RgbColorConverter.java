/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class RgbColorConverter{
	private static final Logger log = Logger.getLogger(RgbColorConverter.class);

	public Color getColor(String rgbColor){
		if (rgbColor == null || rgbColor.length() == 0){
			return null;
		}

		String[] tokens = rgbColor.split(",");
		if (tokens.length != 3){
			return null;
		}

		List<Integer> colors = new ArrayList<Integer>();
		for (String c : tokens){
			try{
				colors.add(Integer.parseInt(c.trim()));
			} catch (Exception e){
				log.warn("Cannot parse color:" + rgbColor);
			}
		}

		if (colors.size() == 3){
			Color color = new Color(colors.get(0), colors.get(1), colors.get(2));
			log.debug("Color: " + color.toString());
			return color;
		}
		return null;
	}

	public String getColorString(Color color){
		if (color == null){
			return null;
		}
		String colorStr = color.getRed() + "," + color.getGreen() + "," + color.getBlue();
		log.debug("Color string: " + colorStr);
		return colorStr;
	}

}
