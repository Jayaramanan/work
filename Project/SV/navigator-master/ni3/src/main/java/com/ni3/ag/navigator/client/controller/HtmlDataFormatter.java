/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.UserSettings;

public class HtmlDataFormatter{
	private static final String LESS_HTML = "&lt;";
	private static final String GREATER_HTML = "&gt;";
	private static final String NEW_LINE_HTML = "<BR>";

	private final int tooltipHTMLFontSize;
	private final int tooltipHTMLWrapLen;

	public HtmlDataFormatter(){
		tooltipHTMLFontSize = Integer.valueOf(UserSettings.getProperty("Applet", "TooltipHTMLFontSize", "3"));
		tooltipHTMLWrapLen = Integer.valueOf(UserSettings.getProperty("Applet", "TooltipHTMLWrapLen", "30"));
	}

	public String getObjectTooltip(DBObject obj){
		final StringBuilder ret = new StringBuilder();
		ret.append("<HTML><FONT size=").append(tooltipHTMLFontSize).append("><TABLE border=0 CELLSPACING=0 CELLPADDING=0>");

		for (final Attribute a : obj.getEntity().getReadableAttributes()){
			if (a.inToolTip && obj.getValue(a.ID) != null){
				ret.append("<TR><TD valign=\"top\">");

				final String Label = formatLabel(a, a.label);

				ret.append(wrapString(null, Label, tooltipHTMLWrapLen, 0)).append(":&nbsp;</TD><TD>");

				String value = a.displayValueAsPartOfHTML(obj.getValue(a.ID));

				// replace < and > signs for html
				if (!a.isURLAttribute()){
					value = value.replace("<", LESS_HTML).replace(">", GREATER_HTML);
				}
				if (a.predefined && !a.multivalue){
					value = value.replace("\n", NEW_LINE_HTML);
				}
				if (a.multivalue){
					value = replaceSemicolon(value);
				}

				value = wrapString(a, formatLabel(a, value), tooltipHTMLWrapLen, 0);

				ret.append(value);
				ret.append("</TD></TR>");
			}
		}
		ret.append("</TABLE></FONT></HTML>");
		return ret.toString();
	}

	String wrapString(final Attribute a, final String s, final int MaxWidth, final int ident){
		if (s.length() > MaxWidth){
			String p[];

			if (a != null && a.isURLAttribute()){
				p = s.split(";");
			} else{
				p = s.split("[ ]");
			}

			String ret = "";
			int i = 0;
			for (final String ss : p){
				if (ret.length() > 0){
					if (i > MaxWidth){
						i = 0;
						ret += "<BR>";
						for (int xx = 0; xx < ident; xx++){
							ret += "&nbsp;";
						}
					} else{
						if (a != null && a.isURLAttribute()){
							ret += ";";
						} else{
							ret += " ";
						}
					}
				}

				ret += ss;
				i += ss.length();
			}

			return ret;
		} else{
			return s;
		}
	}

	private String replaceSemicolon(String str){
		// replace ";" for multivalue attributes
		String result = str.replace(";", NEW_LINE_HTML);
		// return back < and > signs
		result = result.replace("&lt<BR>", LESS_HTML).replace("&gt<BR>", GREATER_HTML);
		return result;
	}

	private String formatLabel(final Attribute a, String t){
		final StringBuilder txt = new StringBuilder();
		if (a.isLabelBold()){
			t = txt.append("<B>").append(t).append("</B>").toString();
			txt.delete(0, txt.length());
		}
		if (a.isLabelUnderline()){
			t = txt.append("<U>").append(t).append("</U>").toString();
			txt.delete(0, txt.length());
		}
		if (a.isLabelItalic()){
			t = txt.append("<I>").append(t).append("</I>").toString();
		}
		return t;
	}
}
