package com.ni3.ag.navigator.server.domain;

public class SysIam{
	/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
	private int id;
	private String version;
	private String name;

	public SysIam(){
	}

	public SysIam(int id, String name){
		this.id = id;
		this.name = name;
	}

	public int getId(){
		return this.id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getVersion(){
		return this.version;
	}

	public void setVersion(String version){
		this.version = version;
	}

	public String getName(){
		return this.name;
	}

	public void setName(String name){
		this.name = name;
	}

}
