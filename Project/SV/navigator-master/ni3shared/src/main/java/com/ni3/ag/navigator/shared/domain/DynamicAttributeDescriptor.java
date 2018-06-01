package com.ni3.ag.navigator.shared.domain;

import java.util.List;

public class DynamicAttributeDescriptor{
	private static int uniqID = -10;
	private int fakeId;
	private int fromEntity;
	private int fromAttribute;
	private String operation;
	private Object attribute;
	private List<Integer> ids;
	private int schema;

	public DynamicAttributeDescriptor(){
		this.fakeId = --uniqID;
		if(uniqID == Integer.MIN_VALUE)
			uniqID = -1;
	}

	public Integer getFakeAttributeId(){
		return fakeId;
	}

	public void setFromEntity(int fromEntity){
		this.fromEntity = fromEntity;
	}

	public void setFromAttribute(int fromAttribute){
		this.fromAttribute = fromAttribute;
	}

	public void setOperation(String operation){
		this.operation = operation;
	}

	public void setAttribute(Object attribute){
		this.attribute = attribute;
	}

	public void setIds(List<Integer> ids){
		this.ids = ids;
	}

	public void setSchema(int schema){
		this.schema = schema;
	}

	public Object getAttribute(){
		return attribute;
	}

	public int getFakeId(){
		return fakeId;
	}

	public int getFromEntity(){
		return fromEntity;
	}

	public int getFromAttribute(){
		return fromAttribute;
	}

	public String getOperation(){
		return operation;
	}

	public List<Integer> getIds(){
		return ids;
	}

	public int getSchema(){
		return schema;
	}

	public void setFakeId(int fakeId){
		this.fakeId = fakeId;
	}

	@Override
	public String toString(){
		return "DynamicAttributeDescriptor{" +
				"fakeId=" + fakeId +
				", fromEntity=" + fromEntity +
				", fromAttribute=" + fromAttribute +
				", operation='" + operation + '\'' +
				", attribute=" + attribute +
				", ids=" + ids +
				", schema=" + schema +
				'}';
	}
}
