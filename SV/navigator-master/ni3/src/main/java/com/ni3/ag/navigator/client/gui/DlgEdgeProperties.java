/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;

@SuppressWarnings("serial")
public class DlgEdgeProperties extends Ni3Dialog{

	private SpringLayout springLayout;
	private JComboBox comboBox;

	/**
	 * Create the dialog
	 */
	public DlgEdgeProperties(){
		super();
		initComponents();
	}

	protected void initComponents(){
		springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		setSize(500, 375);
		setName("Edge properties");

		JLabel typeLabel;
		typeLabel = new JLabel();
		getContentPane().add(typeLabel);
		springLayout.putConstraint(SpringLayout.SOUTH, typeLabel, 171, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, typeLabel, 155, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, typeLabel, 27, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, typeLabel, 0, SpringLayout.WEST, getContentPane());
		typeLabel.setText("Type");

		comboBox = new JComboBox();
		getContentPane().add(comboBox);
		springLayout.putConstraint(SpringLayout.SOUTH, comboBox, 341, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, comboBox, 0, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, comboBox, 492, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, comboBox, 27, SpringLayout.WEST, getContentPane());

	}

}
