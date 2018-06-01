package com.ni3.ag.licensecreator.model;

import java.util.Date;

public class LicenseCreatorModel{

	private Date startDate, expiryDate;
	private int adminCount;
	private String systemId, client;
	private PropertyTableModel tableModel;

	public void setStartDate(Date date){
		startDate = date;
	}

	public void setExpiryDate(Date date){
		expiryDate = date;
	}

	public void setAdminCount(int i){
		adminCount = i;
	}

	public Date getStartDate(){
		return startDate;
	}

	public Date getExpiryDate(){
		return expiryDate;
	}

	public Integer getAdminCount(){
		return adminCount;
	}

	public String getSystemId(){
		return systemId;
	}

	public void setSystemId(String sid){
		systemId = sid;
	}

	public void setClient(String client){
		this.client = client;
	}

	public String getClient(){
		return client;
	}

	public void setPropertyTableModel(PropertyTableModel tModel){
		tableModel = tModel;
	}

	public PropertyTableModel getPropertyTableModel(){
		return tableModel;
	}

}
