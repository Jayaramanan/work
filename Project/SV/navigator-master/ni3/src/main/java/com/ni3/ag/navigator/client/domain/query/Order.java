/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain.query;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.shared.util.StringTokenizerEx;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class Order{
	public Entity ent;
	public Attribute attr;
	public boolean asc;

	public Order(Entity ent){
		this.ent = ent;
		attr = null;
		asc = true;
	}

	public Order(Attribute attr, boolean asc){
		this.ent = attr.ent;
		this.attr = attr;
		this.asc = asc;
	}

	public String toXML(){
		return "<Order AttrID='" + attr.ID + "' Asc='" + asc + "'/>";
	}

	public void fromXML(NanoXML xml){
		NanoXMLAttribute attrXML;

		while ((attrXML = xml.Tag.getNextAttribute()) != null){
			if ("AttrID".equals(attrXML.Name)){
				attr = ent.getAttribute(attrXML.getIntegerValue());
			} else if ("Asc".equals(attrXML.Name)){
				asc = com.ni3.ag.navigator.client.util.Utility.processBooleanString(attrXML.getValue());
			}
		}
	}

	public String toString(){
		return attr.ID + "\t" + asc + "\t";
	}

	public boolean fromString(StringTokenizerEx tok){
		if (!tok.hasMoreTokens())
			return false;

		String ID = tok.nextToken();
		if ("End".equals(ID))
			return false;

		attr = ent.getAttribute(Integer.valueOf(ID));
		asc = com.ni3.ag.navigator.client.util.Utility.processBooleanString(tok.nextToken());

		return true;
	}

	public int getAttributeId(){
		return attr.ID;
	}

	public boolean getAsc(){
		return asc;
	}
}
