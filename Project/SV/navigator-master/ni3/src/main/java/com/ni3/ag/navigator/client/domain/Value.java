/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

import java.awt.*;

import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.proto.NResponse.AttributeValue;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class Value{
	private Attribute attr;
	private int ID;
	private int ParentID;
	private String value;
	private String label;
	private int sort;
	private boolean Enabled;
	private Color HaloColor;
	private boolean HaloColorSelected;
	private boolean toUse;

	public Value(int ID, int ParentID, String value, String label){
		this.ID = ID;
		this.ParentID = ParentID;
		this.value = value;
		this.label = UserSettings.getWord(label);
		Enabled = true;
		toUse = true;
	}

	public Value(NanoXML xml, Attribute valueOf){
		HaloColor = null;
		HaloColorSelected = false;

		attr = valueOf;

		fromXML(xml);
		Enabled = true;
		toUse = true;
	}

	public Value(AttributeValue attributeValue, Attribute valueOf){
		HaloColor = null;
		HaloColorSelected = false;
		sort = 0;

		attr = valueOf;

		ID = attributeValue.getId();
		ParentID = attributeValue.getParentId();
		label = attributeValue.getLabel();
		if (label == null){
			label = "";
		}
		value = attributeValue.getValue();
		sort = attributeValue.getSort();
		toUse = attributeValue.getToUse();
		HaloColor = Utility.createColor(attributeValue.getHaloColor());
		HaloColorSelected = attributeValue.getHaloColorSelected();

		Enabled = true;
	}

	public int getId(){
		return ID;
	}

	public int getParentId(){
		return ParentID;
	}

	public Attribute getAttribute(){
		return attr;
	}

	public boolean isEnabled(){
		return Enabled;
	}

	public void setEnabled(boolean enabled){
		this.Enabled = enabled;
	}

	public boolean isToUse(){
		return toUse;
	}

	public Color getHaloColor(){
		return HaloColor;
	}

	public void setHaloColor(Color haloColor){
		HaloColor = haloColor;
	}

	public boolean isHaloColorSelected(){
		return HaloColorSelected;
	}

	public void setHaloColorSelected(boolean haloColorSelected){
		HaloColorSelected = haloColorSelected;
	}

	public String getLabel(){
		return label;
	}

	public String getValue(){
		return value;
	}

	public int getSort(){
		return sort;
	}

	public String toString(){
		return label;
	}

	public void fromXML(NanoXML xml){
		NanoXMLAttribute attr = xml.Tag.getAttribute("ID");
		if (attr != null)
			ID = attr.getIntegerValue();

		ParentID = 0;

		value = xml.getValue();

		attr = xml.Tag.getAttribute("label");
		if (attr != null)
			label = attr.Value;
	}

	public String toXML(){
		StringBuilder ret2 = new StringBuilder();

		ret2.append("<Value EntityID='");
		if (attr != null){
			ret2.append(attr.ent.ID).append("' AttrID='");
			ret2.append(attr.ID).append("' ID='").append(ID).append("'");
		} else{
			ret2.append("-1' AttrID='-1' ID='").append(ID).append("'");
		}

		ret2.append(" label='").append(label).append("'>").append(value).append("</Value>\n");

		return ret2.toString();
	}

	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (!(obj instanceof Value))
			return false;
		return ((Value) obj).ID == ID;
	}
}
