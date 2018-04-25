
/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ColorTableCellEditor;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class HaloColorTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener{

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(ColorTableCellEditor.class);

	private Color currentColor;
	private String currentString;
	private JButton button;
	private JColorChooser chooser;
	private JDialog colorDialog;
	private JPopupMenu menu;
	private JTable table;

	private static final String SHOW_POPUP = "popup";
	private static final String EMPTY = "empty";
	private static final String EDIT_COLOR = "edit";
	private static final String AUTO = "auto";
	private static final String RANDOM = "random";

	public HaloColorTableCellEditor(){
		button = new JButton();
		button.setActionCommand(SHOW_POPUP);
		button.addActionListener(this);
		button.setContentAreaFilled(false);
		button.setOpaque(true);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setMargin(new Insets(0, -2, 0, 0));
		button.addActionListener(this);
		button.setFocusable(false);

		createPopupMenu();

		chooser = new JColorChooser();

		filterPanels();

		colorDialog = new JDialog();//JColorChooser.createDialog(button, Translation.get(TextID.ChooseColor), true, chooser, this, null);
	}

	private void createPopupMenu(){
		menu = new JPopupMenu();
		JMenuItem itemEmpty = new JMenuItem(Translation.get(TextID.HaloEmpty));
		JMenuItem itemColor = new JMenuItem(Translation.get(TextID.HaloChooseColor));
		JMenuItem itemAutomatic = new JMenuItem(Translation.get(TextID.HaloAutomatic));
		JMenuItem itemRandom = new JMenuItem(Translation.get(TextID.HaloRandom));

		itemEmpty.addActionListener(this);
		itemColor.addActionListener(this);
		itemAutomatic.addActionListener(this);
		itemRandom.addActionListener(this);

		itemEmpty.setActionCommand(EMPTY);
		itemColor.setActionCommand(EDIT_COLOR);
		itemAutomatic.setActionCommand(AUTO);
		itemRandom.setActionCommand(RANDOM);

		menu.add(itemEmpty);
		menu.add(itemColor);
		menu.add(itemAutomatic);
		menu.add(itemRandom);
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
		if (SHOW_POPUP.equals(e.getActionCommand())){
			Point location = button.getLocation();
			int height = button.getHeight();
			menu.show(table, location.x, location.y + height);
		} else if (EMPTY.equals(e.getActionCommand())){
			currentString = null;
			currentColor = null;
			fireEditingStopped();
		} else if (AUTO.equals(e.getActionCommand())){
			currentString = "A";
			currentColor = null;
			fireEditingStopped();
		} else if (RANDOM.equals(e.getActionCommand())){
			currentString = "R";
			currentColor = null;
			fireEditingStopped();
		} else if (EDIT_COLOR.equals(e.getActionCommand())){
			button.setBackground(currentColor);
			chooser.setColor(currentColor);
			colorDialog.setVisible(true);
			fireEditingStopped();
		} else{
			currentColor = chooser.getColor();
			log.debug("Color selected: " + currentColor);
			fireEditingStopped();
		}
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		String colorStr = (String) value;
		java.awt.Color c = null;
		if (colorStr != null && !colorStr.equalsIgnoreCase("A") && !colorStr.equalsIgnoreCase("R")){
			try{
				c = java.awt.Color.decode(colorStr);
				currentString = null;
			} catch (NumberFormatException ex){
				log.warn("Not valid hex color: " + colorStr);
			}
		} else{
			currentString = colorStr;
		}

		currentColor = c;
		button.setBackground(c);
		button.setText("A".equals(currentString) ? Translation.get(TextID.HaloAutomatic) : Translation
		        .get(TextID.HaloRandom));
		if (this.table == null){
			this.table = table;
		}
		return button;
	}

	public Object getCellEditorValue(){
		if (currentColor == null){
			return currentString;
		}
		String rgb = Integer.toHexString(currentColor.getRGB());
		rgb = "#" + rgb.substring(2, rgb.length());
		return rgb;
	}
}