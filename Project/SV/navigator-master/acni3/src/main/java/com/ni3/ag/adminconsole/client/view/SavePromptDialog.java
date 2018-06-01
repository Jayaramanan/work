/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view;

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
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.Mnemonic;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class SavePromptDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = -8350081086872806152L;

	private Logger log = Logger.getLogger(SavePromptDialog.class);

	private ACButton saveButton;
	private ACButton cancelButton;
	private ACButton discardButton;
	private JCheckBox saveDecisionBox;
	public static final String SAVE_ACTION = "Save";
	public static final String DISCARD_ACTION = "Discard";
	public static final String CANCEL_ACTION = "Cancel";
	public static final String ALWAYS_ASK = "AlwaysAsk";

	private String action = CANCEL_ACTION;

	public SavePromptDialog(){
		setTitle(Translation.get(TextID.SaveChanges));
		initComponents();
		setLocation((int) (ACMain.getScreenWidth() / 2) - getWidth() / 2, (int) (ACMain.getScreenWidth() / 2) - getHeight() / 2);
		setIconImage(new ImageIcon(getClass().getResource("/images/Ni3.png")).getImage());
	}

	private void initComponents(){
		setModal(true);
		setSize(new Dimension(300, 155));

		JPanel mainPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		mainPanel.setLayout(layout);
		getContentPane().add(mainPanel);

		Icon questionIcon = new ImageIcon(ACMain.class.getResource("/images/Question.png"));
		JLabel questionLabel = new JLabel(Translation.get(TextID.SaveChangesQuestion), questionIcon, SwingConstants.LEFT);
		questionLabel.setIconTextGap(10);

		mainPanel.add(questionLabel);

		layout.putConstraint(SpringLayout.WEST, questionLabel, 20, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, questionLabel, 20, SpringLayout.NORTH, mainPanel);

		saveButton = new ACButton(Mnemonic.AltO, TextID.Save);
		saveButton.setSize(70, 23);
		saveButton.setPreferredSize(new Dimension(70, 23));
		cancelButton = new ACButton(Mnemonic.AltC, TextID.Cancel);
		cancelButton.setSize(70, 23);
		cancelButton.setPreferredSize(new Dimension(70, 23));
		discardButton = new ACButton(Mnemonic.AltD, TextID.Discard);
		discardButton.setSize(70, 23);
		discardButton.setPreferredSize(new Dimension(70, 23));
		saveDecisionBox = new JCheckBox();
		saveDecisionBox.setText(Translation.get(TextID.MsgRememberTabSwitchDecision));
		saveDecisionBox.setMnemonic(Mnemonic.AltR.getKey());

		layout.putConstraint(SpringLayout.NORTH, saveDecisionBox, 10, SpringLayout.SOUTH, questionLabel);
		layout.putConstraint(SpringLayout.WEST, saveDecisionBox, 20, SpringLayout.WEST, mainPanel);
		mainPanel.add(saveDecisionBox);

		layout.putConstraint(SpringLayout.NORTH, cancelButton, 10, SpringLayout.SOUTH, saveDecisionBox);
		layout.putConstraint(SpringLayout.EAST, cancelButton, -20, SpringLayout.EAST, mainPanel);
		mainPanel.add(cancelButton);
		cancelButton.addActionListener(this);

		layout.putConstraint(SpringLayout.NORTH, discardButton, 10, SpringLayout.SOUTH, saveDecisionBox);
		layout.putConstraint(SpringLayout.EAST, discardButton, -10, SpringLayout.WEST, cancelButton);
		mainPanel.add(discardButton);
		discardButton.addActionListener(this);

		layout.putConstraint(SpringLayout.NORTH, saveButton, 10, SpringLayout.SOUTH, saveDecisionBox);
		layout.putConstraint(SpringLayout.EAST, saveButton, -10, SpringLayout.WEST, discardButton);
		mainPanel.add(saveButton);
		saveButton.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == saveButton){
			action = SAVE_ACTION;
			log.debug("Save action");
		} else if (e.getSource() == discardButton){
			action = DISCARD_ACTION;
			log.debug("Discard action");
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

	public boolean getRememberDecision(){
		return saveDecisionBox.isSelected();
	}

}
