/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.util;

import java.awt.Color;
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

public class MaskVerifier extends InputVerifier{
	private static final Color INVALID_COLOR = Color.red;
	private static final Color VALID_COLOR = Color.black;
	private MaskFormatter mf = null;

	/**
	 * Constructor
	 * 
	 * @param mask
	 *            MaskFormatter mask for parsing
	 * @throws java.text.ParseException
	 */
	public MaskVerifier(MaskFormatter mf) throws ParseException{
		this.mf = mf;
	}

	/**
	 * Test to see if the contents of the component match the mask
	 * 
	 * @param jc
	 *            the component to test
	 * @return return true if valid, false if not
	 */
	public boolean verify(javax.swing.JComponent jc){
		JFormattedTextField text = (JFormattedTextField) jc;

		try{
			mf.stringToValue(text.getText());
			jc.setForeground(VALID_COLOR);
			return true;
		} catch (Exception e){
			if (text.getText().length() > 0
			        && (!(text.getFormatter() instanceof MaskFormatter) || !(((MaskFormatter) (text.getFormatter()))
			                .getMask().equals(text.getText())))){
				jc.setForeground(INVALID_COLOR);
				return false;
			}

			return true;
		}

	}
}
