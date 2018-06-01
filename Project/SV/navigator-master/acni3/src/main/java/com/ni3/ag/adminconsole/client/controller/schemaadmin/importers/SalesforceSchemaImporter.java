/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.controller.schemaadmin.importers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.SchemaAdminController;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SalesforceImportDialog;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;

public class SalesforceSchemaImporter implements Importer{
	private final static Logger log = Logger.getLogger(XMLSchemaImporter.class);
	private SchemaAdminController controller;
	private ActionListener loginListener;
	private ActionListener importListener;
	private SalesforceImportDialog dlg;

	public SalesforceSchemaImporter(SchemaAdminController controller){
		this.controller = controller;
		loginListener = new LoginActionListener(controller);
		importListener = new ImportButtonListener(controller);
		controller.getModel().setSalesforceUrl("https://login.salesforce.com/services/Soap/u/22.0/");
	}

	public boolean doImport(){
		SchemaAdminModel model = controller.getModel();
		SchemaAdminService schemaAdminService = ACSpringFactory.getInstance().getSchemaAdminService();

		boolean ok = true;
		getSalesforceDialog();
		dlg.showDialog(model.getSalesforceUrl(), model.getSalesforceUsername(), model.getSalesforcePassword());
		ok = dlg.isSuccess();
		if (ok){
			schemaAdminService.setAllInvalidationRequired(true, DataGroup.Attributes, DataGroup.Users, DataGroup.Schema);
			MainPanel2.setAllInvalidationNeeded(DataGroup.Attributes, DataGroup.Users, DataGroup.Schema);
		}
		return ok;
	}

	public SalesforceImportDialog getSalesforceDialog(){
		if (dlg == null){
			dlg = new SalesforceImportDialog();
			dlg.addLoginButtonListener(loginListener);
			dlg.addOkButtonListener(importListener);
		}
		return dlg;
	}

	private class LoginActionListener extends ProgressActionListener{
		public LoginActionListener(AbstractController controller){
			super(controller);
		}

		@Override
		public void performAction(ActionEvent e){
			dlg.clearErrors();
			String url = dlg.getUrl();
			String username = dlg.getUsername();
			String password = dlg.getPassword();
			if (url != null && !url.isEmpty() && username != null && !username.isEmpty() && password != null
					&& !password.isEmpty()){
				SchemaAdminService schemaAdminService = ACSpringFactory.getInstance().getSchemaAdminService();
				try{
					Map<String, List<String>> tabMap = schemaAdminService
							.getAvailableSalesforceTabs(url, username, password);
					dlg.setData(tabMap);
					SchemaAdminModel model = controller.getModel();
					model.setSalesforceUrl(url);
					model.setSalesforceUsername(username);
					model.setSalesforcePassword(password);
				} catch (ACException e1){
					log.error(e1.getMessage());
					dlg.renderErrors(new ServerErrorContainerWrapper(e1));
				}
			}
		}

	}

	private class ImportButtonListener extends ProgressActionListener{
		public ImportButtonListener(AbstractController controller){
			super(controller);
		}

		@Override
		public void performAction(ActionEvent e){
			try{
				dlg.clearErrors();
				String tabSet = dlg.getSelectedTabSet();
				List<String> selectedTabs = dlg.getSelectedTabNames();
				final User user = SessionData.getInstance().getUser();
				SchemaAdminService schemaAdminService = ACSpringFactory.getInstance().getSchemaAdminService();
				SchemaAdminModel model = controller.getModel();
				schemaAdminService.importSchemaFromSalesforce(tabSet, selectedTabs, user.getId(), model.getSalesforceUrl(),
						model.getSalesforceUsername(), model.getSalesforcePassword());
				dlg.setSuccess(true);
				dlg.setVisible(false);
			} catch (ACException ex){
				log.error(ex.getMessage());
				dlg.renderErrors(new ServerErrorContainerWrapper(ex));
			}
		}

	}
}
