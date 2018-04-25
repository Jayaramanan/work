/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.util;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.ParsePosition;

import javax.swing.InputVerifier;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

/**
 * 
 * @author Mark Pendergast Copyright Mark Pendergast
 */
public class IntegerVerifier extends InputVerifier{
	private int minValue = Integer.MIN_VALUE;
	private int maxValue = Integer.MAX_VALUE;
	private static final Color INVALID_COLOR = Color.red;
	private static final Color VALID_COLOR = Color.black;

	/**
	 * Creates an Verifier object that makes sure the text can be parsed into an interger between MIN_VALUE and
	 * MAX_VALUE
	 */
	public IntegerVerifier(){
	}

	/**
	 * Creates an Verifier object that makes sure the text can be parsed into an interger between min and max
	 * 
	 * @param min
	 *            lowest valid value
	 * @param max
	 *            highest valid value
	 * @throws java.lang.IllegalArgumentException
	 */
	public IntegerVerifier(Integer min, Integer max) throws IllegalArgumentException{
		if (min != null){
			minValue = min;
		}
		if (max != null){
			maxValue = max;
		}
		if (minValue > maxValue)
			throw new IllegalArgumentException("min value must be less than max value");
	}

	/**
	 * verifies the value in the component can be parsed to an integer between minValue and maxValue
	 * 
	 * @param jc
	 *            a JTextComponent
	 * @return returns false if the value is not valid
	 */
	public boolean verify(javax.swing.JComponent jc){
		try{
			JFormattedTextField field = ((JFormattedTextField) jc);
			String text = field.getText();

			if (text == null || text.trim().length() == 0)
				return true;

			NumberFormatter nf = (NumberFormatter) field.getFormatter();
			DecimalFormat df = (DecimalFormat) nf.getFormat();
			ParsePosition pp = new ParsePosition(0);
			df.parse(text, pp);
			if (pp.getIndex() != text.length()){
				jc.setForeground(INVALID_COLOR);
				return false;
			}
			Integer value = (Integer) nf.stringToValue(text);
			if (value.compareTo(Integer.valueOf(minValue)) == -1 || value.compareTo(Integer.valueOf(maxValue)) == 1){
				jc.setForeground(INVALID_COLOR);
				return false;
			}
		} catch (Exception e){
			jc.setForeground(INVALID_COLOR);
			return false;
		}
		jc.setForeground(VALID_COLOR);
		return true;
	}

	/**
	 * Mutator method for minValue, minValue is used to set the lower range of valid numbers.
	 * 
	 * @param value
	 * @throws java.lang.IllegalArgumentException
	 */
	public void setMinValue(int value) throws IllegalArgumentException{
		if (value > maxValue)
			throw new IllegalArgumentException("Value must be less than maxvalue");
		minValue = value;
	}

	/**
	 * Mutator method for maxValue, maxValue is used to set the upper range of valid numbers.
	 * 
	 * @param value
	 *            new maximum value
	 * @throws java.lang.IllegalArgumentException
	 */
	public void setMaxValue(int value) throws IllegalArgumentException{
		if (value < minValue)
			throw new IllegalArgumentException("Value must be greater than minvalue");
		maxValue = value;
	}

}
