/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class NumberDocumentFilter extends DocumentFilter{

	String filterLetters(String string){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < string.length(); i++){
			if (Character.isDigit(string.charAt(i)))
				sb.append(string.charAt(i));
		}
		return sb.toString();
	}

	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException{
		String s = filterLetters(string);
		super.insertString(fb, offset, s, attr);
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
	        throws BadLocationException{
		String s = filterLetters(text);
		super.replace(fb, offset, length, s, attrs);
	}
}
