/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

public class CustomTextArea extends JPanel{
	private static final long serialVersionUID = 1L;
	JTextArea area;

	public CustomTextArea(String title){
		setLayout(new BorderLayout());

		area = new JTextArea();
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setFont(new Font(getFont().getName(), getFont().getStyle(), 12));

		JScrollPane sp = new JScrollPane(area);
		sp.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JLabel label = new JLabel(title);
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		add(label, BorderLayout.NORTH);
		add(sp, BorderLayout.CENTER);
	}

	public JTextArea getTextArea(){
		return area;
	}
}
