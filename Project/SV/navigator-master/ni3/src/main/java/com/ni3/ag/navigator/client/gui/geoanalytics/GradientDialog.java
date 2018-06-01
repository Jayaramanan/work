/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.geoanalytics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.gui.ColorChooser;

public class GradientDialog extends Ni3Dialog implements ActionListener{
	private static final long serialVersionUID = -5652922537544625497L;

	private JButton startColorButton;
	private JButton endColorButton;

	private JButton okButton;
	private JButton cancelButton;

	public GradientDialog(JFrame parent, Color baseColor, Color endColor){
		super(parent);
		setTitle(UserSettings.getWord("Gradient"));
		initComponents();
		startColorButton.setBackground(baseColor);
		endColorButton.setBackground(endColor);
	}

	private void initComponents(){
		setModal(true);
		setSize(new Dimension(200, 150));

		JPanel mainPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		mainPanel.setLayout(layout);
		getContentPane().add(mainPanel);

		okButton = new JButton(UserSettings.getWord("Ok"));
		cancelButton = new JButton(UserSettings.getWord("Cancel"));

		startColorButton = new JButton(" ");
		startColorButton.setFocusable(false);
		startColorButton.setBorderPainted(false);
		startColorButton.setContentAreaFilled(false);
		startColorButton.setOpaque(true);

		endColorButton = new JButton(" ");
		endColorButton.setFocusable(false);
		endColorButton.setBorderPainted(false);
		endColorButton.setContentAreaFilled(false);
		endColorButton.setOpaque(true);

		JLabel baseColorLabel = new JLabel(UserSettings.getWord("StartColor"));
		JLabel endColorLabel = new JLabel(UserSettings.getWord("EndColor"));;

		mainPanel.add(okButton);
		mainPanel.add(cancelButton);
		mainPanel.add(startColorButton);
		mainPanel.add(endColorButton);
		mainPanel.add(baseColorLabel);
		mainPanel.add(endColorLabel);

		startColorButton.addActionListener(this);
		endColorButton.addActionListener(this);

		layout.putConstraint(SpringLayout.NORTH, startColorButton, 10, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.WEST, startColorButton, 100, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.EAST, startColorButton, 60, SpringLayout.WEST, startColorButton);

		layout.putConstraint(SpringLayout.NORTH, baseColorLabel, 3, SpringLayout.NORTH, startColorButton);
		layout.putConstraint(SpringLayout.EAST, baseColorLabel, -10, SpringLayout.WEST, startColorButton);

		layout.putConstraint(SpringLayout.NORTH, endColorButton, 10, SpringLayout.SOUTH, startColorButton);
		layout.putConstraint(SpringLayout.WEST, endColorButton, 100, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.EAST, endColorButton, 60, SpringLayout.WEST, endColorButton);

		layout.putConstraint(SpringLayout.NORTH, endColorLabel, 3, SpringLayout.NORTH, endColorButton);
		layout.putConstraint(SpringLayout.EAST, endColorLabel, -10, SpringLayout.WEST, endColorButton);

		layout.putConstraint(SpringLayout.NORTH, cancelButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, cancelButton, -10, SpringLayout.EAST, mainPanel);

		layout.putConstraint(SpringLayout.NORTH, okButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, okButton, -10, SpringLayout.WEST, cancelButton);
	}

	public void addOkButtonListener(ActionListener l){
		okButton.addActionListener(l);
	}

	public void addCancelButtonListener(ActionListener l){
		cancelButton.addActionListener(l);
	}

	public Color getStartColor(){
		return startColorButton.getBackground();
	}

	public Color getEndColor(){
		return endColorButton.getBackground();
	}

	@Override
	public void actionPerformed(ActionEvent e){
		JButton button = (JButton) e.getSource();
		ColorChooser chooser = new ColorChooser(this, UserSettings.getWord("ChooseColor"));
		final Color initialColor = button.getBackground();
		final Color color = chooser.chooseColor(initialColor);
		if (color != null && !color.equals(initialColor)){
			button.setBackground(color);
		}
	}
}