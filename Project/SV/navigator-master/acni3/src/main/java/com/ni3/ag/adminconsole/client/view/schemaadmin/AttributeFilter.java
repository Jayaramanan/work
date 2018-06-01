/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class AttributeFilter extends DocumentFilter{
	@Override
	public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs) throws BadLocationException{
		fb.insertString(offset, getFormattedText(offset, text), attrs);
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
	        throws BadLocationException{
		fb.replace(offset, length, getFormattedText(offset, text), attrs);
	}

	protected String getFormattedText(int offset, String text){
		if (text != null && text.length() > 0){
			char underscore = '_';

			if (offset == 0){
				while (text.length() > 0){
					if (!Character.isLetter(text.charAt(0))){
						text = text.substring(1);
						continue;
					}
					break;
				}
			}

			String newText = "";
			for (int i = 0; i < text.length(); i++){
				char c = text.charAt(i);
				if (Character.isLetterOrDigit(c) || c == underscore){
					newText += c;
				}
			}
			if (!text.equals(newText)){
				text = newText;
			}

		}
		return text;
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException{
		super.remove(fb, offset, length);
		if (offset == 0){
			Document doc = fb.getDocument();
			String text = doc.getText(0, doc.getLength());
			String formattedText = getFormattedText(0, text);
			if (!text.equals(formattedText)){
				doc.remove(0, doc.getLength());
				doc.insertString(0, getFormattedText(0, text), null);
			}
		}
	}
}
