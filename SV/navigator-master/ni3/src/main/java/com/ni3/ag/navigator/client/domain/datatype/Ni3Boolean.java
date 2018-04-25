/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain.datatype;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.util.Utility;

public class Ni3Boolean extends Ni3Datatype{
	public Ni3Boolean(Attribute attr){
		super(attr);
	}

	@Override
	public AbstractFormatter createDisplayFormatter(String format){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractFormatter createEditFormatter(String format){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractFormatter getDefaultDisplayFormatter(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractFormatter getDefaultEditFormatter(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String displayValue(Object val, boolean HTML){
		if (val == null)
			return "";
		return val.toString();
	}

	@Override
	public Object getValue(String val){
		return Utility.processBooleanString(val);
	}

	@Override
	public String editValue(Object val){
		return val != null && val instanceof Boolean ? val.toString() : Boolean.FALSE.toString();
	}

	@Override
	public int compare(Object o1, Object o2){
		if (o1 == null && o2 == null)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;
		boolean b1 = (Boolean) o1;
		boolean b2 = (Boolean) o2;

		if (b1 == b2)
			return 0;

		if (b1 && !b2)
			return 1;

		return -1;
	}

	@Override
	public InputVerifier getInputVerifier(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTransferString(Object val){
		boolean value = false;
		if (val instanceof Boolean){
			value = (Boolean) val;
		} else if (val instanceof String){
			value = Boolean.valueOf((String) val);
		}

		return value ? "1" : "0";
	}

	@Override
	public boolean checkValue(Object o){
		return true;
	}
}
