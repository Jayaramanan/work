/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

import java.util.ArrayList;
import java.util.List;

public class Context{
	public int ID;
	public String name;
	public Attribute pk;

	private List<Attribute> attributes;

	public Entity ent;

	public Context(Entity ent, com.ni3.ag.navigator.shared.proto.NResponse.Context context){
		this.ent = ent;

		ID = context.getId();
		name = context.getName();
		pk = ent.getAttribute(context.getPkAttributeId());

		attributes = new ArrayList<Attribute>();
		final List<Integer> list = context.getRelatedAttributesList();
		for (int i = 0; i < context.getRelatedAttributesCount(); i++){
			attributes.add(ent.getAttribute(list.get(i)));
		}
	}

	public List<Attribute> getAttributes(){
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes){
		this.attributes = attributes;
	}
}
