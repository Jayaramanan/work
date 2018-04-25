/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.domain;

public class UrlOperation{
	private int id;
	private String label;
	private String url;
	private String sort;

	public UrlOperation(){
	}

	public UrlOperation(int id, String label, String url, String sort){
		this.id = id;
		this.label = label;
		this.url = url;
		this.sort = sort;
	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getLabel(){
		return label;
	}

	public void setLabel(String label){
		this.label = label;
	}

	public String getUrl(){
		return url;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getSort(){
		return sort;
	}

	public void setSort(String sort){
		this.sort = sort;
	}

}
