package com.ni3.ag.navigator.client.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;

@SuppressWarnings("serial")
public class DropDownButton extends JPanel{
	int left;
	boolean firstTime = true;
	boolean isSelected = false;

	JButton button;
	BasicArrowButton arrow;

	ArrayList<ActionListener> listeners;

	String[] options;
	String[] commands;

	public DropDownButton(String text, String options[], String commands[]){
		button = new JButton(text);
		init(options, commands);
	}

	public DropDownButton(ImageIcon icon, String options[], String commands[]){
		button = new JButton(icon);
		init(options, commands);
	}

	private void init(String options[], String commands[]){
		this.options = options;
		this.commands = commands;

		listeners = new ArrayList<ActionListener>();
		arrow = new BasicArrowButton(BasicArrowButton.SOUTH);

		setLayout(new BorderLayout());
		add(button, BorderLayout.CENTER);
		add(arrow, BorderLayout.EAST);

		button.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				for (ActionListener l : listeners){
					l.actionPerformed(new ActionEvent(this, 1, "ExpandAll"));
				}
			}
		});

		arrow.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				JPopupMenu menu = getPopupMenu();
				menu.show(button, 0, button.getHeight());
			}
		});
	}

	private JPopupMenu getPopupMenu(){
		JPopupMenu popupMenu = new JPopupMenu();
		for (int j = 0; j < options.length; j++){
			JMenuItem item = new JMenuItem(options[j]);
			item.setActionCommand(commands[j]);

			for (ActionListener l : listeners){
				item.addActionListener(l);
			}
			popupMenu.add(item);
		}
		return popupMenu;
	}

	public void addActionListener(ActionListener lst){
		listeners.add(lst);
	}

    // Marathon support
    @Override
    public void setName(String name) {
        button.setName(name);
        arrow.setName(name + "ArrowButton");
    }
}
