/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.util;

import java.text.ParseException;

import javax.swing.text.MaskFormatter;

@SuppressWarnings("serial")
public class AllowBlankMaskFormatter extends MaskFormatter{

	private boolean allowBlankField = true;
	private String blankRepresentation;

	public AllowBlankMaskFormatter(){
		super();
	}

	public AllowBlankMaskFormatter(String mask) throws ParseException{
		super(mask);
	}

	public void setAllowBlankField(boolean allowBlankField){
		this.allowBlankField = allowBlankField;
	}

	public boolean isAllowBlankField(){
		return allowBlankField;
	}

	/**
	 * Update our blank representation whenever the mask is updated.
	 */
	@Override
	public void setMask(String mask) throws ParseException{
		super.setMask(mask);
		updateBlankRepresentation();
	}

	/**
	 * Update our blank representation whenever the mask is updated.
	 */
	@Override
	public void setPlaceholderCharacter(char placeholder){
		super.setPlaceholderCharacter(placeholder);
		updateBlankRepresentation();
	}

	/**
	 * Override the stringToValue method to check the blank representation.
	 */
	@Override
	public Object stringToValue(String value) throws ParseException{
		Object result = value;
		if (isAllowBlankField() && blankRepresentation != null && blankRepresentation.equals(value)){
			// an empty field should have a 'null' value.
			result = null;
		} else{
			result = super.stringToValue(value);
		}
		return result;
	}

	private void updateBlankRepresentation(){
		try{
			// calling valueToString on the parent class with a null attribute
			// will get the 'blank'
			// representation.
			blankRepresentation = valueToString(null);
		} catch (ParseException e){
			blankRepresentation = null;
		}
	}
}