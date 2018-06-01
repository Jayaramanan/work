/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.languageadmin;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.languageadmin.LanguageView;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.LanguageModel;
import com.ni3.ag.adminconsole.shared.service.def.LanguageAdminService;


public class DeleteLanguageButtonListener extends ProgressActionListener{

	public DeleteLanguageButtonListener(LanguageController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		LanguageController controller = (LanguageController) getController();
		LanguageView view = controller.getView();
		view.clearErrors();
		LanguageModel model = controller.getModel();
		TreePath currentTreePath = view.getLeftPanel().getSelectionTreePath();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		int result = ACOptionPane.showConfirmDialog(view, Translation.get(TextID.ConfirmDeleteLanguage),
		        Translation.get(TextID.DeleteLanguage));
		if (result != ACOptionPane.YES_OPTION)
			return;

		Language currentLanguage = model.getCurrentLanguage();
		Language nextLanguage = getNextLanguage();
		if (currentLanguage != null){
			LanguageAdminService service = ACSpringFactory.getInstance().getLanguageAdminService();
			service.deleteLanguage(currentLanguage);
			controller.loadData();
			controller.refreshTableData(true);
			controller.updateTreeModel();
		}

		if (dbInstance != null){
			TreePath found = null;
			if (nextLanguage != null){
				currentTreePath = currentTreePath.getParentPath();
				currentTreePath = currentTreePath.pathByAddingChild(nextLanguage);
				found = currentTreePath;
			} else
				found = new TreeModelSupport().findPathForEqualObject(dbInstance, view.getLeftPanel().getTreeModel());

			if (found != null)
				view.getLeftPanel().setSelectionTreePath(found);
		}
	}

	private Language getNextLanguage(){
		LanguageModel model = (LanguageModel) getController().getModel();
		List<Language> languages = model.getLanguages();
		int index = languages.indexOf(model.getCurrentLanguage());
		if (index == -1)
			return null;
		if (languages.size() == 1)
			return null;
		if (index == 0)
			return languages.get(1);
		else
			return languages.get(index - 1);
	}
}
