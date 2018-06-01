/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.apache.log4j.Logger;

public class ACTextField extends JTextField implements ChangeResetable{
	private static final long serialVersionUID = 7389576981633188900L;
	private Object startValue;
	private Object currentValue;

	public ACTextField(Document doc, String text, int columns){
		super(doc, text, columns);
		startValue = text;
		currentValue = text;
		getDocument().addDocumentListener(new ACTextFieldDocumentListener());
	}

	public ACTextField(String text, int columns){
		this(null, text, columns);
	}

	public ACTextField(String text){
		this(null, text, 0);
	}

	public ACTextField(){
		this(null, null, 0);
	}

	public ACTextField(int columns){
		this(null, null, columns);
	}

	@Override
	public void setText(String t){
		super.setText(t);
		startValue = t;
		currentValue = t;
		setFontColor();
	}

	private void setFontColor(){
		if (isChanged()){
			setForeground(Color.BLUE);
		} else{
			setForeground(Color.BLACK);
		}
	}

	private class ACTextFieldDocumentListener implements DocumentListener{

		private void updateCurrentValue(){
			currentValue = getText();
			setFontColor();
			Logger.getLogger(getClass()).debug("current value: " + currentValue);
		}

		@Override
		public void changedUpdate(DocumentEvent e){
			updateCurrentValue();
		}

		@Override
		public void insertUpdate(DocumentEvent e){
			updateCurrentValue();
		}

		@Override
		public void removeUpdate(DocumentEvent e){
			updateCurrentValue();
		}
	}

	@Override
	public void resetChanges(){
		startValue = currentValue;
		setFontColor();
	}

	@Override
	public boolean isChanged(){
		if ((startValue == null && currentValue == null) || (startValue != null && startValue.equals(currentValue))){
			return false;
		}
		return true;
	}
}
