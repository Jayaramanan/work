package com.ni3.ag.adminconsole.domain;

public class DeltaParam{

	private long id;
	private String name;
	private String value;
	private DeltaHeader header;

	public DeltaParam(){
	}

	public DeltaParam(DeltaHeader header, final String name, final String value){
		this.header = header;
		this.name = name;
		this.value = value;
	}

	public DeltaParam(DeltaHeader header, final DeltaParamIdentifier identifier, final String value){
		this(header, identifier.getIdentifier(), value);
	}

	public long getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public String getValue(){
		return value;
	}

	public Boolean getValueAsBoolean(){
		if (value == null){
			return null;
		}
		return !"0".equals(value);
	}

	public Integer getValueAsInteger(){
		Integer ret = null;
		try{
			ret = Integer.valueOf(value);
		} catch (final NumberFormatException e){
			// ignore
		}

		return ret;
	}

	public void setId(final long id){
		this.id = id;
	}

	public void setName(final String name){
		this.name = name;
	}

	public void setValue(final String value){
		this.value = value;
	}

	public DeltaHeader getHeader(){
		return header;
	}

	public void setHeader(DeltaHeader header){
		this.header = header;
	}

	@Override
	public String toString(){
		final StringBuilder sb = new StringBuilder();
		sb.append("DeltaParam");
		sb.append("{id=").append(id);
		sb.append(", name=").append(name);
		sb.append(", value='").append(value).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
