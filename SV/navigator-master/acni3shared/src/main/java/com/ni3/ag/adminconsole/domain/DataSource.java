package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class DataSource implements Serializable{
	public static final DataSource defaultDataSource = new DataSource(-1, "defaultDataSource");
	public static final DataSource defaultPrimaryDataSource = new DataSource(-2, "defaultPrimaryDataSource");

	private Integer id;
	private String name;

	public DataSource(){
	}

	private DataSource(Integer id, String name){
		this.id = id;
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	@Override
	public String toString(){
		return name;
	}

	@Override
	public boolean equals(Object o){
		if (this == o) return true;
		if (!(o instanceof DataSource)) return false;

		DataSource that = (DataSource) o;

		if (name != null ? !name.equals(that.name) : that.name != null) return false;

		return true;
	}

	@Override
	public int hashCode(){
		return name != null ? name.hashCode() : 0;
	}
}
