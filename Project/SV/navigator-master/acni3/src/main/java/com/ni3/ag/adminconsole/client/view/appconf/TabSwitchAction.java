/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

public class TabSwitchAction{
	private String name;
	private String value;

	public TabSwitchAction(String name, String value){
		this.name = name;
		this.value = value;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setValue(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}

	@Override
	public String toString(){
		return name;
	}

}
