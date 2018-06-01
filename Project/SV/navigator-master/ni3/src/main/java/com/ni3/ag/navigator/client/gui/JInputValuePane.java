/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.Dimension;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import com.ni3.ag.navigator.client.util.SpringUtilities;

@SuppressWarnings("serial")
public class JInputValuePane extends JScrollPane{
	public JPanel panel;

	private String Values[];
	private JFormattedTextField ValuesTF[];
	@SuppressWarnings("unused")
	private String formats[];

	public JInputValuePane(String Values[], Object DefaultValues[], String formats[]){
		panel = new JPanel();
		setViewportView(panel);

		this.Values = Values;
		this.formats = formats;

		ValuesTF = new JFormattedTextField[Values.length];

		makePanel(Values, DefaultValues);
	}

	public void makePanel(String Values[], Object DefaultValues[]){
		panel.removeAll();

		panel.setLayout(new SpringLayout());

		panel.setPreferredSize(new Dimension(200, 30 * Values.length));
		panel.setMinimumSize(new Dimension(180, 27 * Values.length));

		for (int n = 0; n < Values.length; n++){
			JLabel polje1Label = new JLabel();
			polje1Label.setText(Values[n]);
			polje1Label.setPreferredSize(new Dimension((int) (Values[n].length() * 1.1), 25));
			polje1Label.setMinimumSize(new Dimension((int) (Values[n].length() * 1.1), 25));
			polje1Label.setMaximumSize(new Dimension(400, 25));
			panel.add(polje1Label);

			ValuesTF[n] = new JFormattedTextField();
			ValuesTF[n].setPreferredSize(new Dimension(40, 25));
			ValuesTF[n].setMinimumSize(new Dimension(30, 25));
			ValuesTF[n].setMaximumSize(new Dimension(200, 25));
			ValuesTF[n].setValue(DefaultValues[n]);
			panel.add(ValuesTF[n]);
		}

		SpringUtilities.makeCompactGrid(panel, Values.length, 2, // rows, cols
		        6, 6, // initX, initY
		        6, 6); // xPad, yPad

	}

	public Object getValue(int ValueIndex){
		return ValuesTF[ValueIndex].getValue();
	}

	public Object getValue(String ValueName){
		for (int n = 0; n < Values.length; n++){
			if (Values[n].equalsIgnoreCase(ValueName))
				return getValue(n);
		}

		return "";
	}

}
