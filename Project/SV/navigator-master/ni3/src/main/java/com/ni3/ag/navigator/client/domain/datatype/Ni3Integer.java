/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain.datatype;

import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.NumberFormatter;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.gui.util.IntegerVerifier;
import org.apache.log4j.Logger;

public class Ni3Integer extends Ni3Datatype{

	private final static Logger log = Logger.getLogger(Ni3Integer.class);

	public Ni3Integer(final Attribute attr){
		super(attr);
	}

	@Override
	public boolean checkValue(final Object o){
		return true;
	}

	@Override
	public int compare(final Object o1, final Object o2){
		if (o1 == null && o2 == null)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;

		int i1 = (Integer) o1;
		int i2 = (Integer) o2;

		if (i1 > i2){
			return 1;
		}
		if (i1 < i2){
			return -1;
		}

		return 0;
	}

	@Override
	public AbstractFormatter createDisplayFormatter(final String format){
		final NumberFormatter formatter = new NumberFormatter(new DecimalFormat(format));
		formatter.setValueClass(Integer.class);
		return formatter;
	}

	@Override
	public AbstractFormatter createEditFormatter(final String format){
		DecimalFormat df = new DecimalFormat(format);
		df.setParseIntegerOnly(false);
		final NumberFormatter formatter = new NumberFormatter(df);
		formatter.setValueClass(Integer.class);
		if (attr.minVal instanceof Integer){
			formatter.setMinimum((Integer) attr.minVal);
		}
		if (attr.maxVal instanceof Integer){
			formatter.setMaximum((Integer) attr.maxVal);
		}
		return formatter;
	}

	@Override
	public String displayValue(final Object val, final boolean HTML){
		return formatValue(val, attr.formatFactory.getDisplayFormatter());
	}

	@Override
	public String editValue(final Object val){
		return formatValue(val, attr.formatFactory.getEditFormatter());
	}

	public String formatValue(final Object val, final AbstractFormatter formatter){
		if (val == null){
			return "";
		}

		if (formatter != null){
			try{
				return formatter.valueToString(val);
			} catch (final ParseException e){
				return "0";
			}
		} else{
			return val.toString();
		}
	}

	@Override
	public AbstractFormatter getDefaultDisplayFormatter(){
		return createDisplayFormatter("#,###");
	}

	@Override
	public AbstractFormatter getDefaultEditFormatter(){
		return createEditFormatter("#,###");
	}

	@Override
	public InputVerifier getInputVerifier(){
		if (attr.minVal instanceof Integer || attr.maxVal instanceof Integer){
			return new IntegerVerifier((Integer) attr.minVal, (Integer) attr.maxVal);
		} else
			return new IntegerVerifier(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public String getTransferString(final Object val){
		if (val == null){
			return "";
		}

		return val.toString();
	}

	@Override
	public Object getValue(final String val){
		if(val.isEmpty() || val.equals("null"))
			return null;
		try{
			return Integer.decode(val);
		} catch (final Exception e){
			log.warn("can not decode integer: `" + val + "`");
		}

		return null;
	}
}
