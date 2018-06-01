/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;


import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.view.SavePromptDialog;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ACForceConfirmDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = -8350081086872806152L;

	private Logger log = Logger.getLogger(SavePromptDialog.class);

	private ACButton okButton;
	private ACButton cancelButton;
	private JCheckBox forceComboBox;
	private JLabel questionLabel;
	public static final String OK_ACTION = "OK";
	public static final String CANCEL_ACTION = "Cancel";

	private String action = CANCEL_ACTION;

	public ACForceConfirmDialog(String title, String message, String forceText){
		setTitle(title);
		initComponents(message, forceText);
		questionLabel.setText(message);
		setLocation((int) (ACMain.getScreenWidth() / 2) - getWidth() / 2, (int) (ACMain.getScreenHeight() / 2) - getHeight() / 2);
		setIconImage(new ImageIcon(getClass().getResource("/images/Ni3.png")).getImage());
	}

	private void initComponents(String message, String forceText){
		setModal(true);
		setSize(new Dimension(300, 155));

		JPanel mainPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		mainPanel.setLayout(layout);
		getContentPane().add(mainPanel);

		Icon questionIcon = new ImageIcon(ACMain.class.getResource("/images/Question.png"));
		questionLabel = new JLabel(message, questionIcon, SwingConstants.LEFT);
		questionLabel.setIconTextGap(10);

		mainPanel.add(questionLabel);

		layout.putConstraint(SpringLayout.WEST, questionLabel, 20, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, questionLabel, 20, SpringLayout.NORTH, mainPanel);

		okButton = new ACButton(Mnemonic.AltO, TextID.Ok);
		okButton.setSize(70, 23);
		okButton.setPreferredSize(new Dimension(70, 23));
		cancelButton = new ACButton(Mnemonic.AltC, TextID.Cancel);
		cancelButton.setSize(70, 23);
		cancelButton.setPreferredSize(new Dimension(70, 23));
		forceComboBox = new JCheckBox(forceText, false);
		forceComboBox.setVisible(forceText != null);

		layout.putConstraint(SpringLayout.NORTH, forceComboBox, 10, SpringLayout.SOUTH, questionLabel);
		layout.putConstraint(SpringLayout.WEST, forceComboBox, 20, SpringLayout.WEST, mainPanel);
		mainPanel.add(forceComboBox);

		layout.putConstraint(SpringLayout.NORTH, cancelButton, 10, SpringLayout.SOUTH, forceComboBox);
		layout.putConstraint(SpringLayout.EAST, cancelButton, -20, SpringLayout.EAST, mainPanel);
		mainPanel.add(cancelButton);
		cancelButton.addActionListener(this);

		layout.putConstraint(SpringLayout.NORTH, okButton, 10, SpringLayout.SOUTH, forceComboBox);
		layout.putConstraint(SpringLayout.EAST, okButton, -10, SpringLayout.WEST, cancelButton);
		mainPanel.add(okButton);
		okButton.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == okButton){
			action = OK_ACTION;
			log.debug("Ok action");
		} else{
			action = CANCEL_ACTION;
			log.debug("Cancel action");
		}
		setVisible(false);
	}

	public String getSelectedAction(){
		action = CANCEL_ACTION;
		setVisible(true);
		return action;
	}

	public boolean isForceOption(){
		return forceComboBox.isSelected();
	}
}