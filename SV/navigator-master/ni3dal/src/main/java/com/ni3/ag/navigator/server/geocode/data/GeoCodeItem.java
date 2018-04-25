package com.ni3.ag.navigator.server.geocode.data;

public class GeoCodeItem{
	private String request;
	private int id;

	public GeoCodeItem(int id, String request){
		this.id = id;
		this.request = request;
	}

	public String getRequest(){
		return request;
	}

	public int getId(){
		return id;
	}
}
