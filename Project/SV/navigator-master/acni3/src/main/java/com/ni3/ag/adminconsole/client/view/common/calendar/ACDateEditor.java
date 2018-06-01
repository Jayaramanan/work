/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common.calendar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.Locale;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Logger;


import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.view.common.calendar.ACCalendarDialog.DisplayType;
import com.ni3.ag.adminconsole.client.view.thickclient.maps.ImageLoader;

public class ACDateEditor extends AbstractCellEditor implements TableCellEditor{
	private static final Logger log = Logger.getLogger(ACDateEditor.class);
	private static final long serialVersionUID = 1L;

	private JButton calendarButton;
	private DisplayType displayType;
	private JFormattedTextField dateField;
	private JPanel panel;
	private Locale locale;

	@Override
	public Object getCellEditorValue(){
		return dateField.getValue();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		dateField.setValue(value);
		return panel;
	}

	public ACDateEditor(){
		this(DisplayType.DateTime);
	}

	public ACDateEditor(DisplayType displayType){
		this.displayType = displayType;
		dateField = new JFormattedTextField(new SimpleDateFormat(DisplayType.Date.equals(displayType) ? "yyyy-MM-dd"
		        : "yyyy-MM-dd HH:mm"));
		dateField.setBorder(BorderFactory.createEmptyBorder());
		calendarButton = new JButton(ImageLoader.loadIcon("/images/Calendar16.png"));
		calendarButton.setMaximumSize(new Dimension(16, 16));
		calendarButton.setPreferredSize(new Dimension(16, 16));
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(dateField, BorderLayout.CENTER);
		panel.add(calendarButton, BorderLayout.EAST);
		calendarButton.addActionListener(new CalendarButtonActionListener());
		this.locale = LocaleParser.getLocaleByUserLanguage();
	}

	@Override
	public boolean isCellEditable(EventObject anEvent){
		if (anEvent instanceof MouseEvent){
			return ((MouseEvent) anEvent).getClickCount() >= 2;
		}
		return true;
	}

	private class CalendarButtonActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			Date date = (Date) dateField.getValue();
			log.debug("Old date: " + date);
			ACCalendarDialog calendar = new ACCalendarDialog(displayType, locale);
			ImageIcon frameIcon = new ImageIcon(ACMain.class.getResource("/images/Ni3.png"));
			calendar.setIconImage(frameIcon.getImage());
			calendar.showCalendar(date, panel.getLocationOnScreen());
			if (!calendar.isCancelled()){
				Date newDate = calendar.getDate();
				log.debug("New date: " + newDate);
				dateField.setValue(newDate);
			}
			fireEditingStopped();
		}
	}
}
