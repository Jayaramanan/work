/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class ReportTemplate implements Serializable, Cloneable{

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private Integer type;
	private String xml;
	private byte[] preview;
	private Schema schema;

	public void setId(Integer id){
		this.id = id;
	}

	public Integer getId(){
		return id;
	}

	public void setSchema(Schema schema){
		this.schema = schema;
	}

	public Schema getSchema(){
		return schema;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setType(Integer type){
		this.type = type;
	}

	public Integer getType(){
		return type;
	}

	public ReportType getReportType(){
		return ReportType.fromValue(type);
	}

	public void setReportType(ReportType reportType){
		this.type = reportType.getValue();
	}

	public void setXml(String xml){
		this.xml = xml;
	}

	public String getXml(){
		return xml;
	}

	public void setPreview(byte[] preview){
		this.preview = preview;
	}

	public byte[] getPreview(){
		return preview;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof ReportTemplate))
			return false;
		if (o == this)
			return true;
		ReportTemplate report = (ReportTemplate) o;
		if (getId() == null || report.getId() == null)
			return false;
		return getId().equals(report.getId());
	}

	public ReportTemplate clone() throws CloneNotSupportedException{
		return (ReportTemplate) super.clone();
	}

	public ReportTemplate clone(Integer id, Schema schema) throws CloneNotSupportedException{
		ReportTemplate rt = clone();
		rt.setSchema(schema);
		rt.setId(id);
		return rt;
	}

}
