/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;

@SuppressWarnings("serial")
public class JInputValuesDialog extends Ni3Dialog{
	JPanel panel;

	public static final int RET_CANCEL = 0;
	public static final int RET_OK = 1;
	private int returnStatus;

	JInputValuePane mainPane;

	private String[] values, formats;
	private Object[] defaultValues;

	public JInputValuesDialog(String title, String values[], Object defaultValues[], String formats[]){
		super();
		setModal(true);
		this.values = values;
		this.defaultValues = defaultValues;
		this.formats = formats;

		setTitle(UserSettings.getWord(title));

		returnStatus = RET_CANCEL;
		initComponents();
	}

	protected void initComponents(){
		panel = new JPanel();
		panel.setLayout(new BorderLayout());

		getContentPane().add(panel);

		mainPane = new JInputValuePane(values, defaultValues, formats);

		panel.add(mainPane, BorderLayout.CENTER);

		setSize(panel.getPreferredSize().width + 40, panel.getPreferredSize().height + 80);

		JPanel okcancel = new JPanel();

		JButton btn = new JButton(UserSettings.getWord("OK"));
		okcancel.add(btn);

		btn.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				okButtonActionPerformed(evt);
			}
		});

		btn = new JButton(UserSettings.getWord("Cancel"));
		okcancel.add(btn);
		btn.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				cancelButtonActionPerformed(evt);
			}
		});

		panel.add(okcancel, BorderLayout.SOUTH);
	}

	protected void onEnterAction(){
		doClose(RET_OK);
	}

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt){
		doClose(RET_OK);
	}

	/** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
	public int getReturnStatus(){
		return returnStatus;
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt){
		doClose(RET_CANCEL);
	}

	private void doClose(int retStatus){
		returnStatus = retStatus;
		setVisible(false);
		dispose();
	}

	public Object getValue(int ValueIndex){
		return mainPane.getValue(ValueIndex);
	}

	public Object getValue(String ValueName){
		return mainPane.getValue(ValueName);
	}

	@Override
	public void setVisible(boolean b){
		if (b)
			returnStatus = RET_CANCEL;
		super.setVisible(b);
	}
}
