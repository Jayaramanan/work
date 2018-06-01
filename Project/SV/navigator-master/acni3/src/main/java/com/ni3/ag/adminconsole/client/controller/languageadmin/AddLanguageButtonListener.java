/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.languageadmin;

import static com.ni3.ag.adminconsole.client.view.Translation.get;
import static com.ni3.ag.adminconsole.shared.language.TextID.MsgEnterNameOfNewLanguage;
import static com.ni3.ag.adminconsole.shared.language.TextID.NewLanguage;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.common.StringValidator;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.languageadmin.LanguageLeftPanel;
import com.ni3.ag.adminconsole.client.view.languageadmin.LanguageView;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.LanguageModel;
import com.ni3.ag.adminconsole.shared.service.def.LanguageAdminService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;


public class AddLanguageButtonListener extends ProgressActionListener{
	private LanguageController controller;
	private ACValidationRule rule;

	public AddLanguageButtonListener(LanguageController controller){
		super(controller);
		this.controller = controller;
		rule = (ACValidationRule) ACSpringFactory.getInstance().getBean("languageNameValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		LanguageView view = controller.getView();
		LanguageModel model = controller.getModel();
		view.clearErrors();

		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		String langName = ACOptionPane.showInputDialog(view, get(MsgEnterNameOfNewLanguage), get(NewLanguage));
		langName = StringValidator.validate(langName);
		if (langName == null){
			return;
		}

		Language language = new Language();
		language.setLanguage(langName);

		model.setCurrentLanguage(language);

		if (!rule.performCheck(model)){
			view.renderErrors(rule.getErrorEntries());
			return;
		}
		TreePath currentPath = view.getLeftPanel().getSelectionTreePath();

		LanguageAdminService service = ACSpringFactory.getInstance().getLanguageAdminService();
		Language newLanguage = service.saveOrUpdateLanguage(language);

		controller.loadData();
		controller.refreshTableData(true);
		controller.updateTreeModel();

		restoreSelection(currentPath, newLanguage);
	}

	private void restoreSelection(TreePath currentPath, Language newLanguage){
		LanguageLeftPanel leftPanel = controller.getView().getLeftPanel();
		AbstractTreeModel treeModel = leftPanel.getTreeModel();
		Object[] oldNodes = currentPath.getPath();
		Object[] newNodes = new Object[] { oldNodes[0], oldNodes[1], newLanguage };

		TreePath newPath = new TreeModelSupport().findPathByNodes(newNodes, treeModel);
		leftPanel.setSelectionTreePath(newPath);
	}
}