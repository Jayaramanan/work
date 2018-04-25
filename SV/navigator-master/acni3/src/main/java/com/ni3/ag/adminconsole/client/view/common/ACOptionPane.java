/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ACOptionPane{

	public static final int YES_OPTION = JOptionPane.YES_OPTION;
	public static final int NO_OPTION = JOptionPane.NO_OPTION;
	public static final int YES_NO_OPTION = JOptionPane.YES_NO_OPTION;
	public static final int INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE;

	public static int showConfirmDialog(Component view, String msg, String title){
		int n = JOptionPane.showOptionDialog(view, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
		        null, new String[] { Translation.get(TextID.Yes), Translation.get(TextID.No) }, // the titles of buttons
		        Translation.get(TextID.Yes)); // default button title
		return n;
	}

	public static String showInputDialog(Component view, String msg, String title){
		return showInputDialog(view, msg, title, JOptionPane.QUESTION_MESSAGE, null, null, null);
	}

	public static String showInputDialog(Component view, String msg, String title, int dialogType, Icon icon,
	        Object[] values, Object defaultValue){
		CustomInputDialog customDialog = new CustomInputDialog(title, msg, view, dialogType, icon, values, defaultValue);
		String s = customDialog.getValidatedText();
		return s;
	}

	static class CustomInputDialog extends JDialog implements ActionListener, PropertyChangeListener{
		private static final long serialVersionUID = 1L;
		private String typedText = null;
		private JComponent inputField;

		private JOptionPane optionPane;

		/**
		 * Returns null if the typed string was invalid; otherwise, returns the string as the user entered it.
		 */
		public String getValidatedText(){
			return typedText;
		}

		/** Creates the reusable dialog. */
		public CustomInputDialog(String title, String msg, Component parent, int dialogType, Icon icon, Object[] values,
		        Object defaultValue){
			super((Frame) ACMain.getMainFrame(), true);

			setTitle(title);

			if (values == null)
				inputField = new JTextField(10);
			else
				inputField = new JComboBox(values);

			// Create an array of the text and components to be displayed.
			Object[] array = { msg, inputField };

			// Create the JOptionPane.
			optionPane = new JOptionPane(array, dialogType, JOptionPane.YES_NO_OPTION, icon, new String[] {
			        Translation.get(TextID.Yes), Translation.get(TextID.No) }, Translation.get(TextID.Yes));

			// Make this dialog display it.
			setContentPane(optionPane);

			// Handle window closing correctly.
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent we){
					/*
					 * Instead of directly closing the window, we're going to change the JOptionPane's value property.
					 */
					optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
				}
			});

			// Ensure the text field always gets the first focus.
			addComponentListener(new ComponentAdapter(){
				public void componentShown(ComponentEvent ce){
					inputField.requestFocusInWindow();
				}
			});

			// Register an event handler that puts the text into the option pane.
			if (values == null)
				((JTextField) inputField).addActionListener(this);

			// Register an event handler that reacts to option pane state changes.
			optionPane.addPropertyChangeListener(this);

			setLocationRelativeTo(ACMain.getMainFrame());
			pack();
			setVisible(true);

		}

		/** This method handles events for the text field. */
		public void actionPerformed(ActionEvent e){
			optionPane.setValue(Translation.get(TextID.Yes));
		}

		/** This method reacts to state changes in the option pane. */
		public void propertyChange(PropertyChangeEvent e){
			String prop = e.getPropertyName();

			if (isVisible() && (e.getSource().equals(optionPane))
			        && (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))){
				Object value = optionPane.getValue();

				if (value == JOptionPane.UNINITIALIZED_VALUE){
					// ignore reset
					return;
				}

				// Reset the JOptionPane's value.
				// If you don't do this, then if the user
				// presses the same button next time, no
				// property change event will be fired.
				optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

				if (Translation.get(TextID.Yes).equals(value)){
					if (inputField instanceof JTextField)
						typedText = ((JTextField) inputField).getText();
					else
						typedText = ((JComboBox) inputField).getSelectedItem().toString();
				}
				clearAndHide();
			}
		}

		public void clearAndHide(){
			if (inputField instanceof JTextField)
				((JTextField) inputField).setText(null);
			setVisible(false);
		}
	}

}
