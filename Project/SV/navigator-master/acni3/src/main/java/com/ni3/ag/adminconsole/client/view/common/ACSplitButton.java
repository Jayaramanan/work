/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.MenuElement;
import javax.swing.plaf.basic.BasicArrowButton;

import com.ni3.ag.adminconsole.shared.language.TextID;

public class ACSplitButton extends JToolBar{

	private static final long serialVersionUID = 1L;
	private JButton mainButton;
	private JButton arrowButton;
	private JPopupMenu menu;
	private boolean withDefaultAction = true;
	private Mnemonic mnemonic;

	public ACSplitButton(Mnemonic key, boolean withDefaultAction){
		mainButton = new MainButton(key);
		mnemonic = key;
		this.withDefaultAction = withDefaultAction;
		init();
	}

	public ACSplitButton(Mnemonic key){
		mainButton = new MainButton(key);
		mnemonic = key;
		init();
	}

	public ACSplitButton(Mnemonic key, Action a){
		mainButton = new MainButton(key, a);
		mnemonic = key;
		init();
	}

	public ACSplitButton(Mnemonic key, Icon icon){
		mainButton = new MainButton(key, icon);
		mnemonic = key;
		init();
	}

	public ACSplitButton(Mnemonic key, TextID id, Icon icon){
		mainButton = new MainButton(key, id, icon);
		mnemonic = key;
		init();
	}

	public ACSplitButton(Mnemonic key, TextID id){
		mainButton = new MainButton(key, id);
		mnemonic = key;
		init();
	}

	public ACSplitButton(){
		mainButton = new MainButton();
		init();
	}

	public ACSplitButton(Action a){
		mainButton = new MainButton(a);
		init();
	}

	public ACSplitButton(TextID id, Icon icon){
		mainButton = new MainButton(id, icon);
		init();
	}

	public ACSplitButton(TextID id){
		mainButton = new MainButton(id);
		init();
	}

	public ACSplitButton(Icon icon){
		mainButton = new MainButton(icon);
		init();
	}

	private void init(){
		setFloatable(false);
		setRollover(true);
		arrowButton = createArrowButton();
		arrowButton.setBorder(BorderFactory.createEmptyBorder());
		if (mnemonic != null)
			arrowButton.setName("Split_" + mnemonic.name() + "_arrow");

		this.menu = new JPopupMenu();
		this.add(mainButton);
		this.add(arrowButton);
		ActionListener lsn = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Point location = mainButton.getLocation();
				int height = mainButton.getHeight();
				menu.show(ACSplitButton.this, location.x, location.y + height);
			}
		};

		this.arrowButton.addActionListener(lsn);
		if (!withDefaultAction){
			mainButton.addActionListener(lsn);
		}
	}

	protected JButton createArrowButton(){
		JButton button = new ArrowButton();
		return button;
	}

	public Action getAction(){
		return mainButton.getAction();
	}

	public void setAction(Action action){
		mainButton.setAction(action);
	}

	public JMenuItem add(JMenuItem menuItem){
		JMenuItem ret = menu.add(menuItem);
		return ret;
	}

	public JMenuItem add(String s){
		JMenuItem ret = menu.add(s);
		return ret;
	}

	public JMenuItem add(String s, String command){
		JMenuItem ret = menu.add(s);
		ret.setActionCommand(command);
		return ret;
	}

	public JMenuItem add(Icon icon, String s, String command){
		JMenuItem ret = new JMenuItem(s, icon);
		if (mnemonic != null)
			menu.add(ret).setName("item_" + mnemonic.name() + "_" + command);
		ret.setActionCommand(command);
		ret.setMargin(new Insets(2, -10, 2, 0));
		return ret;
	}

	public JMenuItem addAction(Action a){
		JMenuItem ret = menu.add(a);
		return ret;
	}

	public void setText(String text){
		mainButton.setText(text);
	}

	public void setIcon(Icon icon){
		mainButton.setIcon(icon);
	}

	public void addActionListener(ActionListener listener){
		for (MenuElement menuItem : menu.getSubElements()){
			((JMenuItem) menuItem).addActionListener(listener);
		}
		if (withDefaultAction){
			mainButton.addActionListener(listener);
		}
	}

	public void setToolTipText(String text){
		mainButton.setToolTipText(text);
	}

	public void setEnabled(boolean b){
		mainButton.setEnabled(b);
		arrowButton.setEnabled(b);
	}

	private class MainButton extends ACButton{
		private static final long serialVersionUID = 1L;

		public MainButton(Mnemonic key){
			super(key);
		}

		public MainButton(Mnemonic key, Action a){
			super(key, a);
		}

		public MainButton(Mnemonic key, Icon icon){
			super(key, icon);
		}

		public MainButton(Mnemonic key, TextID id, Icon icon){
			super(key, id, icon);
		}

		public MainButton(Mnemonic key, TextID id){
			super(key, id);
		}

		public MainButton(){
			super();
		}

		public MainButton(Action a){
			super(a);
		}

		public MainButton(TextID id, Icon icon){
			super(id, icon);
		}

		public MainButton(TextID id){
			super(id);
		}

		public MainButton(Icon icon){
			super(icon);
		}

	}

	private class ArrowButton extends BasicArrowButton{

		private static final long serialVersionUID = 1L;

		public ArrowButton(){
			super(BasicArrowButton.SOUTH);
		}

		public Dimension getPreferredSize(){
			return new Dimension(8, 16);
		}

		public Dimension getMinimumSize(){
			return new Dimension(5, 5);
		}

		public Dimension getMaximumSize(){
			return new Dimension(8, 20);
		}

		@Override
		public void paintTriangle(Graphics g, int x, int y, int size, int direction, boolean isEnabled){
			super.paintTriangle(g, x, y, size + 3, direction, isEnabled);
		}
	}

	public void resetLabels(String[] newLabels){
		if (newLabels == null || newLabels.length == 0)
			return;
		setToolTipText(newLabels[0]);
		MenuElement[] menuElems = menu.getSubElements();
		for (int i = 1; i < newLabels.length; i++){
			JMenuItem mi = (JMenuItem) menuElems[i - 1];
			mi.setText(newLabels[i]);
		}
	}

}