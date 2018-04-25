/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useractivity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FilterModeComboListener implements ActionListener{
	private UserActivityController controller;

	public FilterModeComboListener(UserActivityController userActivityController){
		this.controller = userActivityController;
	}

	@Override
	public void actionPerformed(ActionEvent e){
		controller.refreshFilterCombo();
	}
}
