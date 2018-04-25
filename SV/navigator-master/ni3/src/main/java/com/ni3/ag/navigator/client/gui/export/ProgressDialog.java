/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.export;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.Timer;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.model.SystemGlobals;

public class ProgressDialog extends Ni3Dialog{
	private static final long serialVersionUID = 3184826064454063919L;
	private JProgressBar progressBar;
	private JButton closeButton;
	private Timer timer;
	private String btnText = UserSettings.getWord("Close");
	private int closeDelay;

	/**
	 * @param title
	 *            title
	 * @param closeDelay
	 *            delay in seconds before dialog closed
	 */
	public ProgressDialog(String title, int closeDelay){
		super();
		setTitle(title);
		this.closeDelay = closeDelay;
		initComponents();
	}

	protected void initComponents(){
		Component c = getContentPane();
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		progressBar = new JProgressBar(0, 100);
		progressBar.setIndeterminate(true);
		add(progressBar);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(300, 120);
		setPreferredSize(new Dimension(300, 75));
		setMinimumSize(new Dimension(300, 75));
		setLocationRelativeTo(SystemGlobals.MainFrame);
		closeButton = new JButton();
		add(closeButton);

		layout.putConstraint(SpringLayout.WEST, progressBar, 10, SpringLayout.WEST, c);
		layout.putConstraint(SpringLayout.NORTH, progressBar, 10, SpringLayout.NORTH, c);
		layout.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, c);
		layout.putConstraint(SpringLayout.SOUTH, progressBar, 40, SpringLayout.NORTH, c);

		layout.putConstraint(SpringLayout.WEST, closeButton, 100, SpringLayout.WEST, c);
		layout.putConstraint(SpringLayout.NORTH, closeButton, 50, SpringLayout.NORTH, c);
		layout.putConstraint(SpringLayout.EAST, closeButton, 100, SpringLayout.WEST, closeButton);

		closeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		closeButton.setVisible(false);
	}

	public JProgressBar getProgressBar(){
		return progressBar;
	}

	public JButton getCloseButton(){
		return closeButton;
	}

	public void setFinished(){
		progressBar.setIndeterminate(false);
		progressBar.setString(UserSettings.getWord("Finished"));
		progressBar.setStringPainted(true);
		closeButton.setVisible(true);
		closeButton.setText(btnText + " (" + closeDelay + ")");

		timer = new Timer(1000, new ActionListener(){
			int i = closeDelay;

			public void actionPerformed(ActionEvent e){
				if (i > 0){
					closeButton.setText(btnText + " (" + i + ")");
					i--;
				} else{
					ProgressDialog.this.setVisible(false);
					ProgressDialog.this.dispose();
				}
			}

		});
		timer.setInitialDelay(0);
		timer.setRepeats(true);
		timer.start();
	}

	@Override
	public void dispose(){
		super.dispose();
		if (timer != null){
			timer.stop();
			timer = null;
		}
	}
}
