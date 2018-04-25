/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.view.thickclient.maps.ImageLoader;
import com.ni3.ag.adminconsole.util.TimeUtil;

public class ACCalendarDialog extends JDialog implements ActionListener, MouseListener, ChangeListener, KeyListener{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ACCalendarDialog.class);
	private static final Insets ZERO_INSETS = new Insets(0, 0, 0, 0);

	/** Display Type */
	private DisplayType displayType;
	/** The Date */
	private GregorianCalendar calendar;
	/** Locale */
	private Locale locale;
	/** Is there a PM format */
	private boolean hasAM_PM = false;
	//
	private JButton[] dayButtons;
	private JButton todayBtn;

	/** First Date of week */
	private int firstDay;
	//
	private int currentDay;
	private int currentMonth;
	private int currentYear;
	private int current24Hour = 0;
	private int currentMinute = 0;
	private boolean setting = true;
	private boolean cancelled = true;
	private long lastClick = System.currentTimeMillis();
	private int lastDay = -1;

	private JPanel mainPanel = new JPanel();
	private JPanel monthPanel = new JPanel();
	private JComboBox cMonth = new JComboBox();
	private JSpinner cYear;
	private BorderLayout mainLayout = new BorderLayout();
	private JPanel dayPanel = new JPanel();
	private GridLayout dayLayout = new GridLayout();
	private GridBagLayout monthLayout = new GridBagLayout();
	private JButton bNext = new JButton();
	private JButton bBack = new JButton();
	private JPanel timePanel = new JPanel();
	private JComboBox fHour;
	private JLabel lTimeSep = new JLabel();
	private JSpinner fMinute;
	private JCheckBox cbPM = new JCheckBox();
	private JButton bOK = new JButton();
	private GridBagLayout timeLayout = new GridBagLayout();

	public ACCalendarDialog(DisplayType displayType){
		this(displayType, Locale.getDefault());
	}

	public ACCalendarDialog(DisplayType displayType, Locale locale){
		setModal(true);
		this.displayType = displayType;
		this.locale = locale;

		try{
			jbInit();
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		} catch (Exception ex){
			log.error(ex);
		}
	}

	public void showCalendar(Date date, Point location){
		loadData(date);
		pack();
		setLocation(location);
		setVisible(true);
	}

	private void jbInit() throws Exception{
		this.addKeyListener(this);
		fHour = new JComboBox(getHours());
		fMinute = new JSpinner(new MinuteModel(5)); // 5 minute snap size
		cYear = new JSpinner(new SpinnerNumberModel(2000, 1900, 2100, 1));
		//
		mainPanel.setLayout(mainLayout);
		mainLayout.setHgap(2);
		mainLayout.setVgap(2);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 2));
		getContentPane().add(mainPanel);

		// Month Panel
		monthPanel.setLayout(monthLayout);
		monthPanel.add(bBack, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
		        new Insets(0, 0, 0, 0), 0, 0));
		monthPanel.add(cYear, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.SOUTHEAST,
		        GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		monthPanel.add(bNext, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
		        new Insets(0, 0, 0, 0), 0, 0));
		monthPanel.add(cMonth, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
		        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		monthPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

		mainPanel.add(monthPanel, BorderLayout.NORTH);
		cMonth.addActionListener(this);
		cYear.addChangeListener(this);
		bBack.setIcon(ImageLoader.loadIcon("/images/Previous16.png"));
		bBack.setMargin(new Insets(0, 0, 0, 0));
		bBack.addActionListener(this);
		bNext.setIcon(ImageLoader.loadIcon("/images/Next16.png"));
		bNext.setMargin(new Insets(0, 0, 0, 0));
		bNext.addActionListener(this);

		// Day Panel
		dayPanel.setLayout(dayLayout);
		dayLayout.setColumns(7);
		dayLayout.setHgap(2);
		dayLayout.setRows(7);
		dayLayout.setVgap(2);
		dayPanel.setBackground(Color.white);
		dayPanel.setOpaque(true);
		mainPanel.add(dayPanel, BorderLayout.CENTER);

		// Time Panel
		timePanel.setLayout(timeLayout);
		lTimeSep.setText(" : ");
		timePanel.add(fHour, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
		        GridBagConstraints.HORIZONTAL, new Insets(0, 6, 0, 0), 0, 0));
		timePanel.add(lTimeSep, new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER,
		        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		timePanel.add(fMinute, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
		        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		timePanel.add(cbPM, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
		        new Insets(0, 5, 0, 0), 0, 0));
		timePanel.add(bOK, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
		        GridBagConstraints.HORIZONTAL, new Insets(0, 70, 0, 2), 0, 0));
		mainPanel.add(timePanel, BorderLayout.SOUTH);
		fHour.addKeyListener(this); // Enter returns
		// JSpinner ignores KeyListener
		((JSpinner.DefaultEditor) fMinute.getEditor()).getTextField().addKeyListener(this);
		fMinute.addChangeListener(this);
		cbPM.addActionListener(this);
		cbPM.addKeyListener(this);
		bOK.setIcon(ImageLoader.loadIcon("/images/Ok16.png"));
		bOK.setMargin(new Insets(0, 1, 0, 1));
		bOK.setPreferredSize(new Dimension(50, 24));
		bOK.setMinimumSize(new Dimension(50, 24));
		bOK.addActionListener(this);
	}

	protected void processWindowEvent(WindowEvent e){
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_OPENED){
			todayBtn.requestFocus();
		}
	}

	private void loadData(Date startDate){
		calendar = new GregorianCalendar(locale);
		if (startDate == null)
			calendar.setTimeInMillis(System.currentTimeMillis());
		else
			calendar.setTime(startDate);
		firstDay = 2; // always start from Monday

		SimpleDateFormat formatDate = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, locale);
		// Short: h:mm a - HH:mm Long: h:mm:ss a z - HH:mm:ss z
		SimpleDateFormat formatTime = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.SHORT, locale);
		if (hasAM_PM)
			cbPM.setText(formatTime.getDateFormatSymbols().getAmPmStrings()[1]);
		else
			cbPM.setVisible(false);

		// Years
		currentYear = calendar.get(java.util.Calendar.YEAR);
		cYear.setEditor(new JSpinner.NumberEditor(cYear, "0000"));
		cYear.setValue(new Integer(currentYear));

		// Months -> 0=Jan 12=_
		String[] months = formatDate.getDateFormatSymbols().getMonths();
		for (int i = 0; i < months.length; i++){
			if (!"".equals(months[i]))
				cMonth.addItem(months[i]);
		}
		currentMonth = calendar.get(java.util.Calendar.MONTH) + 1; // Jan=0
		cMonth.setSelectedIndex(currentMonth - 1);

		// Week Days -> 0=_ 1=Su .. 7=Sa
		String[] days = formatDate.getDateFormatSymbols().getShortWeekdays(); // 0 is blank, 1 is Sunday
		for (int i = firstDay; i < 7 + firstDay; i++){
			int index = i > 7 ? i - 7 : i;
			dayPanel.add(createWeekday(days[index], new Color(220, 210, 255)), null);
		}

		// Days
		dayButtons = new JButton[6 * 7];
		currentDay = calendar.get(java.util.Calendar.DATE);
		for (int i = 0; i < 6; i++)
			// six weeks a month maximun
			for (int j = 0; j < 7; j++) // seven days
			{
				int index = i * 7 + j;
				dayButtons[index] = createDay();
				dayPanel.add(dayButtons[index], null);
			}

		// Today button
		dayButtons[dayButtons.length - 1].setForeground(Color.green);
		dayButtons[dayButtons.length - 1].setText("*");
		dayButtons[dayButtons.length - 1].setToolTipText("Today");

		// Date/Time
		current24Hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
		currentMinute = calendar.get(java.util.Calendar.MINUTE);

		// What to show
		timePanel.setVisible(displayType.equals(DisplayType.DateTime));

		// update UI from m_current...
		setting = false;
		setCalendar();
	}

	private JLabel createWeekday(String title, Color color){
		JLabel label = new JLabel(title);
		label.setBackground(color);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		label.setRequestFocusEnabled(false);
		label.setOpaque(true);
		return label;
	}

	private JButton createDay(){
		JButton button = new JButton();
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setMargin(ZERO_INSETS);
		button.addActionListener(this);
		button.addMouseListener(this);
		button.addKeyListener(this);
		button.setFont(new Font(button.getFont().getFamily(), Font.BOLD, button.getFont().getSize()));
		button.setPreferredSize(new Dimension(30, 20));

		return button;
	}

	private Object[] getHours(){
		Object[] retValue = new Object[hasAM_PM ? 12 : 24];
		if (hasAM_PM){
			retValue[0] = "12";
			for (int i = 1; i < 10; i++)
				retValue[i] = " " + String.valueOf(i);
			for (int i = 10; i < 12; i++)
				retValue[i] = String.valueOf(i);
		} else{
			for (int i = 0; i < 10; i++)
				retValue[i] = "0" + String.valueOf(i);
			for (int i = 10; i < 24; i++)
				retValue[i] = String.valueOf(i);
		}
		return retValue;
	}

	private void setCalendar(){
		if (setting)
			return;

		// --- Set Month & Year
		setting = true;
		cMonth.setSelectedIndex(currentMonth - 1);
		cYear.setValue(new Integer(currentYear));
		setting = false;

		// --- Set Day
		// what is the first day in the selected month?
		calendar.set(currentYear, currentMonth - 1, 1); // Month is zero based
		int dayOne = calendar.get(java.util.Calendar.DAY_OF_WEEK);
		int lastDate = calendar.getActualMaximum(java.util.Calendar.DATE);
		// convert to index
		dayOne -= firstDay;
		if (dayOne < 0)
			dayOne += 7;
		lastDate += dayOne - 1;
		GregorianCalendar tmpCalendar = new GregorianCalendar();
		int prevMonthLastDate = 0;
		if (currentMonth >= 3){
			tmpCalendar.set(currentYear, currentMonth - 2, 1);
			prevMonthLastDate = tmpCalendar.getActualMaximum(java.util.Calendar.DATE);
		} else{
			tmpCalendar.set(currentYear, currentMonth - 2 + 12, 1);
			prevMonthLastDate = tmpCalendar.getActualMaximum(java.util.Calendar.DATE);
		}

		// for all buttons but the last
		int curDay = 1;
		for (int i = 0; i < dayButtons.length - 1; i++){
			if (i >= dayOne && i <= lastDate){
				tmpCalendar.set(currentYear, currentMonth - 1, curDay);
				int weekDay = tmpCalendar.get(java.util.Calendar.DAY_OF_WEEK);
				if (currentDay == curDay){
					dayButtons[i].setForeground(Color.BLACK);
					todayBtn = dayButtons[i];
					todayBtn.requestFocus();
				} else if (weekDay == 1 || weekDay == 7){// Weekend code goes here
					dayButtons[i].setForeground(new Color(255, 180, 10));
				} else{
					dayButtons[i].setForeground(new Color(90, 140, 255));
				}
				dayButtons[i].setText(String.valueOf(curDay++));
				dayButtons[i].setEnabled(true);
			} else{
				if (i < dayOne){
					dayButtons[i].setText(String.valueOf(prevMonthLastDate - dayOne + i + 1));
					dayButtons[i].setEnabled(false);
					dayButtons[i].setForeground(Color.GRAY);
				} else{
					dayButtons[i].setText(String.valueOf(i - lastDate));
					dayButtons[i].setEnabled(false);
					dayButtons[i].setForeground(Color.GRAY);
				}
			}
		}

		// Set Hour
		boolean pm = current24Hour > 12;
		int index = current24Hour;
		if (pm && hasAM_PM)
			index -= 12;
		if (index < 0 || index >= fHour.getItemCount())
			index = 0;
		fHour.setSelectedIndex(index);
		// Set Minute 168/209/255
		int m = calendar.get(java.util.Calendar.MINUTE);
		fMinute.setValue(new Integer(m));
		// Set PM
		cbPM.setSelected(pm);

		// Update Calendar
		calendar.set(currentYear, currentMonth - 1, currentDay, current24Hour, currentMinute, 0);
		calendar.set(java.util.Calendar.MILLISECOND, 0);
	}

	private void setTime(){
		// Hour
		int h = fHour.getSelectedIndex();
		current24Hour = h;
		if (hasAM_PM && cbPM.isSelected())
			current24Hour += 12;
		if (current24Hour < 0 || current24Hour > 23)
			current24Hour = 0;

		// Minute
		Integer ii = (Integer) fMinute.getValue();
		currentMinute = ii.intValue();
		if (currentMinute < 0 || currentMinute > 59)
			currentMinute = 0;
	}

	public Date getDate(){
		calendar.set(currentYear, currentMonth - 1, currentDay, current24Hour, currentMinute, 0);
		calendar.set(java.util.Calendar.MILLISECOND, 0);

		// Return value
		if (cancelled)
			return null;
		Date dt = calendar.getTime();
		if (displayType.equals(DisplayType.Date))
			dt = TimeUtil.truncToDate(dt);
		if (displayType.equals(DisplayType.DateTime))
			dt = TimeUtil.truncToMinute(dt);
		return dt;
	}

	public void actionPerformed(ActionEvent e){
		if (setting)
			return;
		setTime();

		if (e.getSource().equals(bOK)){
			cancelled = false;
			dispose();
			return;
		} else if (e.getSource().equals(bBack)){
			if (--currentMonth < 1){
				currentMonth = 12;
				currentYear--;
			}
			lastDay = -1;
		} else if (e.getSource().equals(bNext)){
			if (++currentMonth > 12){
				currentMonth = 1;
				currentYear++;
			}
			lastDay = -1;
		} else if (e.getSource() instanceof JButton){
			JButton b = (JButton) e.getSource();
			String text = b.getText();
			// Set to today's date
			if ("*".equals(text)){
				calendar.setTime(new Date());
				currentDay = calendar.get(java.util.Calendar.DATE);
				currentMonth = calendar.get(java.util.Calendar.MONTH) + 1;
				currentYear = calendar.get(java.util.Calendar.YEAR);
				current24Hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);

			}
			// we have a day
			else if (text.length() > 0){
				currentDay = Integer.parseInt(text);
				long currentClick = System.currentTimeMillis();
				if (currentDay == lastDay && currentClick - lastClick < 1000) // double click 1 second
				{
					cancelled = false;
					dispose();
					return;
				}
				lastClick = currentClick;
				lastDay = currentDay;
			}
		} else if (e.getSource().equals(cbPM)){
			setTime();
			lastDay = -1;
		} else{
			// Set Month
			currentMonth = cMonth.getSelectedIndex() + 1;
			lastDay = -1;
		}
		setCalendar();
	}

	public void stateChanged(ChangeEvent e){
		if (setting)
			return;

		// Set Minute
		if (e.getSource().equals(fMinute)){
			setTime();
			return;
		}
		// Set Year
		currentYear = ((Integer) cYear.getValue()).intValue();
		lastDay = -1;
		setCalendar();
	}

	public void mouseClicked(MouseEvent e){
		if (e.getClickCount() == 2){
			cancelled = false;
			dispose();
		}
	}

	public void mousePressed(MouseEvent e){
	}

	public void mouseEntered(MouseEvent e){
	}

	public void mouseExited(MouseEvent e){
	}

	public void mouseReleased(MouseEvent e){
	}

	public void keyReleased(KeyEvent e){
		// Day Buttons
		if (e.getSource() instanceof JButton){
			if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN){
				if (++currentMonth > 12){
					currentMonth = 1;
					currentYear++;
				}
				setCalendar();
				return;
			}
			if (e.getKeyCode() == KeyEvent.VK_PAGE_UP){
				if (--currentMonth < 1){
					currentMonth = 12;
					currentYear--;
				}
				setCalendar();
				return;
			}

			// Arrows
			int offset = 0;
			if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				offset = 1;
			else if (e.getKeyCode() == KeyEvent.VK_LEFT)
				offset = -1;
			else if (e.getKeyCode() == KeyEvent.VK_UP)
				offset = -7;
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
				offset = 7;
			if (offset != 0){
				calendar.add(java.util.Calendar.DAY_OF_YEAR, offset);
				currentDay = calendar.get(java.util.Calendar.DAY_OF_MONTH);
				currentMonth = calendar.get(java.util.Calendar.MONTH) + 1;
				currentYear = calendar.get(java.util.Calendar.YEAR);
				setCalendar();
				return;
			}
			// something else
			actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, ""));
		}

		// Pressed Enter anywhere
		if (e.getKeyCode() == KeyEvent.VK_ENTER){
			cancelled = false;
			setTime();
			dispose();
			return;
		}

		// Modified Hour/Miinute
		setTime();
		lastDay = -1;
	}

	public boolean isCancelled(){
		return cancelled;
	}

	public void keyTyped(KeyEvent e){
	}

	public void keyPressed(KeyEvent e){
	}

	private class MinuteModel extends SpinnerNumberModel{

		private static final long serialVersionUID = 1L;

		public MinuteModel(int snapSize){
			super(0, 0, 59, 1); // Integer Model
			m_snapSize = snapSize;
		}

		/** Snap size */
		private int m_snapSize;

		public Object getNextValue(){
			int minutes = ((Integer) getValue()).intValue();
			minutes += m_snapSize;
			if (minutes >= 60)
				minutes -= 60;
			//
			int steps = minutes / m_snapSize;
			return new Integer(steps * m_snapSize);
		}

		public Object getPreviousValue(){
			int minutes = ((Integer) getValue()).intValue();
			minutes -= m_snapSize;
			if (minutes < 0)
				minutes += 60;
			//
			int steps = minutes / m_snapSize;
			if (minutes % m_snapSize != 0)
				steps++;
			if (steps * m_snapSize > 59)
				steps = 0;
			return new Integer(steps * m_snapSize);
		}
	}

	public enum DisplayType{
		Date, DateTime
	};
}
