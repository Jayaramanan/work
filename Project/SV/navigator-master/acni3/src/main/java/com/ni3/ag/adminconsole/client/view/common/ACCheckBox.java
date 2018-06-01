/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import org.apache.log4j.Logger;

public class ACCheckBox extends JCheckBox implements ChangeResetable{

	private static final long serialVersionUID = 5073760403539942457L;
	private Boolean startValue = null;
	private static Logger log = Logger.getLogger(ACCheckBox.class);
	private boolean initValueSet = false;

	public ACCheckBox(){
		super();
		addListener();
	}

	public ACCheckBox(String text){
		super(text);
		addListener();
	}

	public ACCheckBox(String text, boolean initialValue){
		super(text, initialValue);
		startValue = initialValue;
		initValueSet = true;
		addListener();
	}

	private void addListener(){
		addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e){
				setFontColor();
			}
		});
	}

	@Override
	public void setSelected(boolean b){
		super.setSelected(b);
		if (!initValueSet){
			startValue = b;
			initValueSet = true;
		}
		setFontColor();
	}

	private void setFontColor(){
		Boolean current = isSelected();
		log.debug("Start: " + startValue + "  Current: " + current);
		if (startValue != current){
			setForeground(Color.BLUE);
		} else{
			setForeground(Color.BLACK);
		}
	}

	@Override
	public void resetChanges(){
		startValue = isSelected();
		initValueSet = false;
		setFontColor();
	}

	@Override
	public boolean isChanged(){
		return startValue != isSelected();
	}

	public void readdOwnListener(){
		addListener();
	}
}
