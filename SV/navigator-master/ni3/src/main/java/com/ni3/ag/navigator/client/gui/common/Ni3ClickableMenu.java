/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.common;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public class Ni3ClickableMenu extends JMenu{
	private static final long serialVersionUID = -8101461494919988204L;
	private final JPopupMenu parentMenu;

	public Ni3ClickableMenu(String s, JPopupMenu parent){
		super(s);
		this.parentMenu = parent;
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand(), e.getWhen(),
				        e.getModifiers());
				fireActionPerformed(event);
				if (parentMenu != null){
					parentMenu.setVisible(false);
				}
			}
		});
	}
}
