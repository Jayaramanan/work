/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACMandatoryLabel;
import com.ni3.ag.adminconsole.client.view.common.Mnemonic;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;

public class LoginView extends JDialog implements ErrorRenderer{
	private static final long serialVersionUID = 1L;
	private final JPasswordField passwordField;
	private final JTextField userNameText;
	private ACButton loginButton;
	private ACButton cancelButton;

	private JLabel errorLabel;

	private LoginView(){

		super();
		getContentPane().setLayout(null);
		setResizable(false);
		setName("loginDialog");
		setModal(true);
		setSize(new Dimension(363, 242));
		setTitle("Login");
		setBounds(100, 100, 371, 194);

		final ACMandatoryLabel idLabel = new ACMandatoryLabel();
		idLabel.setBounds(0, 23, 85, 14);
		idLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		idLabel.setText("Username");
		getContentPane().add(idLabel);

		userNameText = new JTextField();
		userNameText.setBounds(95, 20, 185, 20);
		userNameText.setColumns(10);
		getContentPane().add(userNameText);

		final ACMandatoryLabel passwordLabel = new ACMandatoryLabel();
		passwordLabel.setBounds(0, 63, 85, 14);
		passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		passwordLabel.setText("Password");
		getContentPane().add(passwordLabel);

		passwordField = new JPasswordField();
		passwordField.setBounds(95, 60, 185, 20);
		getContentPane().add(passwordField);

		loginButton = new ACButton(Mnemonic.AltL);
		loginButton.setBounds(95, 100, 90, 25);
		loginButton.setText("Login");
		getContentPane().add(loginButton);
		getRootPane().setDefaultButton(loginButton);

		cancelButton = new ACButton(Mnemonic.AltC);
		cancelButton.setBounds(195, 100, 85, 25);
		cancelButton.setText("Cancel");
		getContentPane().add(cancelButton);

		errorLabel = new JLabel();
		errorLabel.setForeground(Color.RED);
		errorLabel.setFont(new Font(errorLabel.getFont().getName(), Font.BOLD, 12));
		errorLabel.setBounds(10, 140, 350, 25);
		getContentPane().add(errorLabel);

		passwordField.setName("login_password");
		userNameText.setName("login_userName");
		loginButton.setName("login_login");
		cancelButton.setName("login_cancel");
		errorLabel.setName("login_errorLabel");
	}

	@Override
	public void requestFocus(){
		userNameText.requestFocus();
	}

	public String getUserName(){
		return userNameText.getText();
	}

	public String getPassword(){
		return new String(passwordField.getPassword());
	}

	public void setUserName(String userName){
		userNameText.setText(userName);
	}

	public void setPassword(String password){
		passwordField.setText(password);
	}

	public void addLoginListener(ActionListener mal){
		loginButton.addActionListener(mal);
	}

	public void addCancelListener(ActionListener cal){
		cancelButton.addActionListener(cal);
	}

	public void clearErrors(){
		errorLabel.setText("");
	}

	public void renderErrors(ErrorContainer c){
		if (c != null){
			renderErrors(c.getErrors());
		}
	}

	@Override
	public void renderErrors(List<ErrorEntry> errors){
		if (errors != null && !errors.isEmpty()){
			ErrorEntry err = errors.get(0);
			String msg = Translation.get(err.getId(), err.getErrors());
			errorLabel.setText(msg);
		}
	}

	public void addEscListener(KeyListener escapeListener){
		userNameText.addKeyListener(escapeListener);
		passwordField.addKeyListener(escapeListener);
		loginButton.addKeyListener(escapeListener);
		cancelButton.addKeyListener(escapeListener);
	}
}
