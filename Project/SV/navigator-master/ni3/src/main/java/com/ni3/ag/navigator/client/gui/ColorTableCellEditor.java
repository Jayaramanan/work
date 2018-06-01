/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

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

import com.ni3.ag.navigator.client.domain.UserSettings;

public class ColorTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener{
	private static final long serialVersionUID = -614895658127503939L;

	private Color currentColor;
	private JButton button;
	private JColorChooser chooser;
	private JDialog colorDialog;
	private static final String EDIT = "edit";

	public ColorTableCellEditor(Component parent){
		this(parent, true);
	}

	public ColorTableCellEditor(Component parent, boolean storeAsHex){
		button = new JButton();
		button.setActionCommand(EDIT);
		button.addActionListener(this);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(true);

		chooser = new JColorChooser();
		filterPanels();

		colorDialog = new JDialog(); //JColorChooser.createDialog(parent, UserSettings.getWord("ChooseColor"), true, chooser, this, null);
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
		if (value instanceof Color){
			currentColor = (Color) value;
		} else{
			currentColor = null;
		}
		button.setBackground(currentColor);
		return button;
	}

	public Object getCellEditorValue(){
		return currentColor;
	}
}
