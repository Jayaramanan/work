/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.util;

import java.awt.Color;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

public class StringVerifier extends InputVerifier{
	private static final Color INVALID_COLOR = Color.red;
	private static final Color VALID_COLOR = Color.black;
	private String validCharacters;
	private String invalidCharacters;

	public StringVerifier(String validCharacters, String invalidCharacters){
		this.validCharacters = validCharacters;
		this.invalidCharacters = invalidCharacters;
	}

	@Override
	public boolean verify(JComponent jc){
		boolean result = true;
		String text = ((JTextComponent) jc).getText();
		if (text == null || text.isEmpty() || (validCharacters == null && invalidCharacters == null)){
			result = true;
		} else{
			if (validCharacters != null && !validCharacters.isEmpty()){
				result = checkValidCharacters(text);
			}
			if (result && invalidCharacters != null && !invalidCharacters.isEmpty()){
				result = checkInvalidCharacters(text);
			}
		}

		jc.setForeground(result ? VALID_COLOR : INVALID_COLOR);

		return result;
	}

	boolean checkInvalidCharacters(String text){
		boolean result = true;
		for (int i = 0; i < invalidCharacters.length(); i++){
			char ch = invalidCharacters.charAt(i);
			if (text.indexOf(ch) >= 0){
				result = false;
				break;
			}
		}
		return result;
	}

	boolean checkValidCharacters(String text){
		boolean result = true;
		for (int i = 0; i < text.length(); i++){
			char ch = text.charAt(i);
			if (validCharacters.indexOf(ch) < 0){
				result = false;
				break;
			}
		}
		return result;
	}
}
