/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.common;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

import com.ni3.ag.navigator.client.gui.Ni3;

public abstract class Ni3Dialog extends JDialog{

	private static final long serialVersionUID = 1L;

	public Ni3Dialog(){
		this(Ni3.mainF instanceof JFrame ? (JFrame)Ni3.mainF : null, true);
	}

	private Ni3Dialog(JFrame parent, boolean modal){
		super(parent, modal);
	}

	public Ni3Dialog(Window parent){
		super(parent);
	}

	public Ni3Dialog(JDialog parent){
		super(parent);
	}

	public Ni3Dialog(JFrame parent){
		super(parent);
	}

	@Override
	public void setVisible(boolean visible){
		// alwaysOnTop should be set to false when visible=false
		// http://bugs.sun.com/view_bug.do?bug_id=6829546
		setAlwaysOnTop(visible);
		super.setVisible(visible);
	}

	@Override
	public void dispose(){
		setAlwaysOnTop(false);
		super.dispose();
	}

	protected JRootPane createRootPane(){
		KeyStroke escStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		JRootPane rootPane = new JRootPane();
		rootPane.registerKeyboardAction(new EscapeActionListener(), escStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		rootPane.registerKeyboardAction(new EnterActionListener(), enterStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
	}

	protected void onEnterAction(){
		// implement in subclasses to set action on enter key
	}

	private class EscapeActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e){
			setVisible(false);
		}
	}

	private class EnterActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e){
			onEnterAction();
		}
	}
}
