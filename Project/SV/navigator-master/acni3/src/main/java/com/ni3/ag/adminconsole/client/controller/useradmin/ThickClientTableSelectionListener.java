/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ni3.ag.adminconsole.client.view.useradmin.ThickClientPanel;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.domain.User;

public class ThickClientTableSelectionListener implements ListSelectionListener{

	private UserAdminController controller;

	public ThickClientTableSelectionListener(UserAdminController controller){
		this.controller = controller;
	}

	@Override
	public void valueChanged(ListSelectionEvent e){

		if (e.getValueIsAdjusting())
			return;
		UserAdminView view = controller.getView();
		ThickClientPanel panel = view.getThickClientPanel();
		User selectedUser = panel.getSelectedUser();
		if (selectedUser != null)
			controller.populateDataToSequenceRangeTable(selectedUser);

	}

}
