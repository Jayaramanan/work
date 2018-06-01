/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.reports;

public class RootNode{
	private String text = "";

	public RootNode(){
	}

	public RootNode(String text){
		this.text = text;
	}

	public String toString(){
		return text;
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj){
			return true;
		}
		if (text == null || !(obj instanceof RootNode)){
			return false;
		}
		return text.equals(((RootNode) obj).toString());
	}
}
