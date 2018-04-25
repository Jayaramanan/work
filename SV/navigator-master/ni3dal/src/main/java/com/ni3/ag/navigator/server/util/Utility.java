/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.util;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class Utility{
	private static final Logger log = Logger.getLogger(Utility.class);

	public static String DEFULT_FONT = "Dialog,0,12";
	public static String DEFULT_COLOR = "150,150,150";

	public static void debugToConsole(String text){
		if (log.isDebugEnabled()){
			log.debug(text);
		}
	}

	public static void debugToConsole(String text, int TraceLevel){
		if (log.isDebugEnabled()){
			StackTraceElement st[] = Thread.currentThread().getStackTrace();
			log.debug(text);
			for (int n = 3; n < 3 + TraceLevel; n++)
				log.debug("    " + st[n].getClassName() + ":" + st[n].getLineNumber());
		}
	}

	public static Color createColor(String colorString){
		// if color string is in format #aaee99
		if (colorString.startsWith("#")){
			return Color.decode(colorString);
		} else{
			StringTokenizer st = new StringTokenizer(colorString, ",");
			int red = new Integer(st.nextToken()).intValue();
			int green = new Integer(st.nextToken()).intValue();
			int blue = new Integer(st.nextToken()).intValue();

			return new Color(red, green, blue);
		}
	}

	public static String colorToHexString(Color color){
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

	public static List<String> stringToList(String str){
		return stringToList(str, ",");
	}

	public static List<String> stringToList(String str, String separator){
		List<String> list = new ArrayList<String>();
		if (str != null && !str.isEmpty()){
			String[] strs = str.split(separator);
			list = Arrays.asList(strs);
		}
		return list;
	}

	public static List<Integer> stringToIntegerList(String str){
		return stringToIntegerList(str, ",");
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
}
