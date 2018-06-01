/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

public class ACTextArea extends JTextArea implements ChangeResetable{

	private static final Logger log = Logger.getLogger(ACTextArea.class);

	private static final long serialVersionUID = 1L;

	private static final JTextField mockTextField = new JTextField();

	private String startDocumentText;
	private String currentText;

	public ACTextArea(){
		this(null);
	}

	public ACTextArea(String text){
		super(text);
		setFont(mockTextField.getFont());
		int len = getDocument().getLength();
		try{
			startDocumentText = getDocument().getText(0, len);
			currentText = startDocumentText;
		} catch (BadLocationException e){
			log.error(e.getMessage(), e);
		}
		getDocument().addDocumentListener(new ACTextFieldDocumentListener());
	}

	public void setEditable(boolean b){
		super.setEditable(b);
		mockTextField.setEditable(b);
		setBackground(mockTextField.getBackground());
	}

	private void setFontColor(){
		if (isChanged()){
			setForeground(Color.BLUE);
		} else{
			setForeground(Color.BLACK);
		}
	}

	@Override
	public void setText(String t){
		super.setText(t);
		int len = getDocument().getLength();
		try{
			startDocumentText = getDocument().getText(0, len);
			currentText = startDocumentText;
		} catch (BadLocationException e){
			log.error(e.getMessage(), e);
		}
		setFontColor();
	}

	@Override
	public void resetChanges(){
		int len = getDocument().getLength();
		try{
			String curDocText = getDocument().getText(0, len);
			setText(curDocText);
		} catch (BadLocationException e){
			log.error(e.getMessage(), e);
		}
		setFontColor();
	}

	@Override
	public boolean isChanged(){
		int len = getDocument().getLength();
		try{
			String curDocText = getDocument().getText(0, len);
			if (!startDocumentText.equals(curDocText))
				return true;
		} catch (BadLocationException e){
			log.error(e.getMessage(), e);
		}
		return false;
	}

	public void updateText(){
		startDocumentText = getText();
	}

	private class ACTextFieldDocumentListener implements DocumentListener{

		private void updateCurrentValue(){
			currentText = getText();
			setFontColor();
			Logger.getLogger(getClass()).debug("current value: " + currentText);
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
}
