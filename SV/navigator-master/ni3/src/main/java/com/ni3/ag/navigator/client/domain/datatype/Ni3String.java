/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain.datatype;

import java.text.ParseException;
import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.util.AllowBlankMaskFormatter;
import com.ni3.ag.navigator.client.gui.util.MaskVerifier;
import com.ni3.ag.navigator.client.gui.util.StringVerifier;
import org.apache.log4j.Logger;

public class Ni3String extends Ni3Datatype{

	private final static Logger log = Logger.getLogger(Ni3String.class);

	public Ni3String(Attribute attr){
		super(attr);
	}

	@Override
	public AbstractFormatter getDefaultEditFormatter(){
		return null;
	}

	@Override
	public AbstractFormatter createEditFormatter(String format){
		AllowBlankMaskFormatter formatter = null;
		try{
			formatter = new AllowBlankMaskFormatter(format);
			formatter.setPlaceholderCharacter('_');

			if (!attr.validchars.isEmpty())
				formatter.setValidCharacters(attr.validchars);

			if (!attr.invalidchars.isEmpty())
				formatter.setInvalidCharacters(attr.invalidchars);

			formatter.setValueClass(String.class);
		} catch (ParseException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return formatter;
	}

	@Override
	public AbstractFormatter getDefaultDisplayFormatter(){
		return null;
	}

	@Override
	public AbstractFormatter createDisplayFormatter(String format){
		AllowBlankMaskFormatter formatter = null;
		try{
			formatter = new AllowBlankMaskFormatter(format);
			formatter.setPlaceholderCharacter('_');

			if (!attr.validchars.isEmpty())
				formatter.setValidCharacters(attr.validchars);

			if (!attr.invalidchars.isEmpty())
				formatter.setInvalidCharacters(attr.invalidchars);
		} catch (ParseException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return formatter;
	}

	@Override
	public String displayValue(Object val, boolean HTML){
		String result = "";
		if (val != null){
			if (attr.formatFactory != null){
				result = formatValue(val, attr.formatFactory.getDisplayFormatter());
			} else{
				result = val.toString();
			}
		}
		return result;
	}

	@Override
	public Object getValue(String val){
		return val;
	}

	private String formatValue(Object val, AbstractFormatter formatter){
		if (formatter == null){
			if (val == null)
				return "";

			return (String) val;
		}

		try{
			return formatter.valueToString(val);
		} catch (ParseException e){
			log.warn("can not format value: `" + val + "`");
		}

		return "";
	}

	@Override
	public String editValue(Object val){
		if (val == null)
			return "";

		if (attr.formatFactory != null)
			return formatValue(val, attr.formatFactory.getEditFormatter());

		return val.toString();
	}

	@Override
	public int compare(Object o1, Object o2){
		if (o1 == null && o2 == null)
			return 0;

		if (o1 != null && o2 == null)
			return 1;

		if (o1 == null && o2 != null)
			return -1;

		return UserSettings.getCollator().compare((String) o1, (String) o2);
	}

	@Override
	public InputVerifier getInputVerifier(){
		if (attr.formatFactory != null){
			try{
				return new MaskVerifier((AllowBlankMaskFormatter) (attr.formatFactory.getEditFormatter()));
			} catch (ParseException e){
				log.error(e.getMessage(), e);
			}
		} else if ((attr.validchars != null && !attr.validchars.isEmpty())
				|| (attr.invalidchars != null && !attr.invalidchars.isEmpty())){
			return new StringVerifier(attr.validchars, attr.invalidchars);
		}

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
