package com.ni3.ag.navigator.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.colorchooser.AbstractColorChooserPanel;

public class ColorChooser implements ActionListener{

	private Color currentColor;
	private JColorChooser chooser;
	private JDialog colorDialog;

	public ColorChooser(Component parent, String title){
		chooser = new JColorChooser();
		colorDialog = JColorChooser.createDialog(parent, title, true, chooser, this, null);
		filterPanels();
	}

	public Color chooseColor(Color initialColor){
		currentColor = initialColor;
		chooser.setColor(currentColor);
		colorDialog.setVisible(true);
		return currentColor;
	}

	private void filterPanels(){
		chooser.setPreviewPanel(new JPanel());
		AbstractColorChooserPanel[] oldPanels = chooser.getChooserPanels();
		for (int i = 0; i < oldPanels.length; i++){
			String clsName = oldPanels[i].getClass().getName();
			if (!"javax.swing.colorchooser.DefaultHSBChooserPanel".equals(clsName)){
				chooser.removeChooserPanel(oldPanels[i]);
			}
		}
	}

	public void actionPerformed(ActionEvent e){
		currentColor = chooser.getColor();
	}
}