package com.ni3.ag.navigator.server.domain;

public class ContextAttribute{
	private Integer id;
	private Integer contextId;
	private Integer attributeId;

	public Integer getAttributeId(){
		return attributeId;
	}

	public Integer getContextId(){
		return contextId;
	}

	public Integer getId(){
		return id;
	}

	public void setAttributeId(final Integer attributeId){
		this.attributeId = attributeId;
	}

	public void setContextId(final Integer contextId){
		this.contextId = contextId;
	}

	public void setId(final Integer id){
		this.id = id;
	}
}
