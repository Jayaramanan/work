/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.text.AbstractDocument;

import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.Mnemonic;
import com.ni3.ag.adminconsole.client.view.common.StringValidator;
import com.ni3.ag.adminconsole.client.view.schemaadmin.ObjectNameDocumentFilter;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.BasicErrorContainer;

public class NewSchemaRequestDialog extends JDialog implements ErrorRenderer{

	private static final long serialVersionUID = -651234216740922689L;
	private String newName;
	private JLabel label;
	private JLabel errorPane;
	private JTextField field;
	private ACButton okButton;
	private ACButton cancelButton;

	public NewSchemaRequestDialog(String title, String prompt, String initialValue){
		setTitle(title);
		newName = initialValue;
		initComponents(prompt, initialValue);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setLocation(screenWidth / 2 - getWidth() / 2, screenHeight / 2 - getHeight() / 2);
	}

	private void initComponents(String prompt, String initialValue){
		setResizable(false);
		setModal(true);
		setSize(new Dimension(300, 160));
		SpringLayout layout = new SpringLayout();
		Container c = getContentPane();
		c.setLayout(layout);

		errorPane = new JLabel();
		errorPane.setSize(0, 0);
		layout.putConstraint(SpringLayout.NORTH, errorPane, 10, SpringLayout.NORTH, c);
		layout.putConstraint(SpringLayout.WEST, errorPane, 10, SpringLayout.WEST, c);
		c.add(errorPane);

		label = new JLabel(prompt);
		layout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.SOUTH, errorPane);
		layout.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, c);
		c.add(label);

		field = new JTextField();
		((AbstractDocument) field.getDocument()).setDocumentFilter(new ObjectNameDocumentFilter());
		layout.putConstraint(SpringLayout.NORTH, field, 10, SpringLayout.SOUTH, label);
		layout.putConstraint(SpringLayout.WEST, field, 20, SpringLayout.WEST, c);
		layout.putConstraint(SpringLayout.EAST, field, -20, SpringLayout.EAST, c);
		c.add(field);
		field.setText(initialValue);
		field.selectAll();

		okButton = new ACButton(Mnemonic.AltO, TextID.Ok);
		okButton.setSize(70, 23);
		okButton.setPreferredSize(new Dimension(70, 23));
		cancelButton = new ACButton(Mnemonic.AltC, TextID.Cancel);
		cancelButton.setSize(70, 23);
		cancelButton.setPreferredSize(new Dimension(70, 23));

		layout.putConstraint(SpringLayout.NORTH, cancelButton, -33, SpringLayout.SOUTH, c);
		layout.putConstraint(SpringLayout.EAST, cancelButton, -10, SpringLayout.EAST, c);
		c.add(cancelButton);

		layout.putConstraint(SpringLayout.NORTH, okButton, -33, SpringLayout.SOUTH, c);
		layout.putConstraint(SpringLayout.EAST, okButton, -10, SpringLayout.WEST, cancelButton);
		c.add(okButton);

		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				NewSchemaRequestDialog.this.newName = null;
				NewSchemaRequestDialog.this.setVisible(false);
			}
		});

		addWindowListener(new NewSchemaRequestWindowListener());

		okButton.addActionListener(new OkButtonListener());
		getRootPane().setDefaultButton(okButton);
	}

	private class OkButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent e){
			String text = NewSchemaRequestDialog.this.field.getText();
			text = StringValidator.validate(text);
			if (text == null){
				field.setText(text);
				renderErrors(new BasicErrorContainer(new ErrorEntry(TextID.MsgSectionNameEmpty)));
				return;
			}
			if (text.length() > Schema.SCHEMA_MAX_NAME_LENGTH){
				renderErrors(new BasicErrorContainer(new ErrorEntry(TextID.MsgEnteredNameTooLong)));
				return;
			}
			newName = text;
			setVisible(false);
		}
	};

	private class NewSchemaRequestWindowListener implements WindowListener{

		public void windowOpened(WindowEvent e){
		}

		public void windowIconified(WindowEvent e){
		}

		public void windowDeiconified(WindowEvent e){
		}

		public void windowDeactivated(WindowEvent e){
		}

		public void windowClosing(WindowEvent e){
			NewSchemaRequestDialog.this.newName = null;
		}

		public void windowClosed(WindowEvent e){
		}

		public void windowActivated(WindowEvent e){
		}
	}

	public void renderErrors(ErrorContainer c){
		renderErrors(c.getErrors());
	}

	public String getNewName(){
		return newName;
	}

	@Override
	public void renderErrors(List<ErrorEntry> errs){
		StringBuffer sb = new StringBuffer();
		for (ErrorEntry err : errs){
			String s = Translation.get(err.getId(), err.getErrors());
			sb.append(s).append("\n");
		}
		errorPane.setForeground(Color.RED);
		errorPane.setText(sb.toString());
	}
}
