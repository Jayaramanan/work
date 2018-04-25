package com.ni3.ag.navigator.server.geocode.data;

public class GeoCodeError{
	private int status;
	private String previousAddressRequest;

	public GeoCodeError(int status, String previousAddressRequest){
		this.status = status;
		this.previousAddressRequest = previousAddressRequest;
	}

	public int getStatus(){
		return status;
	}

	public String getPreviousAddressRequest(){
		return previousAddressRequest;
	}
}
