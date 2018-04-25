/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.Container;
import java.awt.Dimension;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class AboutBox extends Ni3Dialog{
	private static final Logger log = Logger.getLogger(AboutBox.class);
	private JEditorPane aboutText;
	private JButton okButton;

	public AboutBox(){
		super();
		initComponents();

		this.setTitle(UserSettings.getWord("About"));
		aboutText.setCaretPosition(0);
	}

	protected void initComponents(){
		String about = UserSettings.getWord("About text");
		about = about.replace("@VER", Ni3.version);
		aboutText = new JEditorPane("text/html", about);
		aboutText.addHyperlinkListener(new AboutHyperlinkListener());
		aboutText.setEditable(false);
		aboutText.addKeyListener(new AboutTextKeyListener());
		JScrollPane pane = new JScrollPane();
		pane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setViewportView(aboutText);

		addWindowListener(new java.awt.event.WindowAdapter(){
			public void windowClosing(java.awt.event.WindowEvent evt){
				doClose();
			}
		});

		okButton = new JButton("OK");
		okButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		okButton.setSize(70, 20);
		okButton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				doClose();
			}
		});

		Container c = getContentPane();
		SpringLayout sLayout = new SpringLayout();
		c.setLayout(sLayout);
		sLayout.putConstraint(SpringLayout.NORTH, pane, 10, SpringLayout.NORTH, c);
		sLayout.putConstraint(SpringLayout.WEST, pane, 10, SpringLayout.WEST, c);
		sLayout.putConstraint(SpringLayout.EAST, pane, -10, SpringLayout.EAST, c);
		sLayout.putConstraint(SpringLayout.SOUTH, pane, -30, SpringLayout.SOUTH, c);

		sLayout.putConstraint(SpringLayout.NORTH, okButton, 5, SpringLayout.SOUTH, pane);
		sLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, okButton, 0, SpringLayout.HORIZONTAL_CENTER, c);
		sLayout.putConstraint(SpringLayout.SOUTH, okButton, -5, SpringLayout.SOUTH, c);
		getContentPane().add(okButton);
		getContentPane().add(pane);
		setMinimumSize(new Dimension(400, 250));
		setLocationRelativeTo(null);
	}

	protected void onEnterAction(){
		doClose();
	}

	private void doClose(){
		setVisible(false);
		dispose();
	}

	private class AboutHyperlinkListener implements HyperlinkListener{

		@Override
		public void hyperlinkUpdate(HyperlinkEvent e){
			if (!HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType()))
				return;
			if (!java.awt.Desktop.isDesktopSupported())
				return;
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

			if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE))
				return;

			try{
				desktop.browse(e.getURL().toURI());
			} catch (Exception ex){
				log.error("Error following link " + e.getURL(), ex);
			}
		}
	}

	private class AboutTextKeyListener implements KeyListener{
		@Override
		public void keyTyped(KeyEvent e){
			if (e.getKeyChar() == KeyEvent.VK_ENTER && e.getModifiers() == 0)
				onEnterAction();
		}

		@Override
		public void keyPressed(KeyEvent e){
		}

		@Override
		public void keyReleased(KeyEvent e){
		}
	}
}