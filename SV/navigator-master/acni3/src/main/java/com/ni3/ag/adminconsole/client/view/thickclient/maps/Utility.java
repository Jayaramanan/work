/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.maps;

import java.awt.Color;
import java.awt.Font;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class Utility{

	private final static Logger log = Logger.getLogger(Utility.class);

	public static final boolean DEBUG = false;
	public static final String DEFULT_FONT = "Dialog,0,12";
	public static final String DEFULT_COLOR = "150,150,150";

	// public static Palette PaletteInUse;

	public static Color createColor(String colorString){
		// if color string is in format #aaee99
		if ("null".equals(colorString))
			return null;

		if ("R".equals(colorString)) // Random color
			return new Color((int) (Math.random() * 255.0), (int) (Math.random() * 255.0), (int) (Math.random() * 255.0));

		if (colorString.startsWith("#")){
			try{
				return Color.decode(colorString);
			} catch (NumberFormatException e){
				log.error("Unknow color " + colorString, e);
				return null;
			}
		} else{
			int red, green, blue;
			try{
				StringTokenizer st = new StringTokenizer(colorString, ",");
				red = new Integer(st.nextToken()).intValue();
				green = new Integer(st.nextToken()).intValue();
				blue = new Integer(st.nextToken()).intValue();
			} catch (Exception e){
				log.error("Unknown color " + colorString, e);
				red = green = blue = 0;
			}

			return new Color(red, green, blue);
		}
	}

	public static String encodeColor(Color color){
		// if color string is in format #aaee99
		if (color == null)
			return "null";

		String rgb = Integer.toHexString(color.getRGB());
		return "#" + rgb.substring(2, rgb.length());
	}

	public static Font createFont(String fontString){
		StringTokenizer st = new StringTokenizer(fontString, ",");
		String name = st.nextToken();
		int style = new Integer(st.nextToken()).intValue();
		int size = new Integer(st.nextToken()).intValue();
		return new Font(name, style, size);
	}

	public static boolean processBooleanString(String value){
		return (("1".equals(value)) || ("yes".equalsIgnoreCase(value)) || ("y".equalsIgnoreCase(value))
		        || ("true".equalsIgnoreCase(value)) || ("t".equalsIgnoreCase(value)));
	}

}
