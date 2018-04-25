/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import com.ni3.ag.navigator.client.domain.cache.IconCache;

public class Ni3Frame extends JFrame{
	private static final long serialVersionUID = 1880584305708355811L;

	public Ni3Frame(){
		super();
//		ImageIcon frameIcon = IconCache.getImageIcon("molecule.png");
//		if (frameIcon != null)
//			setIconImage(frameIcon.getImage());
	}

	public Ni3Frame(String title){
		this();
		setTitle(title);
	}

	protected JRootPane createRootPane(){
		KeyStroke escStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = new JRootPane();
		rootPane.registerKeyboardAction(new EscapeActionListener(), escStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
	}

	private class EscapeActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e){
			setVisible(false);
		}
	}
}
