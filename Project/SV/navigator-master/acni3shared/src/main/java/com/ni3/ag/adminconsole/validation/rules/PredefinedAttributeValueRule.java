/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.validation.rules;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class PredefinedAttributeValueRule implements ACValidationRule{
	private static final Logger log = Logger.getLogger(PredefinedAttributeValueRule.class);
	private List<ErrorEntry> errors;

	PredefinedAttributeValueRule(){
	}

	@Override
	public boolean performCheck(AbstractModel model){

		errors = new ArrayList<ErrorEntry>();
		PredefinedAttributeEditModel mdl = (PredefinedAttributeEditModel) model;

		if (mdl.getCurrentAttribute() != null){
			List<PredefinedAttribute> predefinedAttributes = mdl.getCurrentAttribute().getPredefinedAttributes();
			final DataType dt = mdl.getCurrentAttribute().getDataType();
			ErrorEntry error = null;
			switch (dt){
				case INT:
					error = checkIntValues(predefinedAttributes);
					break;
				case DECIMAL:
					error = checkDecimalValues(predefinedAttributes);
					break;
				case DATE:
					error = checkDateValues(predefinedAttributes, mdl.getDateFormat());
					break;
			}

			if (error != null){
				errors.add(error);
			}
		}
		return errors.isEmpty();
	}

	ErrorEntry checkIntValues(List<PredefinedAttribute> predefinedAttributes){
		ErrorEntry result = null;
		List<String> incorrectList = new ArrayList<String>();
		for (PredefinedAttribute pa : predefinedAttributes){
			String value = pa.getValue();
			try{
				Integer.valueOf(value);
			} catch (NumberFormatException ex){
				incorrectList.add(value);
				log.warn("Cannot parse int value: " + value);
			}
		}
		if (!incorrectList.isEmpty()){
			result = new ErrorEntry(TextID.MsgPredefinedValuesNotInt, new String[] { listToString(incorrectList) });
		}
		return result;
	}

	ErrorEntry checkDecimalValues(List<PredefinedAttribute> predefinedAttributes){
		ErrorEntry result = null;
		List<String> incorrectList = new ArrayList<String>();
		for (PredefinedAttribute pa : predefinedAttributes){
			String value = pa.getValue();
			try{
				Double.valueOf(value);
			} catch (NumberFormatException ex){
				incorrectList.add(value);
				log.warn("Cannot parse double value: " + value);
			}
		}
		if (!incorrectList.isEmpty()){
			result = new ErrorEntry(TextID.MsgPredefinedValuesNotDecimal, new String[] { listToString(incorrectList) });
		}
		return result;
	}

	ErrorEntry checkDateValues(List<PredefinedAttribute> predefinedAttributes, String format){
		String dFormat = (format == null || format.isEmpty()) ? DataType.DISPLAY_DATE_FORMAT : format;
		DateFormat dateFormat = new SimpleDateFormat(dFormat);
		ErrorEntry result = null;
		List<String> incorrectList = new ArrayList<String>();
		for (PredefinedAttribute pa : predefinedAttributes){
			String value = pa.getValue();
			try{
				dateFormat.parse(value);
			} catch (ParseException e){
				incorrectList.add(value);
				log.warn("Cannot parse date value: " + value);
			}
		}
		if (!incorrectList.isEmpty()){
			result = new ErrorEntry(TextID.MsgPredefinedValuesNotDate, new String[] { dFormat, listToString(incorrectList) });
		}
		return result;
	}

	public static String listToString(List<?> list){
		StringBuilder sb = new StringBuilder();
		if (list != null){
			for (int i = 0; i < list.size(); i++){
				if (i > 0)
					sb.append(", ");
				sb.append(list.get(i));
			}
		}
		return sb.toString();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}
}
