/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.util;

import java.awt.Color;

import javax.swing.InputVerifier;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

/**
 * 
 * @author Mark Pendergast Copyright Mark Pendergast
 */
public class DoubleVerifier extends InputVerifier{
	protected double minValue = -Double.MAX_VALUE;
	protected double maxValue = Double.MAX_VALUE;
	protected static final Color INVALID_COLOR = Color.red;
	protected static final Color VALID_COLOR = Color.black;

	/**
	 * Creates an Verifier object that makes sure the text can be parsed into a double between MIN_VALUE and MAX_VALUE
	 */
	public DoubleVerifier(){

	}

	/**
	 * Creates an Verifier object that makes sure the text can be parsed into a double between min and max
	 * 
	 * @param min
	 *            lowest valid value
	 * @param max
	 *            highest valid value
	 * @throws java.lang.IllegalArgumentException
	 */

	public DoubleVerifier(Double min, Double max) throws IllegalArgumentException{
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
	 * verifies the value in the component can be parsed to a double between minValue and maxValue
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
			double val = (Double) nf.stringToValue(text);
			if (val < minValue || val > maxValue){
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
	public void setMinValue(double value) throws IllegalArgumentException{
		if (value > maxValue)
			throw new IllegalArgumentException("value must be less than maxvalue");
		minValue = value;
	}

	/**
	 * Mutator method for maxValue, maxValue is used to set the upper range of valid numbers.
	 * 
	 * @param value
	 *            new maximum value
	 * @throws java.lang.IllegalArgumentException
	 */

	public void setMaxValue(double value) throws IllegalArgumentException{
		if (value < minValue)
			throw new IllegalArgumentException("value must be greater than minvalue");
		maxValue = value;
	}

}
