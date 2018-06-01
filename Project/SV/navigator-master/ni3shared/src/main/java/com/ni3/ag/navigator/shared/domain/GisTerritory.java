package com.ni3.ag.navigator.shared.domain;

public class GisTerritory{
	private int id;
	private String label;
	private String territory;
	private String tableName;
	private String displayColumn;
	private int version;

	public int getId(){
		return id;
	}

	public String getLabel(){
		return label;
	}

	public String getTerritory(){
		return territory;
	}

	public void setId(int id){
		this.id = id;
	}

	public void setTerritory(String territory){
		this.territory = territory;
	}

	public void setLabel(String label){
		this.label = label;
	}

	public String getTableName(){
		return tableName;
	}

	public void setTableName(String tableName){
		this.tableName = tableName;
	}

	public String getDisplayColumn(){
		return displayColumn;
	}

	public void setDisplayColumn(String displayColumn){
		this.displayColumn = displayColumn;
	}

	public int getVersion(){
		return version;
	}

	public void setVersion(int version){
		this.version = version;
	}

	@Override
	public String toString(){
		return label;
	}
}
