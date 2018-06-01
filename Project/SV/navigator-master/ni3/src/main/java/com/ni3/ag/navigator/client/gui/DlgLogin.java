/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.controller.login.LoginController;

@SuppressWarnings("serial")
public class DlgLogin extends javax.swing.JDialog{
	private static final Logger log = Logger.getLogger(DlgLogin.class);
	private javax.swing.JPasswordField fPassword;
	private javax.swing.JTextField fUsername;
	private javax.swing.JLabel usernameLabel;
	private javax.swing.JLabel passwordLabel;
	private javax.swing.JButton bOK;
	private javax.swing.JButton bCancel;
	private javax.swing.JButton bReset;

	private boolean okPressed;

	/** Creates new form DlgLogin */
	public DlgLogin(java.awt.Frame parent, boolean modal){
		super(parent, modal);

		initComponents();

		getRootPane().setDefaultButton(bOK);
		bCancel.setMnemonic(KeyEvent.VK_ESCAPE);
	}

	public boolean isOkPressed(){
		return okPressed;
	}

	public void setOkPressed(boolean okPressed){
		this.okPressed = okPressed;
	}

	public String getUsername(){
		return fUsername.getText();
	}

	public String getPassword(){
		return new String(fPassword.getPassword());
	}

	private void initComponents(){
		ImageIcon frameIcon = new ImageIcon(Ni3.class.getResource("/Ni3.png"));
		if (frameIcon != null)
			setIconImage(frameIcon.getImage());
		setSize(new Dimension(360, 255));
		setResizable(false);
		setTitle("Login");

		usernameLabel = new javax.swing.JLabel();
		passwordLabel = new javax.swing.JLabel();
		fUsername = new javax.swing.JTextField();
		fUsername.setName("UserName");
		bOK = new javax.swing.JButton();
		bCancel = new javax.swing.JButton();
		fPassword = new javax.swing.JPasswordField();
		fPassword.setName("Password");
		bReset = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		usernameLabel.setText("Username");
		usernameLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		passwordLabel.setText("Password");
		passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		bOK.setText("OK");
		bOK.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				onOK(evt);
			}
		});

		bCancel.setText("Cancel");
		bCancel.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				onCancel(evt);
			}
		});

		bReset.setText("Forgot password?");
		bReset.setBorderPainted(false);
		bReset.setContentAreaFilled(false);
		bReset.setRolloverEnabled(true);
		bReset.setForeground(Color.BLUE);
		bReset.setHorizontalAlignment(SwingConstants.LEFT);
		bReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		bReset.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				onReset(evt);
			}
		});

		Container contentPane = getContentPane();
		contentPane.add(usernameLabel);
		contentPane.add(fUsername);
		contentPane.add(passwordLabel);
		contentPane.add(fPassword);
		contentPane.add(bOK);
		contentPane.add(bReset);

		contentPane.setLayout(null);
		setBounds(100, 100, 370, 190);
		usernameLabel.setBounds(0, 23, 85, 14);
		fUsername.setBounds(95, 20, 190, 20);
		passwordLabel.setBounds(0, 63, 85, 14);
		fPassword.setBounds(95, 60, 190, 20);
		bReset.setBounds(120, 130, 120, 25);

		if (!Ni3.AppletMode){
			contentPane.add(bCancel);
			bOK.setBounds(95, 100, 90, 25);
			bCancel.setBounds(195, 100, 90, 25);
		} else{
			bOK.setBounds(130, 100, 100, 25);
		}
	}

	public void setUsernamePassword(String username, String password, boolean relogin){
		setUsernamePassword(username, password);
		if (relogin){
			this.fUsername.setEnabled(false);
			this.fUsername.setToolTipText("To login with different user please restart the application");
		}
	}

	public void setUsernamePassword(String username, String password){
		if (username != null)
			fUsername.setText(username);

		if (password != null)
			fPassword.setText(password);
	}

	private void onCancel(java.awt.event.ActionEvent evt){
		okPressed = false;
		setVisible(false);
	}

	private void onOK(java.awt.event.ActionEvent evt){
		okPressed = true;
		setVisible(false);
	}

	@Override
	public void setVisible(boolean b){
		if (b){
			okPressed = false;
		}
		super.setVisible(b);
	}

	private void onReset(java.awt.event.ActionEvent evt){
		String email = JOptionPane.showInputDialog(this, "New password will be send to your email.\n Enter email:",
				"Get new password", JOptionPane.OK_CANCEL_OPTION);
		if (email != null && email.length() > 0){
			LoginController loginController = new LoginController();
			try{
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				boolean result = loginController.resetPassword(email);
				if (!result){
					log.error("Cannot connect to the database from server");
					showErrorMessage("Cannot reset password");
				} else{
					JOptionPane.showMessageDialog(this, "New password was sent to email.", "",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} finally{
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

	private void showErrorMessage(String message){
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void update(Graphics g){
		paint(g);
	}

	public void paint(Graphics g){
		super.paint(g);
	}
}
