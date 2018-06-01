/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class GisTerritory implements Serializable, Comparable<GisTerritory>{

	public static final String ZIP_TERRITORY_NAME = "ZIP";
	public static final Integer ZIP_TERRITORY_ID = new Integer(4);
	public static final GisTerritory ZIP = new GisTerritory(ZIP_TERRITORY_ID, ZIP_TERRITORY_NAME, ZIP_TERRITORY_NAME);

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String territory;
	private String label;
	private Integer sort;
	private String tableName;
	private String displayColumn;
	private Integer version;

	public GisTerritory(){
		super();
	}

	public GisTerritory(Integer id, String territory, String label){
		super();
		setId(id);
		setLabel(label);
		setTerritory(territory);
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getTerritory(){
		return territory;
	}

	public void setTerritory(String territory){
		this.territory = territory;
	}

	public String getLabel(){
		return label;
	}

	public void setLabel(String label){
		this.label = label;
	}

	public Integer getSort(){
		return sort;
	}

	public void setSort(Integer sort){
		this.sort = sort;
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

	public Integer getVersion(){
		return version;
	}

	public void setVersion(Integer version){
		this.version = version;
	}

	@Override
	public boolean equals(java.lang.Object obj){
		if (this == obj){
			return true;
		}
		if (obj == null || !(obj instanceof GisTerritory)){
			return false;
		}
		if (getId() == null || ((GisTerritory) obj).getId() == null){
			return false;
		}
		return getId().intValue() == ((GisTerritory) obj).getId().intValue();
	}

	@Override
	public int compareTo(GisTerritory o){
		if (o == null)
			return -1;
		if (label == null && o.label == null)
			return 0;
		if (label == null)
			return 1;
		if (o.getLabel() == null)
			return -1;

		return label.compareTo(o.getLabel());
	}

	@Override
	public String toString(){
		return label;
	}
}
