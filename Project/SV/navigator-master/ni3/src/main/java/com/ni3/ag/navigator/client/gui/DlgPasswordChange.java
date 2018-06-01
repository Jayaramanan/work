/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

import com.ni3.ag.navigator.client.controller.PasswordValidator;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import org.apache.log4j.Logger;


@SuppressWarnings("serial")
public class DlgPasswordChange extends Ni3Dialog{
	private static final Logger log = Logger.getLogger(DlgPasswordChange.class);

	private javax.swing.JPasswordField passwordField;
	private javax.swing.JPasswordField passwordNewField;
	private javax.swing.JPasswordField passwordConfirmField;
	private JButton okButton;
	private JButton cancelButton;

	public String password, passwordnew;
	public Boolean cancel;

	public DlgPasswordChange(){
		super();

		cancel = true;
		initComponents();

		getRootPane().setDefaultButton(okButton);
		cancelButton.setMnemonic(KeyEvent.VK_ESCAPE);

	}

	protected void initComponents(){
		setTitle(UserSettings.getWord("ChangePassword"));

		getContentPane().setLayout(null);
		setBounds(100, 100, 370, 220);

		final JLabel oldPasswordLabel = new JLabel();
		oldPasswordLabel.setText(UserSettings.getWord("Password"));
		oldPasswordLabel.setBounds(10, 23, 100, 16);
		oldPasswordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(oldPasswordLabel);

		final JLabel newPasswordLabel = new JLabel();
		newPasswordLabel.setText(UserSettings.getWord("NewPassword"));
		newPasswordLabel.setBounds(10, 63, 100, 16);
		newPasswordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(newPasswordLabel);

		final JLabel confirmPasswordLabel = new JLabel();
		confirmPasswordLabel.setText(UserSettings.getWord("ConfirmPassword"));
		confirmPasswordLabel.setBounds(10, 103, 100, 16);
		confirmPasswordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(confirmPasswordLabel);

		passwordField = new JPasswordField();
		passwordField.setBounds(120, 20, 180, 20);
		getContentPane().add(passwordField);

		passwordNewField = new JPasswordField();
		passwordNewField.setBounds(120, 60, 180, 20);
		getContentPane().add(passwordNewField);

		passwordConfirmField = new JPasswordField();
		passwordConfirmField.setBounds(120, 100, 180, 20);
		getContentPane().add(passwordConfirmField);

		okButton = new JButton();
		okButton.setText(UserSettings.getWord("OK"));
		okButton.setBounds(80, 140, 100, 26);
		getContentPane().add(okButton);

		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		cancelButton.setBounds(200, 140, 100, 26);
		getContentPane().add(cancelButton);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		okButton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				onOK();
			}
		});

		cancelButton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				onCancel();
			}
		});

	}

	private void onCancel(){
		cancel = true;
		setVisible(false);
	}

	private void onOK(){
		password = new String(passwordField.getPassword());
		passwordnew = new String(passwordNewField.getPassword());
		String passwordconfirm = new String(passwordConfirmField.getPassword());

		if (!passwordnew.equals(passwordconfirm)){
			log.warn("New passwords are not identical");
			JOptionPane.showMessageDialog(this, UserSettings.getWord("MsgNewPasswordsAreNotIdentical"),
			        UserSettings.getWord("IncorectPasswordTitle"), JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (!validatePassword(passwordnew))
			return;

		cancel = false;
		setVisible(false);
	}

	private boolean validatePassword(String password){
		PasswordValidator passwordValidator = new PasswordValidator();
		String passwordFormat = UserSettings.getStringAppletProperty("PasswordComplexity", null);
		if (passwordFormat != null && !passwordFormat.isEmpty() && !"null".equals(passwordFormat)){
			if (!passwordValidator.parseFormat(passwordFormat)){
				log.error("Cannot parse password complexity format: " + passwordFormat);
				return true;
			}
			if (!passwordValidator.isPasswordValid(password)){
				log.warn("Pasword doesn't match complexity");
				JOptionPane.showMessageDialog(this, UserSettings.getWord("MsgPasswordDoesntMatchComplexity") + "\n"
				        + UserSettings.getWord("PasswordComplexityDescriptionLabel"),
				        UserSettings.getWord("IncorectPasswordTitle"), JOptionPane.WARNING_MESSAGE);
				return false;
			}
			log.debug("Password validated for complexity");
		}
		return true;
	}

	@Override
	protected void onEnterAction(){
		onOK();
	}

	public void update(Graphics g){
		paint(g);
	}

	public void paint(Graphics g){
		super.paint(g);
	}

}
