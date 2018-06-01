/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.shared.util.nanoXML;

public class NanoXMLTag{
	public int TagPositionBeg, TagPositionEnd;
	public String Name;
	public String XML;
	public boolean SimpleTag;
	int NextAttrPos;

	public NanoXMLTag(String xml, int TagBegin, int TagEnd){
		XML = xml;
		TagPositionBeg = TagBegin;
		TagPositionEnd = TagEnd;
		TagPositionBeg = XML.indexOf("<", TagBegin);

		int n = TagPositionBeg + 1;
		int l = XML.length();
		boolean inValue = false;
		TagPositionEnd = -1;
		while (n < l){
			//TODO: DIRTY HACK: if there is an 's' following a ' - then it is NOT an end of the value, like in eg "Peter's house"
			if (XML.charAt(n) == '\'' && XML.length() < n+1 && XML.charAt(n+1) != 's'){
				inValue = !inValue;
			}

			if (XML.charAt(n) == '>' && !inValue){
				TagPositionEnd = n;
				break;
			}

			n++;
		}

		if (XML.charAt(TagPositionEnd - 1) == '/'){
			TagPositionEnd--;
			SimpleTag = true;
		} else{
			SimpleTag = false;
		}

		int NameEnd = XML.indexOf(" ", TagPositionBeg);
		if (NameEnd == -1 || NameEnd > TagPositionEnd)
			NameEnd = TagPositionEnd;
		NameEnd--;

		Name = XML.substring(TagPositionBeg + 1, NameEnd + 1);
		NextAttrPos = NameEnd + 1;
	}

	public NanoXMLAttribute getNextAttribute(){
		if (NextAttrPos < TagPositionEnd){
			NanoXMLAttribute attr = new NanoXMLAttribute(XML, NextAttrPos, TagPositionEnd);
			if (attr.Value == null){
				return null;
			}
			NextAttrPos = attr.EndPos + 1;

			return attr;
		}

		return null;
	}

	public NanoXMLAttribute getAttribute(String AttrName){
		if (NextAttrPos < TagPositionEnd){
			NanoXMLAttribute attr = new NanoXMLAttribute(XML, AttrName, TagPositionBeg, TagPositionEnd);
			if (attr.Value == null){
				return null;
			}
			NextAttrPos = attr.EndPos + 1;

			return attr;
		}

		return null;
	}

}
