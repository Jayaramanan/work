/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACTextArea;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class DBPropertiesDialog extends JDialog{

	private static final long serialVersionUID = 1L;
	private ACTextArea dbPropertiesArea;
	private ACButton okBtn;

	public DBPropertiesDialog(){
		super();
		setModal(true);
		setTitle(Translation.get(TextID.DatabaseProperties));
		setResizable(false);
		initComponents();
		setSize(480, 360);
		setIconImage(new ImageIcon(getClass().getResource("/images/Ni3.png")).getImage());
	}

	public void setText(String text){
		dbPropertiesArea.setText(text);
	}

	public void initComponents(){
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		dbPropertiesArea = new ACTextArea();
		dbPropertiesArea.setEditable(false);
		JScrollPane jsp = new JScrollPane(dbPropertiesArea);
		Container cont = getContentPane();
		cont.add(jsp);
		layout.putConstraint(SpringLayout.WEST, jsp, 5, SpringLayout.WEST, cont);
		layout.putConstraint(SpringLayout.NORTH, jsp, 5, SpringLayout.NORTH, cont);
		layout.putConstraint(SpringLayout.EAST, jsp, -5, SpringLayout.EAST, cont);
		layout.putConstraint(SpringLayout.SOUTH, jsp, 285, SpringLayout.NORTH, jsp);

		okBtn = new ACButton(TextID.Ok);
		JPanel btnPanel = new JPanel(new FlowLayout());
		btnPanel.add(okBtn);
		cont.add(btnPanel);
		layout.putConstraint(SpringLayout.WEST, btnPanel, 5, SpringLayout.WEST, cont);
		layout.putConstraint(SpringLayout.NORTH, btnPanel, 5, SpringLayout.SOUTH, jsp);
		layout.putConstraint(SpringLayout.EAST, btnPanel, -5, SpringLayout.EAST, cont);

		HideListener listener = new HideListener();
		okBtn.addActionListener(listener);
		okBtn.addKeyListener(listener);
		dbPropertiesArea.addKeyListener(listener);
	}

	private class HideListener extends KeyAdapter implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e){
			setVisible(false);
		}

		public void keyPressed(KeyEvent ke){
			if (KeyEvent.VK_ESCAPE == ke.getKeyCode())
				setVisible(false);
		}

	}

}
