/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3FileChooser;
import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.shared.util.StringTokenizerEx;

@SuppressWarnings("serial")
public class JMultivalueTextField extends JPanel{

	JTextArea text;
	JScrollPane scroll;

	JPanel controls;

	JButton plus, minus, browse;

	public JMultivalueTextField(boolean addBrowseButton){
		super();

		setLayout(new BorderLayout());

		text = new JTextArea();

		text.setColumns(0);
		text.setRows(0);

		scroll = new JScrollPane(text);
		add(scroll, BorderLayout.CENTER);

		controls = new JPanel();

		if (addBrowseButton){
			browse = new JButton("...");
			browse.addActionListener(new java.awt.event.ActionListener(){
				@SuppressWarnings("deprecation")
				public void actionPerformed(java.awt.event.ActionEvent evt){
					Ni3FileChooser jfc = new Ni3FileChooser(UserSettings.getWord("Add files as URL"));
					jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int returnVal = jfc.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION){
						File f = jfc.getSelectedFile();
						try{
							insertLine(f.toURL().toString());
						} catch (MalformedURLException e){
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			});
		}

		controls.setLayout(new GridLayout(1, 1));

		if (addBrowseButton)
			controls.add(browse);

		add(controls, BorderLayout.EAST);
	}

	@Override
	public void setName(String name){
		// For marathon testing
		text.setName(name);
	}

	public int getItemsCount(){
		String[] items = getItems();
		return items != null ? items.length : 0;
	}

	public void insertLine(String newLineText){
		int pos = text.getCaretPosition();
		int line, end = 0;
		try{
			line = text.getLineOfOffset(pos);
			end = text.getLineEndOffset(line);
			text.insert("\n" + newLineText, end);
			text.setCaretPosition(end);
			text.requestFocus();
		} catch (BadLocationException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String[] getItems(){
		String s = text.getText();
		String[] result = null;
		if (s != null && !s.isEmpty()){
			List<String> list = new ArrayList<String>();

			StringTokenizerEx tok = new StringTokenizerEx(s, "\n");
			while (tok.hasMoreTokens()){
				String token = tok.nextToken();
				if (token != null && !token.trim().isEmpty()){
					list.add(token.trim());
				}
			}
			if (!list.isEmpty()){
				result = (String[]) list.toArray(new String[list.size()]);
			}
		}
		return result;
	}

	public void setItems(Attribute a, Object[] val){
		if (val == null)
			text.setText("");
		else{
			StringBuilder txt = new StringBuilder();

			for (Object s : val){
				if (txt.length() > 0)
					txt.append("\n");
				txt.append(a.getDataType().editValue(s));
			}

			text.setText(txt.toString());
		}
	}

	public void setEnabled(boolean toEnable){
		if (plus != null)
			plus.setEnabled(toEnable);

		if (minus != null)
			minus.setEnabled(toEnable);

		if (browse != null)
			browse.setEnabled(toEnable);

		if (text != null)
			text.setEnabled(toEnable);
	}

}
