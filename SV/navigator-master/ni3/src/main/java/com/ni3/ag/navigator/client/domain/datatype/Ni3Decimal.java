/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain.datatype;

import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.NumberFormatter;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.gui.util.DoubleVerifier;
import org.apache.log4j.Logger;

public class Ni3Decimal extends Ni3Datatype{

	private final static Logger log = Logger.getLogger(Ni3Decimal.class);

	public Ni3Decimal(Attribute attr){
		super(attr);
	}

	@Override
	public AbstractFormatter getDefaultDisplayFormatter(){
		return createDisplayFormatter("#,##0.00");
	}

	@Override
	public AbstractFormatter createDisplayFormatter(String format){
		NumberFormatter formatter = new NumberFormatter(new DecimalFormat(format));
		formatter.setValueClass(Double.class);

		return formatter;
	}

	@Override
	public AbstractFormatter getDefaultEditFormatter(){
		return createEditFormatter("#,##0.00");
	}

	@Override
	public AbstractFormatter createEditFormatter(String format){
		NumberFormatter formatter = new NumberFormatter(new DecimalFormat(format));
		formatter.setValueClass(Double.class);
		if (attr.minVal instanceof Double){
			formatter.setMinimum((Double) attr.minVal);
		}
		if (attr.maxVal instanceof Double){
			formatter.setMaximum((Double) attr.maxVal);
		}

		return formatter;
	}

	@Override
	public String displayValue(Object val, boolean HTML){
		return formatValue(val, attr.formatFactory.getDisplayFormatter());
	}

	@Override
	public Object getValue(String val){
		if(val.isEmpty() || val.equals("null"))
			return null;
		try{
			return Double.valueOf(val);
		} catch (Exception e){
			log.warn("can not decode decimal: `" + val + "`");
		}

		return null;
	}

	@Override
	public String editValue(Object val){
		return formatValue(val, attr.formatFactory.getEditFormatter());
	}

	private String formatValue(Object val, AbstractFormatter formatter){
		if (val == null)
			return "";

		if (formatter != null){
			try{
				return formatter.valueToString(val);
			} catch (ParseException e){
				return "0";
			}
		} else
			return val.toString();
	}

	@Override
	public int compare(Object o1, Object o2){
		if (o1 == null && o2 == null)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;

		double d1 = (Double) o1;
		double d2 = (Double) o2;

		if (d1 > d2)
			return 1;
		if (d1 < d2)
			return -1;

		return 0;
	}

	@Override
	public InputVerifier getInputVerifier(){
		if (attr.minVal instanceof Double || attr.maxVal instanceof Double)
			return new DoubleVerifier((Double) attr.minVal, (Double) attr.maxVal);

		return null;
	}

	@Override
	public String getTransferString(Object val){
		if (val == null)
			return "";

		return val.toString();
	}

	@Override
	public boolean checkValue(Object o){
		return true;
	}
}
