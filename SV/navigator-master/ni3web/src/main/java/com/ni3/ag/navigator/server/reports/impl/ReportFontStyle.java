/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.reports.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;

public class ReportFontStyle{
	private static Map<String, Style> styleMap = new HashMap<String, Style>();

	public static Style getColumnHeaderStyle(boolean bold, boolean italic, boolean underline){
		String key = true + ";" + bold + ";" + italic + ";" + underline;
		Style style = styleMap.get(key);
		if (style == null){
			style = new Style();
			Font font = new Font(8, "SansSerif", bold, italic, underline);
			style.setFont(font);
			style.setHorizontalAlign(HorizontalAlign.CENTER);
			style.setBackgroundColor(new Color(119, 177, 28));
			style.setBorder(Border.PEN_1_POINT);
			style.setBorderBottom(Border.PEN_2_POINT);
			style.setTransparency(Transparency.OPAQUE);
			styleMap.put(key, style);
		}
		return style;
	}

	public static Style getDetailStyle(boolean bold, boolean italic, boolean underline){
		String key = false + ";" + bold + ";" + italic + ";" + underline;
		Style style = styleMap.get(key);
		if (style == null){
			style = new Style();
			Font font = new Font(8, "SansSerif", bold, italic, underline);
			style.setFont(font);
			style.setPaddingLeft(2);
			style.setBorder(Border.THIN);
		}
		return style;
	}

	public static Style getDetailStyleMetaphor(){
		Style style = new Style();
		Font font = new Font(8, "SansSerif", true, false, false);
		style.setFont(font);
		style.setBorder(Border.THIN);
		style.setTextColor(new Color(193, 0, 0));
		style.setHorizontalAlign(HorizontalAlign.RIGHT);
		return style;
	}
}
