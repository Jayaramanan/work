/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

public class ACRootNode{
	private String text = "";

	public ACRootNode(){
	}

	public ACRootNode(String text){
		this.text = text;
	}

	public String toString(){
		return text;
	}

	@Override
	public int hashCode(){
		return 1;
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj){
			return true;
		}
		if (text == null || !(obj instanceof ACRootNode)){
			return false;
		}
		return text.equals(((ACRootNode) obj).toString());
	}
}
