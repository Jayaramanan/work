/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain.datatype;

import com.ni3.ag.navigator.client.domain.Attribute;

public class Ni3URL extends Ni3String{
	public Ni3URL(Attribute attr){
		super(attr);
	}

	@Override
	public String displayValue(Object val, boolean HTML){
		if (val == null)
			return "";

		if (HTML)
			return "<A href='" + val + "'>" + val + "</A>";
		else
			return val.toString();
	}

	@Override
	public String editValue(Object val){
		if (val == null)
			return "";

		return (String) val;
	}
}
