/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.util;

import java.awt.Color;

import javax.swing.InputVerifier;
import javax.swing.JFormattedTextField;
import javax.swing.text.DateFormatter;

public class DateVerifier extends InputVerifier{
	private static final Color INVALID_COLOR = Color.red;
	private static final Color VALID_COLOR = Color.black;

	DateFormatter sdf = null; // formatted used to check date formats

	/**
	 * Default constructor
	 */
	public DateVerifier(){
		sdf = null;
	}

	/**
	 * Constructor that accepts a mask
	 * 
	 * @param mask
	 *            SimpleDateFormat mask
	 */

	public DateVerifier(DateFormatter sdf){
		this.sdf = sdf;
	}

	/**
	 * Check the contents to see if its a valid date
	 * 
	 * @param jc
	 *            JComponent (the date field)
	 * @return true if valid date, false if not
	 */
	public boolean verify(javax.swing.JComponent jc){
		JFormattedTextField fdf = (JFormattedTextField) jc;
		try{
			String txt = fdf.getText();

			if (txt == null || txt.length() == 0)
				return true;

			sdf.stringToValue(txt); // note this allows months > 12,
			// days > 31
			jc.setForeground(VALID_COLOR);
			return true;
		} catch (Exception e){
			jc.setForeground(INVALID_COLOR);
			return false;
		}

	}

}
