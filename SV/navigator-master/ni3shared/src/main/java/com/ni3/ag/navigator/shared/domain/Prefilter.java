package com.ni3.ag.navigator.shared.domain;

public class Prefilter{
	private int id;
	private int groupId;
	private int schemaId;
	private int objectDefinitionId;
	private int attributeId;
	private int predefinedId;

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getGroupId(){
		return groupId;
	}

	public void setGroupId(int groupId){
		this.groupId = groupId;
	}

	public int getObjectDefinitionId(){
		return objectDefinitionId;
	}

	public void setObjectDefinitionId(int objectDefinitionId){
		this.objectDefinitionId = objectDefinitionId;
	}

	public int getAttributeId(){
		return attributeId;
	}

	public void setAttributeId(int attributeId){
		this.attributeId = attributeId;
	}

	public int getPredefinedId(){
		return predefinedId;
	}

	public void setPredefinedId(int predefinedId){
		this.predefinedId = predefinedId;
	}

	public int getSchemaId(){
		return schemaId;
	}

	public void setSchemaId(int schemaId){
		this.schemaId = schemaId;
	}
}
