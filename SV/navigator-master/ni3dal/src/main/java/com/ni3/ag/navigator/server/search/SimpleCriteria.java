package com.ni3.ag.navigator.server.search;

public class SimpleCriteria extends Criteria{
	private String term;

	public void setTerm(String term){
		this.term = term;
	}

	public String getTerm(){
		return term;
	}
}
