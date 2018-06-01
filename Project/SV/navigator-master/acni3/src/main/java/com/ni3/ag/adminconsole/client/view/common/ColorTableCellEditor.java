/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ColorTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener{
	private static final long serialVersionUID = -614895658127503939L;
	private static Logger log = Logger.getLogger(ColorTableCellEditor.class);

	private Color currentColor;
	private JButton button;
	private JColorChooser chooser;
	private JDialog colorDialog;
	private static final String EDIT = "edit";
	private boolean storeAsHex = true;
	private RgbColorConverter converter = null;

	public ColorTableCellEditor(){
		this(true);
	}

	public ColorTableCellEditor(boolean storeAsHex){
		this.storeAsHex = storeAsHex;
		if (!storeAsHex){
			converter = new RgbColorConverter();
		}
		button = new JButton();
		button.setActionCommand(EDIT);
		button.addActionListener(this);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(true);

		chooser = new JColorChooser();
		filterPanels();

		colorDialog = new JDialog();//JColorChooser.createDialog(button, Translation.get(TextID.ChooseColor), true, chooser, this, null);
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
		if (EDIT.equals(e.getActionCommand())){
			button.setBackground(currentColor);
			chooser.setColor(currentColor);
			colorDialog.setVisible(true);
			fireEditingStopped();
		} else{
			currentColor = chooser.getColor();
		}
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		String colorStr = (String) value;
		java.awt.Color c = null;
		if (colorStr != null){
			try{
				if (storeAsHex){
					c = java.awt.Color.decode(colorStr);
				} else{
					c = converter.getColor(colorStr);
				}

			} catch (NumberFormatException ex){
				log.warn("Not valid hex color: " + colorStr);
			}
		}
		currentColor = c;
		button.setBackground(c);
		return button;
	}

	public Object getCellEditorValue(){
		if (currentColor == null){
			return null;
		}
		String rgb = null;
		if (storeAsHex){
			rgb = Integer.toHexString(currentColor.getRGB());
			rgb = "#" + rgb.substring(2, rgb.length());
		} else{
			rgb = converter.getColorString(currentColor);
		}
		return rgb;
	}
}
