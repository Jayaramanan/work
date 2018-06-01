/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.controller.appconf.attributes;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

public class AdvancedViewCheckBoxListener implements ItemListener{

	private AttributeEditController controller;
	private boolean enabled = true;

	public AdvancedViewCheckBoxListener(AttributeEditController controller){
		this.controller = controller;
	}

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}

	@Override
	public void itemStateChanged(ItemEvent e){
		Object obj = e.getSource();

		if (!enabled || !(obj instanceof JCheckBox))
			return;

		JCheckBox cb = (JCheckBox) obj;
		boolean selected = cb.isSelected();
		controller.setAdvancedView(selected);
		if (!selected){
			controller.resetSorts();
		}

	}

}
