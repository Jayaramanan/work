package com.ni3.ag.licensecreator.actions;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class AdminCountFieldDocFilter extends DocumentFilter{

	@Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException{
		StringBuilder dest = new StringBuilder();
		for(int i = 0; i < string.length(); i++)
			if(Character.isDigit(string.charAt(i)))
				dest.append(string.charAt(i));
	    super.insertString(fb, offset, dest.toString(), attr);
    }

	@Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException{
		StringBuilder dest = new StringBuilder();
		for(int i = 0; i < text.length(); i++)
			if(Character.isDigit(text.charAt(i)))
				dest.append(text.charAt(i));
	    super.replace(fb, offset, length, dest.toString(), attrs);
    }

}
