/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.shared.util.nanoXML;

import java.awt.*;

public class NanoXMLAttribute{
	public String Name;
	public String Value;

	public int EndPos;

	public NanoXMLAttribute(String attr, int beg, int end){
		int EqPos = attr.indexOf("=", beg);
		if (EqPos != -1){
			Name = attr.substring(beg, EqPos).trim();

			int FirstPos = attr.indexOf("'", EqPos);
			EndPos = FirstPos + 1;
			do{
				EndPos = attr.indexOf("'", EndPos);
				if (EndPos == -1 || EndPos > end)
					return;

				if (attr.charAt(EndPos + 1) != '\'')
					break;
			} while (EndPos < end && EndPos != -1);

			Value = attr.substring(FirstPos + 1, EndPos);
		}
	}

	public NanoXMLAttribute(String Attr, String AttrName, int beg, int end){
		int EqPos = Attr.indexOf(" " + AttrName + "='", beg);
		if (EqPos != -1){
			Name = Attr.substring(EqPos - AttrName.length() - 1, EqPos).trim();

			int FirstPos = Attr.indexOf("'", EqPos);
			EndPos = FirstPos + 1;
			do{
				EndPos = Attr.indexOf("'", EndPos);
				if (EndPos == -1 || EndPos > end)
					return;

				if (Attr.charAt(EndPos + 1) != '\'')
					break;
			} while (EndPos < end && EndPos != -1);

			Value = Attr.substring(FirstPos + 1, EndPos);
		}
	}

	public String getValue(){
		return Value;
	}

	public int getIntegerValue(){
		try{
			return Integer.decode(Value);
		} catch (NumberFormatException e){
			return 0;
		}
	}

	public double getDoubleValue(){
		try{
			return Double.parseDouble(Value);
		} catch (NumberFormatException e){
			return 0.0;
		}
	}

	public float getFloatValue(){
		try{
			return Float.parseFloat(Value);
		} catch (NumberFormatException e){
			return 0.0f;
		}
	}

//	public Color getColorValue(){
//		try{
//			return com.ni3.ag.navigator.client.util.Utility.createColor(Value);
//		} catch (NumberFormatException e){
//			return null;
//		}
//	}

	public boolean getBooleanValue(){
		return "true".equals(Value);
	}

	public Point getPointValue(){
		int pos = Value.indexOf(",");
		if (pos == -1)
			return null;

		Point ret = new Point();

		try{
			ret.x = Integer.decode(Value.substring(0, pos));
			ret.y = Integer.decode(Value.substring(pos + 1));
		} catch (NumberFormatException e){
			return null;
		}

		return ret;
	}
}
