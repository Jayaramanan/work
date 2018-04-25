/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;

@SuppressWarnings("serial")
public class ACAutoCompleteTextArea extends RSyntaxTextArea implements ChangeResetable, DocumentListener{
	private static final Logger log = Logger.getLogger(ACAutoCompleteTextArea.class);
	private static final JTextField mockTextField = new JTextField();
	private String startDocumentText;

	private String triggerChars;
	private boolean inited;
	private AutoCompletion autoCompletion;

	public ACAutoCompleteTextArea(String triggerChars){
		if (triggerChars == null)
			triggerChars = "";
		this.triggerChars = triggerChars;
		setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY);
		autoCompletion = new AutoCompletion(createDefaultProvider());
		autoCompletion.install(this);
		setFont(this, mockTextField.getFont());
		getDocument().addDocumentListener(this);
		startDocumentText = getText();
		inited = true;
		addKeyListener(new ACAutoCompleteTextAreaKeyListener());
		autoCompletion.setListCellRenderer(new AutoCompleteListCellRenderer(mockTextField.getFont()));
	}

	public void setFont(RSyntaxTextArea textArea, Font font){
		if (font != null){
			SyntaxScheme ss = textArea.getSyntaxScheme();
			ss = (SyntaxScheme) ss.clone();
			for (int i = 0; i < ss.styles.length; i++){
				if (ss.styles[i] != null){
					ss.styles[i].font = font;
				}
			}
			textArea.setSyntaxScheme(ss);
			textArea.setFont(font);
		}
	}

	private CompletionProvider createDefaultProvider(){
		DefaultCompletionProvider provider = new DefaultCompletionProvider();
		return provider;
	}

	@Override
	public void setEditable(boolean b){
		super.setEditable(b);
		mockTextField.setEditable(b);
		setBackground(mockTextField.getBackground());
		if (inited)
			setHighlightCurrentLine(b);
	}

	private void setBorderColor(){
		JComponent parent = (JComponent) getParent();
		if (!(parent instanceof JScrollPane))
			parent = this;
		if (isChanged()){
			parent.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		} else{
			parent.setBorder(null);
		}
	}

	@Override
	public void setText(String t){
		super.setText(t);
		resetChanges();
	}

	@Override
	public void resetChanges(){
		startDocumentText = getText();
		setBorderColor();
	}

	@Override
	public boolean isChanged(){
		String curDocText = getText();
		if ((curDocText == null && startDocumentText != null) || (curDocText != null && startDocumentText == null))
			return true;
		if (startDocumentText != null && !startDocumentText.equals(curDocText))
			return true;
		return false;
	}

	@Override
	public void changedUpdate(DocumentEvent e){
		processDocumentChange(e);
	}

	@Override
	public void insertUpdate(DocumentEvent e){
		processDocumentChange(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e){
		processDocumentChange(e);
	}

	private void processDocumentChange(DocumentEvent e){
		setBorderColor();
	}

	private boolean isTriggerChar(char ch){
		return triggerChars.indexOf(ch) != -1;
	}

	public void setAutoCompleteItems(List<ObjectAttribute> attrs){
		log.debug("setAutoCompleteItems: " + attrs);
		if (attrs == null || attrs.isEmpty()){
			autoCompletion.setCompletionProvider(createDefaultProvider());
			return;
		}
		DefaultCompletionProvider provider = new DefaultCompletionProvider();
		for (ObjectAttribute oa : attrs)
			provider.addCompletion(createVariable(provider, oa));
		autoCompletion.setCompletionProvider(provider);
	}

	public void setAutoCompleteItems(Map<String, String> vars){
		log.debug("setAutoCompleteItems: " + vars);
		if (vars == null || vars.isEmpty()){
			autoCompletion.setCompletionProvider(createDefaultProvider());
			return;
		}
		DefaultCompletionProvider provider = new DefaultCompletionProvider();
		for (String name : vars.keySet())
			provider.addCompletion(createVariable(provider, name, vars.get(name)));
		autoCompletion.setCompletionProvider(provider);
	}

	private Completion createVariable(DefaultCompletionProvider provider, String name, String type){
		VariableCompletion var = new VariableCompletion(provider, name, type);
		return var;
	}

	private Completion createVariable(CompletionProvider provider, ObjectAttribute oa){
		VariableCompletion var = new VariableCompletion(provider, oa.getName(), oa.getDataType().getTextId().getKey());
		return var;
	}

	private class ACAutoCompleteTextAreaKeyListener implements KeyListener{

		private boolean isDuplicateTrigger(){
			int index = getCaretPosition() - 1;
			if (index < 0)
				return false;
			String str = getText();
			if (isTriggerChar(str.charAt(index)))
				return true;
			return false;
		}

		@Override
		public void keyTyped(KeyEvent e){
			if (isTriggerChar(e.getKeyChar()) && !isDuplicateTrigger())
				autoCompletion.doCompletion();
		}

		@Override
		public void keyPressed(KeyEvent e){
		}

		@Override
		public void keyReleased(KeyEvent e){
		}

	}
}
