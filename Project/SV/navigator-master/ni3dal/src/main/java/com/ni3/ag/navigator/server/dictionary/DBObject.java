/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.dictionary;

import java.util.Map;

import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;

/**
 * @deprecated merge object with domain object DBObject
 */
public class DBObject{
	public int ID;
	public ObjectDefinition ent; // DBObject type

	private Map<Attribute, String> attributeToValueMap = null;

	public String getAttributeValue(Attribute a){
		return attributeToValueMap.get(a);
	}

	public Map<Attribute, String> getAttributeToValueMap(){
		return attributeToValueMap;
	}

	public void setAttributeValue(Attribute a, String value){
		attributeToValueMap.put(a, value);
	}

	public DBObject(int id, ObjectDefinition entity, Map<Attribute, String> attributeToValueMap){
		this.ID = id;
		this.ent = entity;
		this.attributeToValueMap = attributeToValueMap;
	}

	public DBObject(ObjectDefinition entity, Map<Attribute, String> attributeToValueMap){
		this.ent = entity;
		this.attributeToValueMap = attributeToValueMap;
	}

	@Override
	public String toString(){
		return "DBObject [ent=" + ent + ", ID=" + ID;
	}
}
