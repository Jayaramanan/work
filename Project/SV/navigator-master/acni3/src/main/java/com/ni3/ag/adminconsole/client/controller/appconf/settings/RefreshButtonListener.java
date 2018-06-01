/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.settings;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.appconf.SettingsView;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;

public class RefreshButtonListener extends ProgressActionListener{

	public RefreshButtonListener(SettingsController settingsController){
		super(settingsController);
	}

	@Override
	public void performAction(ActionEvent e){
		SettingsController controller = (SettingsController) getController();
		SettingsModel model = (SettingsModel) controller.getModel();
		if (!model.isCurrent())
			return;
		SettingsView view = (SettingsView) controller.getView();

		Setting selectedSetting = view.getSelectedSetting();
		TreePath selectedPath = view.getUserTree().getSelectionPath();

		view.stopCellEditing();
		view.clearErrors();
		controller.reloadData();

		if (selectedPath != null){
			TreeModel treeModel = view.getUserTree().getModel();
			TreePath found = new TreeModelSupport().findPathByNodes(selectedPath.getPath(), treeModel);
			view.getUserTree().setSelectionPath(found);
			view.setActiveTableRow(selectedSetting);
		}
		controller.updateUserSettingTree();
	}

}
