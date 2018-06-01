package com.ni3.ag.adminconsole.server.jobs.data;

import java.util.ArrayList;
import java.util.List;

public class UserDataTable{
	private String tableName;
	private List<String> columnNames = new ArrayList<String>();

	public UserDataTable(String tableName){
		this.tableName = tableName;
	}

	public String getTableName(){
		return tableName;
	}

	public void setTableName(String tableName){
		this.tableName = tableName;
	}

	public List<String> getColumnNames(){
		return columnNames;
	}
}