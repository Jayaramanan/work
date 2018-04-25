/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain.query;

public class Operation{
	public String label;
	public String operation;

	public Operation(String operation){
		this.label = operation;
		this.operation = operation;
	}

	public Operation(String label, String operation){
		this.label = label;
		this.operation = operation;
	}

	public String toString(){
		return label;
	}
}
