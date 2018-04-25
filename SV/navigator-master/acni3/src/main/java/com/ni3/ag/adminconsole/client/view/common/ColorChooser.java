/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ColorChooser implements ActionListener{

	private Color currentColor;
	private JColorChooser chooser;
	private JDialog colorDialog;

	public ColorChooser(Component parent){
		chooser = new JColorChooser();
		colorDialog = JColorChooser.createDialog(parent, Translation.get(TextID.ChooseColor), true, chooser, this, null);
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
