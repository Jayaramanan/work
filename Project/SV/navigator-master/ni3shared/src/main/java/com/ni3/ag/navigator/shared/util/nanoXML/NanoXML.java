/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.shared.util.nanoXML;

public class NanoXML{
	int BodyPositionBeg, BodyPositionEnd;
	int NextElement;
	String XML;
	public NanoXMLTag Tag;
	int XMLBeg, XMLEnd;
	int ValueBeg, ValueEnd;

	public NanoXML(String xml, int start, int end){
		XML = xml;
		XMLBeg = start;

		Tag = new NanoXMLTag(xml, start, end);

		ValueBeg = Tag.TagPositionEnd + 1;

		if (Tag.SimpleTag)
			ValueEnd = Tag.TagPositionEnd + 1;
		else{
			ValueEnd = xml.indexOf("</" + Tag.Name + ">", start);
		}

		NextElement = ValueBeg;

		if (Tag.SimpleTag)
			XMLEnd = ValueEnd;
		else
			XMLEnd = ValueEnd + Tag.Name.length() + 2;
	}

	public NanoXML getNextElement(){
		if (NextElement < ValueEnd){
			NanoXML ret = new NanoXML(XML, NextElement, ValueEnd);
			if (ret != null){
				NextElement = ret.XMLEnd + 1;
			} else
				NextElement = ValueEnd;

			return ret;
		}

		return null;
	}

	public String getName(){
		return Tag.Name;
	}

	public String getValue(){
		try {
			return XML.substring(ValueBeg, ValueEnd);
		} catch (StringIndexOutOfBoundsException e){
			System.out.println("XML: " + XML);
			System.out.println("vstart: " + ValueBeg + " end: " + ValueEnd);
			System.out.println("xstart: " + XMLBeg + " end: " + XMLEnd);

		}
		return null;
	}

	public NanoXML copy(){
		return new NanoXML(XML, XMLBeg, XML.length());
	}
}
