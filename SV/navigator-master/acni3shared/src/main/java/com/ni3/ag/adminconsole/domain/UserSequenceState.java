/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class UserSequenceState implements Serializable{
	private static final long serialVersionUID = 5346550400588320988L;
	private int rangeStart;
	private int rangeEnd;
	private int current;
	private String name;

	public void setRangeStart(int start){
		rangeStart = start;
	}

	public void setRangeEnd(int end){
		rangeEnd = end;
	}

	public void setCurrent(int current){
		this.current = current;
	}

	public void setSequenceName(String name){
		this.name = name;
	}

	public String getSequenceName(){
		return name;
	}

	public int getIntervalStart(){
		return rangeStart;
	}

	public int getIntervalEnd(){
		return rangeEnd;
	}

	public int getCurrent(){
		return current;
	}
}
