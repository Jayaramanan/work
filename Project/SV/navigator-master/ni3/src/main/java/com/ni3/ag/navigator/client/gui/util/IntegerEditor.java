/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.util;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class IntegerEditor extends DefaultCellEditor{

	private final static Logger log = Logger.getLogger(IntegerEditor.class);

	JFormattedTextField ftf;
	NumberFormat integerFormat;
	private Integer minimum, maximum;

	public IntegerEditor(){
		super(new JFormattedTextField());
		ftf = (JFormattedTextField) getComponent();
		minimum = new Integer(Integer.MIN_VALUE);
		maximum = new Integer(Integer.MAX_VALUE);

		integerFormat = NumberFormat.getIntegerInstance();
		NumberFormatter intFormatter = new NumberFormatter(integerFormat);
		intFormatter.setFormat(integerFormat);
		intFormatter.setMinimum(minimum);
		intFormatter.setMaximum(maximum);

		ftf.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
		ftf.setValue(minimum);
		ftf.setHorizontalAlignment(JTextField.TRAILING);
		ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);

		ftf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
		ftf.getActionMap().put("check", new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				if (!ftf.isEditValid()){
					Toolkit.getDefaultToolkit().beep();
					ftf.selectAll();
				} else{
					try{
						ftf.commitEdit();
						ftf.postActionEvent();
					} catch (java.text.ParseException exc){
						log.error(exc.getMessage(), exc);
					}
				}
			}
		});
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		JFormattedTextField ftf = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row,
		        column);
		ftf.setValue(value);
		return ftf;
	}

	public Object getCellEditorValue(){
		JFormattedTextField ftf = (JFormattedTextField) getComponent();
		Object o = ftf.getValue();
		if (o instanceof Integer){
			return o;
		} else if (o instanceof Number){
			return new Integer(((Number) o).intValue());
		} else{
			try{
				if (o != null)
					return integerFormat.parseObject(o.toString());
				else
					return null;
			} catch (ParseException exc){
				System.err.println("getCellEditorValue: can't parse o: " + o);
				log.error(exc.getMessage(), exc);
				return null;
			}
		}
	}

	public boolean stopCellEditing(){
		JFormattedTextField ftf = (JFormattedTextField) getComponent();
		if (ftf.isEditValid()){
			try{
				ftf.commitEdit();
			} catch (java.text.ParseException exc){
				log.error(exc.getMessage(), exc);
			}
		} else{
			Toolkit.getDefaultToolkit().beep();
			ftf.selectAll();
			return false;
		}
		return super.stopCellEditing();
	}
}
