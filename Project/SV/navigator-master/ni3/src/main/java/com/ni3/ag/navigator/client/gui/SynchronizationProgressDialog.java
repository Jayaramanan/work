/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.ni3.ag.navigator.client.domain.SyncModule;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;

public class SynchronizationProgressDialog extends Ni3Dialog{
	private static final long serialVersionUID = 1L;
	public static final Integer IN_PROGRESS_STATUS = 1;
	public static final Integer OK_STATUS = 2;
	public static final Integer ERROR_STATUS = 3;
	private JButton okButton;
	private JButton stopButton;
	private JList list;
	private DefaultListModel listModel;
	private JPanel mainPanel;

	public SynchronizationProgressDialog(AbstractAction stopAction){
		super();
		setTitle(UserSettings.getWord("Synchronization"));
		initComponents(stopAction);
	}

	protected void initComponents(AbstractAction stopAction){
		// setModal(false);
		setSize(new Dimension(380, 400));

		mainPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		mainPanel.setLayout(layout);
		getContentPane().add(mainPanel);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(1, 2, 20, 0));

		stopButton = new JButton(stopAction);
		stopButton.setEnabled(false);
		bottomPanel.add(stopButton);

		okButton = new JButton(UserSettings.getWord("Ok"));
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setVisible(false);
			}
		});
		okButton.setEnabled(false);
		bottomPanel.add(okButton);

		mainPanel.add(bottomPanel);

		list = new JList();
		list.setEnabled(false);
		listModel = new DefaultListModel();
		list.setModel(listModel);
		list.setCellRenderer(new SyncItemListCellRenderer());
		list.setBackground(getBackground());
		list.setOpaque(false);
		mainPanel.add(list);

		layout.putConstraint(SpringLayout.NORTH, list, 10, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, list, -20, SpringLayout.EAST, mainPanel);
		layout.putConstraint(SpringLayout.WEST, list, 10, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.SOUTH, list, -40, SpringLayout.SOUTH, mainPanel);

		layout.putConstraint(SpringLayout.NORTH, bottomPanel, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, bottomPanel, 0, SpringLayout.HORIZONTAL_CENTER, mainPanel);
		// layout.putConstraint(SpringLayout.EAST, okButton, 130, SpringLayout.WEST, okButton);
	}

	public void addModuleProgress(SyncModule module){
		listModel.addElement(module);
		repaintList();
	}

	public void repaintList(){
		list.repaint();
		list.paintImmediately(0, 0, list.getWidth(), list.getHeight());
	}

	public void addString(final String str){
		listModel.addElement("<html><body><font size=\"5\" face=\"arial\">" + str + "</font></body></html>");
		repaintList();
	}

	public void addErrorString(final String str){
		listModel.addElement("<html><body>" + str + "</html></body>");
		repaintList();
	}

	public void showDialog(){
		setVisible(true);
		mainPanel.paintImmediately(0, 0, mainPanel.getWidth(), mainPanel.getHeight());
	}

	public void setStopButtonEnabled(boolean b){
		stopButton.setEnabled(b);
	}

	public void resetStopButtonLabel(){
		stopButton.setText(UserSettings.getWord("Stop"));
	}

	private class SyncItemListCellRenderer extends JPanel implements ListCellRenderer{
		private static final long serialVersionUID = 1L;

		private JLabel moduleLabel;
		private JLabel statusLabel;

		public SyncItemListCellRenderer(){
			moduleLabel = new JLabel();
			statusLabel = new JLabel();
			setLayout(new BorderLayout());
			add(moduleLabel, BorderLayout.CENTER);
			add(statusLabel, BorderLayout.EAST);

			setPreferredSize(new Dimension(150, 30));

			statusLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
		        boolean cellHasFocus){
			if (value instanceof SyncModule){
				SyncModule module = (SyncModule) value;
				moduleLabel.setText(module.toString());
				if (module.getStatus() == IN_PROGRESS_STATUS){
					statusLabel.setText(". . .");
					statusLabel.setForeground(Color.BLACK);
					statusLabel.setIcon(null);
				} else if (module.getStatus() == OK_STATUS){
					statusLabel.setIcon(null);
					String renderValue = "<html><body><font face=\"dialog\"  size=\"3\" color=\"#00FF00\"><b>"
					        + UserSettings.getWord("Ok") + "<b></font></body></html>";
					statusLabel.setText(renderValue);
				} else if (module.getStatus() == ERROR_STATUS){
					statusLabel.setIcon(null);
					String renderValue = "<html><body><font face=\"dialog\"  size=\"3\" color=\"#FF0000\"><b>"
					        + UserSettings.getWord("Error") + "<b></font></body></html>";
					statusLabel.setText(renderValue);
				}
			} else{
				String text = value != null ? value.toString() : null;
				moduleLabel.setText(text);
				statusLabel.setText(null);
				statusLabel.setIcon(null);
			}
			return this;
		}
	}

	public void setOkButtonEnabled(boolean b){
		okButton.setEnabled(b);
	}

}
