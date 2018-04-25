package com.ni3.ag.navigator.server.search;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.User;

public class Criteria{
	private User user;
	private int schema;
	private List<Integer> filteredValues;
	private int limit;

	public void setUser(User user){
		this.user = user;
	}

	public void setSchema(int schema){
		this.schema = schema;
	}

	public void setLimit(int limit){
		this.limit = limit;
	}

	public User getUser(){
		return user;
	}

	public int getSchema(){
		return schema;
	}

	public int getLimit(){
		return limit;
	}

	public List<Integer> getFilteredValues(){
		return filteredValues;
	}

	public void setFilteredValues(List<Integer> filteredValues){
		this.filteredValues = filteredValues;
	}
}
