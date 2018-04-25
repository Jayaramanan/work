/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common.clipboard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class ClipboardObject implements Serializable{

	private List<List<Object>> values;
	private List<Class<?>> columnClasses;
	private DatabaseInstance databaseInstance;

	public ClipboardObject(){
		values = new ArrayList<List<Object>>();
		columnClasses = new ArrayList<Class<?>>();
	}

	public Object getValue(int row, int column){
		return values.get(column).get(row);
	}

	public Class<?> getColumnClass(int column){
		return columnClasses.get(column);
	}

	public List<Class<?>> getColumnClasses(){
		return columnClasses;
	}

	public List<List<Object>> getValues(){
		return values;
	}

	public void addColumn(List<Object> column, Class<?> columnClass){
		values.add(column);
		columnClasses.add(columnClass);
	}

	public void setDatabaseInstance(DatabaseInstance databaseInstance){
		this.databaseInstance = databaseInstance;
	}

	public DatabaseInstance getDatabaseInstance(){
		return this.databaseInstance;
	}

}
