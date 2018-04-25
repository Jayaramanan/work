/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Rectangle;

import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import com.sun.java.swing.plaf.windows.*;

@SuppressWarnings("serial")
public class ACCustomComboBoxUIFactory{

	public static ComboBoxUI getNativeCustomComboBoxUI(){
		String osName = System.getProperty("os.name");
		if (osName == null || osName.isEmpty())
			osName = "win";
		osName = osName.toLowerCase();
		if (osName.startsWith("win"))
			return new ACWindowsCustomCoboBoxUI();
		else
			return new ACCustomCoboBoxUI();
	}

	public static class ACCustomCoboBoxUI extends BasicComboBoxUI{
		protected ACCustomCoboBoxUI(){
			super();
		}

		protected ComboPopup createPopup(){
			BasicComboPopup popup = new BasicComboPopup(comboBox){
				@Override
				protected Rectangle computePopupBounds(int px, int py, int pw, int ph){
					return super.computePopupBounds(px, py, Math.max(comboBox.getPreferredSize().width, pw), ph);
				}
			};
			popup.getAccessibleContext().setAccessibleParent(comboBox);
			return popup;
		}
	}

	@SuppressWarnings("restriction")
	public static class ACWindowsCustomCoboBoxUI extends WindowsComboBoxUI{
		protected ACWindowsCustomCoboBoxUI(){
			super();
		}

		protected ComboPopup createPopup(){
			BasicComboPopup popup = new BasicComboPopup(comboBox){
				@Override
				protected Rectangle computePopupBounds(int px, int py, int pw, int ph){
					return super.computePopupBounds(px, py, Math.max(comboBox.getPreferredSize().width, pw), ph);
				}
			};
			popup.getAccessibleContext().setAccessibleParent(comboBox);
			return popup;
		}
	}
}