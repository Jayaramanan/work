/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.settings;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.RowSorter.SortKey;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.appconf.SettingsView;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;


public class UpdateButtonListener extends ProgressActionListener{
	private SettingsController controller;

	private ACValidationRule appSettingsRule, groupSettingsRule, userSettingsRule, complexitySettingRule;

	public UpdateButtonListener(SettingsController settingsController){
		super(settingsController);
		controller = settingsController;
		appSettingsRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("applicationSettingsValidationRule");
		groupSettingsRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("groupSettingsValidationRule");
		userSettingsRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("userSettingsValidationRule");
		complexitySettingRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("passwordComplexitySettingRule");
	}

	@Override
	public void performAction(ActionEvent e){
		SettingsView view = (SettingsView) controller.getView();

		Setting selectedSetting = view.getSelectedSetting();
		TreePath selectedPath = view.getUserTree().getSelectionPath();
		List<? extends SortKey> sorting = view.getTableSorting();

		boolean ok = save();
		if (!ok){
			return;
		}
		controller.reloadData();
		if (selectedPath != null){
			TreeModel treeModel = view.getUserTree().getModel();
			TreePath found = new TreeModelSupport().findPathByNodes(selectedPath.getPath(), treeModel);
			view.getUserTree().setSelectionPath(found);
			view.setTableSorting(sorting);
			view.setActiveTableRow(selectedSetting);
		}
		controller.updateUserSettingTree();
		view.resetEditedFields();
	}

	public boolean save(){
		SettingsModel model = (SettingsModel) controller.getModel();
		if (!model.isCurrent())
			return true;
		SettingsView view = (SettingsView) controller.getView();

		view.stopCellEditing();
		view.clearErrors();
		controller.populateDataToModel(model, view);

		if (model.isCurrentApplication()){
			DatabaseInstance db = SessionData.getInstance().getCurrentDatabaseInstance();
			if (!db.isConnected())
				return true;
		} else if (!model.isCurrentGroup() && !model.isCurrentUser())
			return false;

		boolean success = true;
		if (model.isCurrentApplication()){
			if (!appSettingsRule.performCheck(model)){
				view.renderErrors(appSettingsRule.getErrorEntries());
				success = false;
			} else if (!complexitySettingRule.performCheck(model)){
				view.renderErrors(complexitySettingRule.getErrorEntries());
				success = false;
			}
		} else if (model.isCurrentGroup() && !groupSettingsRule.performCheck(model)){
			view.renderErrors(groupSettingsRule.getErrorEntries());
			success = false;
		} else if (model.isCurrentUser() && !userSettingsRule.performCheck(model)){
			view.renderErrors(userSettingsRule.getErrorEntries());
			success = false;
		}

		if (success){
			controller.submitSettings();
			return true;
		}

		return false;
	}

}
