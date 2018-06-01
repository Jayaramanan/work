/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class ObjectNameDocumentFilter extends DocumentFilter{

	private static final String NAME_PATTERN = "[^a-zA-Z0-9 -]*";

	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException{
		String test = string.replaceAll(NAME_PATTERN, "");
		super.insertString(fb, offset, test, attr);
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
	        throws BadLocationException{
		String test = text.replaceAll(NAME_PATTERN, "");
		super.replace(fb, offset, length, test, attrs);
	}
}
