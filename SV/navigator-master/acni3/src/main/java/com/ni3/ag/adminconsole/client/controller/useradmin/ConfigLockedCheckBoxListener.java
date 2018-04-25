/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;

public class ConfigLockedCheckBoxListener implements ItemListener{

	private UserAdminController controller;
	private boolean enabled = true;

	public ConfigLockedCheckBoxListener(UserAdminController controller){
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
		if (!selected){
			UserAdminModel model = controller.getModel();
			GroupPrivilegesUpdater privilegesUpdater = new GroupPrivilegesUpdater(model.getSchemas());
			privilegesUpdater.resetLockedToUnlockedState(model.getCurrentGroup());
		}
		controller.updatePrivilegesTable(selected);

	}
}
