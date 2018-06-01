/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class AttributeGroupModel extends AbstractModel{

	private AttributeGroup attributeGroup;
	private String attributeName;
	private String managing;

	public void setAttributeGroup(AttributeGroup attributeGroup){
		this.attributeGroup = attributeGroup;
	}

	public AttributeGroup getAttributeGroup(){
		return attributeGroup;
	}

	public void setAttributeName(String attributeName){
		this.attributeName = attributeName;
	}

	public String getAttributeName(){
		return attributeName;
	}

	public void setManaging(String managing){
		this.managing = managing;
	}

	public String getManaging(){
		return managing;
	}
}
