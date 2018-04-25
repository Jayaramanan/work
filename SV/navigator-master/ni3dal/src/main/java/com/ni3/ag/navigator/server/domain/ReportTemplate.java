/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.domain;

public class ReportTemplate{
	private Integer id;
	private byte[] template;
	private byte[] previewIcon;
	private String name;
	private ReportType type;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public byte[] getTemplate(){
		return template;
	}

	public void setTemplate(byte[] template){
		this.template = template;
	}

	public byte[] getPreviewIcon(){
		return previewIcon;
	}

	public void setPreviewIcon(byte[] previewIcon){
		this.previewIcon = previewIcon;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public ReportType getType(){
		return type;
	}

	public void setType(ReportType type){
		this.type = type;
	}

}
