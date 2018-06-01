/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.common;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Ni3OptionPane{

	public static void showMessageDialog(Component parent, String message){
		showMessageDialog(parent, message, "", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showMessageDialog(Component parent, String message, String title, int messageType)
	        throws HeadlessException{
		showMessageDialog(parent, message, title, messageType, null);
	}

	public static void showMessageDialog(Component parent, String message, String title, int messageType, Icon icon)
	        throws HeadlessException{
		showOptionDialog(parent, message, title, JOptionPane.DEFAULT_OPTION, messageType, icon, null, null);
	}

	public static int showConfirmDialog(Component parent, String message, String title, int optionType, int messageType){
		return showOptionDialog(parent, message, title, optionType, messageType, null, null, null);
	}

	public static int showConfirmDialog(Component parent, String message, String title, int optionType, int messageType,
	        Icon icon){
		return showOptionDialog(parent, message, title, optionType, messageType, icon, null, null);
	}

	public static int showOptionDialog(Component parent, Object message, String title, int optionType, int messageType,
	        Icon icon, Object[] options, Object initialValue) throws HeadlessException{
		JOptionPane optionPane = new JOptionPane(message, messageType, optionType, icon, options, initialValue);

		JDialog dialog = optionPane.createDialog(parent, title);

		optionPane.selectInitialValue();

		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);

		dialog.setAlwaysOnTop(false);
		dialog.dispose();

		Object selectedValue = optionPane.getValue();

		int result = JOptionPane.CLOSED_OPTION;

		if (selectedValue != null){
			if (options == null){
				if (selectedValue instanceof Integer)
					result = ((Integer) selectedValue).intValue();
			} else{
				for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++){
					if (options[counter].equals(selectedValue)){
						result = counter;
						break;
					}
				}
			}
		}
		return result;
	}
}
