/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.util;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.ni3.ag.navigator.client.domain.Palette;
import org.apache.log4j.Logger;

public class Utility{
	private static final Logger log = Logger.getLogger(Utility.class);

	public static String DEFAULT_FONT = "Dialog,0,12";

	public static boolean DEBUG = false;

	public static Palette PaletteInUse;

	public static void debugToConsole(String text){
		log.debug(text);
	}

	public static void debugToConsole(String text, int TraceLevel){
		StackTraceElement st[] = Thread.currentThread().getStackTrace();
		log.debug(text);
		for (int n = 3; n < Math.min(3 + TraceLevel, st.length); n++)
			log.debug("    " + st[n].getClassName() + ":" + st[n].getLineNumber());
	}

	public static Color createColor(String colorString){
		// if color string is in format #aaee99
		if (colorString == null || colorString.isEmpty() || "null".equals(colorString))
			return null;

		if ("R".equals(colorString)) // Random color
			return new Color((int) (Math.random() * 255.0), (int) (Math.random() * 255.0), (int) (Math.random() * 255.0));

		if ("A".equals(colorString)) // Automatic from palette
			return PaletteInUse.nextColor();

		if (colorString.startsWith("#")){
			try{
				return Color.decode(colorString);
			} catch (NumberFormatException e){
				log.warn("Unknow color " + colorString);
				return null;
			}
		} else{
			int red, green, blue;
			try{
				StringTokenizer st = new StringTokenizer(colorString, ",");
				red = new Integer(st.nextToken());
				green = new Integer(st.nextToken());
				blue = new Integer(st.nextToken());
			} catch (Exception e){
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
		int style = new Integer(st.nextToken());
		int size = new Integer(st.nextToken());
		return new Font(name, style, size);
	}

	public static boolean processBooleanString(String value){
		return (("1".equals(value)) || ("yes".equalsIgnoreCase(value)) || ("y".equalsIgnoreCase(value))
		        || ("true".equalsIgnoreCase(value)) || ("t".equalsIgnoreCase(value)));
	}

	public static void sleep(long millis){
		try{
			Thread.sleep(millis);
		} catch (InterruptedException e){
			log.debug("sleep interupted", e);
		}
	}

	public static String listToString(List<?> list){
		return listToString(list, ",");
	}

	public static String listToString(List<?> list, String separator){
		StringBuilder sb = new StringBuilder();
		if (list != null){
			for (int i = 0; i < list.size(); i++){
				if (i > 0)
					sb.append(separator);
				sb.append(list.get(i));
			}
		}
		return sb.toString();
	}

	public static List<Integer> stringToIntegerList(String str, String separator){
		List<Integer> list = new ArrayList<Integer>();
		if (str != null && !str.isEmpty()){
			String[] strs = str.split(separator);
			for (String s : strs){
				try{
					final int intValue = Integer.parseInt(s);
					list.add(intValue);
				} catch (NumberFormatException ex){
					// ignore
				}
			}
		}
		return list;
	}
}
