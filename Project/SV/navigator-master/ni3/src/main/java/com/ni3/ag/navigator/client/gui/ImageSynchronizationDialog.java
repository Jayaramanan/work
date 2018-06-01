/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.BorderLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.domain.UserSettings;

@SuppressWarnings("serial")
public class ImageSynchronizationDialog extends Ni3Dialog{
	public static final int DOWNLOADING_ICONS = 4;
	public static final int STATUS_DONE = 5;

	private JTextField statusLabel = new JTextField();
	private JTextField errorLabel = new JTextField();
	private JPanel mainPanel = new JPanel();
	private JButton okButton;

	public ImageSynchronizationDialog(){
		super();
		setTitle(UserSettings.getWord("ImageSynchronization"));
		setSize(300, 140);
		setLocationRelativeTo(null);
		setModal(false);
		initComponents();
	}

	protected void initComponents(){
		mainPanel.add(statusLabel);
		mainPanel.add(errorLabel);
		errorLabel.setText("asdfasdfasfdasdf");
		statusLabel.setBorder(BorderFactory.createEmptyBorder());
		statusLabel.setBackground(SystemColor.control);
		errorLabel.setBorder(BorderFactory.createEmptyBorder());
		errorLabel.setBackground(SystemColor.control);
		errorLabel.setEditable(false);
		statusLabel.setEditable(false);

		okButton = new JButton(UserSettings.getWord("Ok"));
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setVisible(false);
			}
		});
		okButton.setEnabled(false);
		mainPanel.add(okButton);

		SpringLayout layout = new SpringLayout();
		mainPanel.setLayout(layout);

		layout.putConstraint(SpringLayout.NORTH, statusLabel, 20, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.WEST, statusLabel, 10, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.EAST, statusLabel, 130, SpringLayout.WEST, mainPanel);

		layout.putConstraint(SpringLayout.NORTH, errorLabel, 10, SpringLayout.SOUTH, statusLabel);
		layout.putConstraint(SpringLayout.WEST, errorLabel, 10, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.EAST, errorLabel, -10, SpringLayout.EAST, mainPanel);

		layout.putConstraint(SpringLayout.NORTH, okButton, 10, SpringLayout.SOUTH, errorLabel);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, okButton, 0, SpringLayout.HORIZONTAL_CENTER, mainPanel);

		getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	public void updateState(int state, int errorCount){
		String status = null;
		String error = UserSettings.getWord("ImgSyncStatusErrorCount");
		switch (state){
			case DOWNLOADING_ICONS:
				status = UserSettings.getWord("ImgSyncStatusDownloading");
				break;
			case STATUS_DONE:
				status = UserSettings.getWord("ImgSyncStatusDone");
				okButton.setEnabled(true);
				break;
		}

		error += ": " + errorCount;
		statusLabel.setText(status);
		if (errorCount >= 0)
			errorLabel.setText(error);
		JComponent[] componenets = new JComponent[] { statusLabel, errorLabel, mainPanel };
		for (JComponent c : componenets){
			c.repaint();
			c.paintImmediately(c.getBounds());
		}
	}

	public void setErrorState(){
		String status = UserSettings.getWord("ImgSyncStatusError");
		statusLabel.setText(status);
		okButton.setEnabled(true);
	}

	public void showDialog(){
		setVisible(true);
		updateState(DOWNLOADING_ICONS, 0);
	}
}
