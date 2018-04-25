/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.vers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;


import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACAutoCompleteTextArea;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ModuleParamsCellEditor extends AbstractCellEditor implements TableCellEditor{
	private static final long serialVersionUID = 3409803607155705382L;
	private JPanel panel;
	private JButton editButton;
	private JLabel label;
	private ACAutoCompleteTextArea paramsArea;
	private Object currentValue;
	private boolean ok;
	private JDialog editDialog;

	public ModuleParamsCellEditor(){
		paramsArea = new ACAutoCompleteTextArea(null);
		paramsArea.addKeyListener(new ParamsEditDialogKeyListener());
		paramsArea.setAutoCompleteItems(Module.PARAMS);
		paramsArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
		editButton = new JButton("...");
		editButton.setPreferredSize(new Dimension(20, 20));
		label = new JLabel();
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(label, BorderLayout.CENTER);
		panel.add(editButton, BorderLayout.EAST);
		editButton.addActionListener(new ModuleParamsEditButtonListener());
	}

	@Override
	public Object getCellEditorValue(){
		return currentValue;
	}

	@Override
	public boolean isCellEditable(EventObject anEvent){
		if (anEvent instanceof MouseEvent){
			return ((MouseEvent) anEvent).getClickCount() >= 2;
		}
		return true;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		currentValue = value;
		String s = "" + value;
		int index = s.indexOf("\n");
		if (index > 0)
			s = s.substring(0, index);
		label.setText(s);
		return panel;
	}

	private void createEditDialog(){
		editDialog = new JDialog();
		ImageIcon frameIcon = new ImageIcon(ACMain.class.getResource("/images/Ni3.png"));
		editDialog.setIconImage(frameIcon.getImage());
		editDialog.setModal(true);
		editDialog.getContentPane().setLayout(new BorderLayout());
		editDialog.getContentPane().add(paramsArea, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		buttonPanel.add(new JButton(new EditAction(TextID.Ok)));
		buttonPanel.add(new JButton(new EditAction(TextID.Cancel)));
		editDialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		editDialog.setSize(300, 200);
		editDialog.setLocationRelativeTo(null);
		editDialog.addKeyListener(new ParamsEditDialogKeyListener());
	}

	private class ModuleParamsEditButtonListener implements ActionListener{

		ModuleParamsEditButtonListener(){
			createEditDialog();
		}

		@Override
		public void actionPerformed(ActionEvent e){
			paramsArea.setText((String) currentValue);
			paramsArea.requestFocus();
			editDialog.setVisible(true);
			if (ok)
				currentValue = paramsArea.getText();
			fireEditingStopped();
		}
	}

	@SuppressWarnings("serial")
	private class EditAction extends AbstractAction{
		private TextID id;

		public EditAction(TextID id){
			super(Translation.get(id));
			this.id = id;
		}

		@Override
		public void actionPerformed(ActionEvent e){
			ok = id == TextID.Ok;
			editDialog.setVisible(false);
		}
	}

	private class ParamsEditDialogKeyListener implements KeyListener{
		@Override
		public void keyTyped(KeyEvent e){
			if (e.getKeyChar() == KeyEvent.VK_ESCAPE){
				ok = false;
				editDialog.setVisible(false);
			} else if (e.getKeyChar() == KeyEvent.VK_ENTER && e.isControlDown()){
				ok = true;
				editDialog.setVisible(false);
			}
		}

		@Override
		public void keyPressed(KeyEvent e){
		}

		@Override
		public void keyReleased(KeyEvent e){
		}
	}
}
