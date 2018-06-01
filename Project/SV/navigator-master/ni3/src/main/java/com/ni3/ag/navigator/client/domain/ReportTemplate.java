/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

import java.awt.Image;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ReportTemplate{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Icon previewIcon;
	private String name;
	private ReportType type;
	private Map<Integer, List<Integer>> selectedColumns = new HashMap<Integer, List<Integer>>();

	public ReportTemplate(String name){
		this.name = name;
	}

	public ReportTemplate(String name, Integer id){
		this.id = id;
		this.name = name;
	}

	public ReportTemplate(String name, Integer id, ReportType type, byte[] preview){
		this.id = id;
		this.name = name;
		this.type = type;
		if (preview != null && preview.length > 0){
			ImageIcon imageIcon = new ImageIcon(preview);
			Image img = imageIcon.getImage();
			previewIcon = new ImageIcon(img);
		}
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getName(){
		return name;
	}

	public ReportType getType(){
		return type;
	}

	public void setPreviewIcon(Icon previewIcon){
		this.previewIcon = previewIcon;
	}

	public void setType(ReportType type){
		this.type = type;
	}

	public boolean isDynamicReport(){
		return ReportType.DYNAMIC_REPORT.equals(type) || ReportType.MERGED.equals(type);
	}

	public boolean isStaticReport(){
		return ReportType.STATIC_REPORT.equals(type);
	}

	public Icon getPreviewIcon(){
		return previewIcon;
	}

	public Map<Integer, List<Integer>> getSelectedColumns(){
		return selectedColumns;
	}

	public void setSelectedColumns(Map<Integer, List<Integer>> initialValues){
		selectedColumns = initialValues;
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj){
			return true;
		}
		if (obj == null || !(obj instanceof ReportTemplate)){
			return false;
		}
		if (getId() == null || ((ReportTemplate) obj).getId() == null){
			return false;
		}
		return getId().intValue() == ((ReportTemplate) obj).getId().intValue();
	}

}
