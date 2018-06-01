/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.languageadmin;

import java.awt.event.ActionEvent;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.languageadmin.LanguageView;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.LanguageModel;
import com.ni3.ag.adminconsole.shared.service.def.LanguageAdminService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UpdatePropertyButtonListener extends ProgressActionListener{

	private LanguageController controller;

	private Logger log = Logger.getLogger(UpdatePropertyButtonListener.class);

	private ACValidationRule languagePropertyRule;
	private ACValidationRule languageNameRule;

	public UpdatePropertyButtonListener(LanguageController controller){
		super(controller);
		this.controller = controller;
		this.languagePropertyRule = (ACValidationRule) ACSpringFactory.getInstance().getBean(
		        "languagePropertyValidationRule");
		this.languageNameRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("languageNameValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		LanguageView view = controller.getView();
		UserLanguageProperty property = view.getSelectedLanguageProperty();

		if (!save()){
			return;
		}

		controller.reloadData();
		if (property != null)
			view.setSelectedLanguageProperty(property);

		view.resetEditedFields();
	}

	public boolean save(){
		log.debug("action performed");

		LanguageView view = controller.getView();
		LanguageModel model = controller.getModel();

		view.stopCellEditing();
		view.clearErrors();
		List<Language> languages = model.getLanguages();

		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected() || languages == null || languages.isEmpty()){
			return true;
		}

		if (!languagePropertyRule.performCheck(model)){
			controller.getView().renderErrors(languagePropertyRule.getErrorEntries());
			return false;
		}
		if (!languageNameRule.performCheck(model)){
			controller.getView().renderErrors(languageNameRule.getErrorEntries());
			return false;
		}

		LanguageAdminService service = ACSpringFactory.getInstance().getLanguageAdminService();
		service.saveOrUpdateLanguages(languages);

		return true;
	}

	public void refreshTable(){
		controller.refreshTableData(true);
	}
}
