package com.ni3.ag.navigator.client.gui.common;

import java.awt.*;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;

import com.sun.java.swing.plaf.windows.WindowsComboBoxUI;

public class BigComboBoxDropDownUIFactory{

	public static ComboBoxUI getNativeCustomComboBoxUI(){
		String osName = System.getProperty("os.name");
		if (osName == null || osName.isEmpty())
			osName = "win";
		osName = osName.toLowerCase();
		if (osName.startsWith("win"))
			return new NWindowsCustomCoboBoxUI();
		else if(osName.contains("linux"))
			return new NMetalCustomCoboBoxUI();
		else
			return new NCustomCoboBoxUI();
	}

	public static class NCustomCoboBoxUI extends BasicComboBoxUI{
		protected NCustomCoboBoxUI(){
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

	public static class NMetalCustomCoboBoxUI extends MetalComboBoxUI{
		protected NMetalCustomCoboBoxUI(){
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
	public static class NWindowsCustomCoboBoxUI extends WindowsComboBoxUI{
		protected NWindowsCustomCoboBoxUI(){
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
