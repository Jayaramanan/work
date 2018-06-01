/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;

import javax.swing.JComboBox;

import org.apache.log4j.Logger;

public class ACComboBox extends JComboBox implements ChangeResetable{
	private static final long serialVersionUID = -9044741603042733572L;
	private Object startValue;
	private boolean setSelectedItemCalled;
	private static Logger log = Logger.getLogger(ACComboBox.class);

	public ACComboBox(){
		
	}

	public ACComboBox(Object[] values){
		super(values);
	}

	@Override
	protected void selectedItemChanged(){
		super.selectedItemChanged();
		log.debug("Changed: current = " + getSelectedItem() + "   start: " + startValue);
		setFontColor();
	}

	@Override
	public void setSelectedItem(Object anObject){
		super.setSelectedItem(anObject);
		if (!setSelectedItemCalled){
			setSelectedItemCalled = true;
			startValue = getSelectedItem();
			log.debug("START_VALUE: " + startValue);
			setFontColor();
		}
	}

	public void setInitialSelectedItem(Object anObject){
		super.setSelectedItem(anObject);
		startValue = getSelectedItem();
		setSelectedItemCalled = true;
		setFontColor();
	}

	@Override
	public void setSelectedIndex(int anIndex){
		super.setSelectedIndex(anIndex);
		if (!setSelectedItemCalled){
			setSelectedItemCalled = true;
			startValue = getSelectedItem();
			log.debug("START_VALUE: " + startValue);
			setFontColor();
		}
	}

	private void setFontColor(){
		Object current = getSelectedItem();
		log.debug("Start: " + startValue + "  Current: " + current);
		if (startValue != null && !startValue.equals(current))
			setForeground(Color.BLUE);
		else if (current == startValue)
			setForeground(Color.BLACK);
		else if (current != null && !current.equals(startValue))
			setForeground(Color.BLUE);
		else
			setForeground(Color.BLACK);
	}

	@Override
	public void resetChanges(){
		startValue = getSelectedItem();
		setSelectedItemCalled = false;
		setFontColor();
	}

	@Override
	public boolean isChanged(){
		Object current = getSelectedItem();
		log.debug("Start: " + startValue + "  Current: " + current);
		if (startValue != null && !startValue.equals(current))
			return true;
		else if (current != null && !current.equals(startValue))
			return true;
		else
			return false;
	}
}
