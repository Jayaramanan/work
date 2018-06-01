/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain.datatype;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DateFormatter;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.gui.util.DateVerifier;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import org.apache.log4j.Logger;

public class Ni3Date extends Ni3String{

	private final static Logger log = Logger.getLogger(Ni3Date.class);

	GregorianCalendar calendar;
	static DateFormatter DBFormatter = new DateFormatter(new SimpleDateFormat("yyyyMMdd"));

	public Ni3Date(Attribute attr){
		super(attr);

		calendar = new GregorianCalendar();
	}

	@Override
	public AbstractFormatter createEditFormatter(String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		DateFormatter ret = new DateFormatter(sdf);
		ret.setValueClass(Date.class);

		return ret;
	}

	@Override
	public AbstractFormatter getDefaultEditFormatter(){
		return createEditFormatter(SystemGlobals.DateFormat);
	}

	@Override
	public AbstractFormatter createDisplayFormatter(String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		DateFormatter ret = new DateFormatter(sdf);
		ret.setValueClass(Date.class);

		return ret;
	}

	@Override
	public AbstractFormatter getDefaultDisplayFormatter(){
		return createDisplayFormatter(SystemGlobals.DateFormat);
	}

	@Override
	public String displayValue(Object val, boolean HTML){
		try{
			return attr.formatFactory.getDisplayFormatter().valueToString(val);
		} catch (ParseException e){
			log.error(e.getMessage(), e);
		}

		return "";
	}

	@Override
	public Object getValue(String val){
		int y, m, d;

		if (val.length() != 8)
			return null;

		y = Integer.valueOf(val.substring(0, 4));
		m = Integer.valueOf(val.substring(4, 6));
		d = Integer.valueOf(val.substring(6, 8));

		calendar.set(y, m - 1, d);
		return calendar.getTime();
	}

	@Override
	public String editValue(Object val){
		try{
			return attr.formatFactory.getEditFormatter().valueToString(val);
		} catch (ParseException e){
			log.error(e.getMessage(), e);
		}

		return "";
	}

	@Override
	public InputVerifier getInputVerifier(){
		if (attr.formatFactory != null){
			return new DateVerifier((DateFormatter) (attr.formatFactory.getEditFormatter()));
		}

		return null;
	}

	@Override
	public String getTransferString(Object val){
		if (val == null)
			return "";
		try{
			return DBFormatter.valueToString(val);
		} catch (ParseException e){
			log.error(e.getMessage(), e);
		}

		return "";
	}

	@Override
	public int compare(Object o1, Object o2){
		if (o1 == null && o2 == null)
			return 0;

		if (o1 == null)
			return 1;

		if (o2 == null)
			return -1;

		return ((Date) o1).compareTo((Date) o2);
	}
}
