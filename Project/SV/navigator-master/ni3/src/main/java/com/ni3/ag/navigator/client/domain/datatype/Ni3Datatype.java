/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain.datatype;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;

import com.ni3.ag.navigator.client.domain.Attribute;

public abstract class Ni3Datatype{
	protected Attribute attr;

	public Ni3Datatype(Attribute attr){
		this.attr = attr;
	}

	abstract public AbstractFormatter getDefaultDisplayFormatter();

	abstract public AbstractFormatter createDisplayFormatter(String format);

	abstract public AbstractFormatter getDefaultEditFormatter();

	abstract public AbstractFormatter createEditFormatter(String format);

	abstract public InputVerifier getInputVerifier();

	abstract public String displayValue(Object val, boolean HTML);

	abstract public String editValue(Object val);

	abstract public Object getValue(String val);

	abstract public String getTransferString(Object val);

	abstract public boolean checkValue(Object o);

	abstract public int compare(Object o1, Object o2);

	public static Ni3Datatype createDatatype(Attribute attr){
		switch (attr.getDType()){
			case TEXT:
				return new Ni3String(attr);

			case INT:
				return new Ni3Integer(attr);

			case BOOL:
				return new Ni3Boolean(attr);

			case DECIMAL:
				return new Ni3Decimal(attr);

			case URL:
				return new Ni3URL(attr);

			case DATE:
				return new Ni3Date(attr);

			default:
				return new Ni3String(attr);
		}
	}
}
