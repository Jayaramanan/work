package com.ni3.ag.navigator.server.domain;

public class DeltaParam{
	private long id;

	// This field is not used at the moment. DeltaParamIdentifier will be used to determine a list of mandatory delta
	// params
	// private DeltaParamType type;

	private DeltaParamIdentifier name;
	private String value;

	public DeltaParam(DeltaParamIdentifier name, String value){
		this.name = name;
		this.value = value;
	}

	public long getId(){
		return id;
	}

	public void setId(long id){
		this.id = id;
	}

	public DeltaParamIdentifier getName(){
		return name;
	}

	public void setName(DeltaParamIdentifier name){
		this.name = name;
	}

	public String getValue(){
		return value;
	}

	public void setValue(String value){
		this.value = value;
	}

	public Integer getValueAsInteger(){
		Integer ret = null;
		try{
			ret = Integer.valueOf(value);
		} catch (NumberFormatException e){
			// ignore
		}

		return ret;
	}

	public Boolean getValueAsBoolean(){
		if (value == null)
			return null;
		return !"0".equals(value);
	}

	@Override
	public String toString(){
		return "DeltaParam [id=" + id + ", name=" + name + ", value=" + value + "]";
	}

}
