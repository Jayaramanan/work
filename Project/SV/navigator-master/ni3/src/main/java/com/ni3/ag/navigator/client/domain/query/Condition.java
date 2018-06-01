/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain.query;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.Schema;
import com.ni3.ag.navigator.shared.domain.DataType;
import com.ni3.ag.navigator.shared.util.StringTokenizerEx;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class Condition{
	private Entity ent;
	private Attribute attr;
	private String operation;
	private Object value;

	public Condition(Entity ent){
		this.ent = ent;
		attr = null;
		operation = null;
		value = null;
	}

	public Condition(Attribute attr, String operation, Object value){
		this.ent = attr.ent;
		this.attr = attr;
		this.operation = operation;
		this.value = value;
	}

	public String toXML(){
		return "<Condition AttrID='" + attr.ID + "' Operation='" + operation + "' Value='"
				+ attr.getDataType().getTransferString(value) + "'/>";
	}

	public void fromXML(NanoXML xml){
		NanoXMLAttribute attrXML;

		while ((attrXML = xml.Tag.getNextAttribute()) != null){
			if ("AttrID".equals(attrXML.Name)){
				attr = ent.getAttribute(attrXML.getIntegerValue());
			} else if ("Operation".equals(attrXML.Name)){
				operation = attrXML.getValue();
			} else if ("Value".equals(attrXML.Name)){
				if (attr.predefined){
					value = attrXML.getValue();
				} else{
					value = attr.getDataType().getValue(attrXML.getValue());
				}
			}
		}
	}

	public String toString(){
		return attr.ID + "\t" + operation + "\t" + attr.getDataType().getTransferString(value) + "\t";
	}

	public boolean fromString(Schema schema, StringTokenizerEx tok){
		if (!tok.hasMoreTokens())
			return false;

		String ID = tok.nextToken();
		if ("End".equals(ID))
			return false;

		attr = ent.getAttribute(Integer.valueOf(ID));
		operation = tok.nextToken();
		value = attr.getDataType().getValue(tok.nextToken());

		return true;
	}

	public int getAttributeId(){
		return attr.ID;
	}

	public String getOperation(){
		return operation;
	}

	public String getTerm(){
		if (attr.getDType() == DataType.DATE && attr.predefined)
			return value.toString();
		return attr.getDataType().getTransferString(value);
	}
}
