/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;

public class UpdateCacheButtonListener extends ProgressActionListener{
	private SchemaAdminController controller;

	public UpdateCacheButtonListener(SchemaAdminController schemaAdminController){
		super(schemaAdminController);
		controller = schemaAdminController;
	}

	@Override
	public void performAction(ActionEvent e){
		controller.getView().clearErrors();
		DatabaseInstance di = SessionData.getInstance().getCurrentDatabaseInstance();
		if (di == null || !di.isConnected())
			return;
		String navHost = di.getNavigatorHost();
		if (navHost == null || navHost.isEmpty()){
			navHost = requestNavigatorHost();
		}
		if (navHost == null)
			return;
		SchemaAdminService service = ACSpringFactory.getInstance().getSchemaAdminService();
        Schema s = controller.getModel().getCurrentSchema();
        if(s == null){
            ObjectDefinition od = controller.getModel().getCurrentObjectDefinition();
            if(od != null)
                s = od.getSchema();
        }
		try{
			service.updateCache(navHost, SessionData.getInstance().getUser(), s != null ? s.getId() : null);
			service.resetAnyInvalidationRequired();
			MainPanel2.resetInvalidationNeeded();
		} catch (ACException ex){
			controller.getView().renderErrors(ex);
		}
	}

	private String requestNavigatorHost(){
		return ACOptionPane.showInputDialog(controller.getView(), Translation.get(TextID.NavigatorHost),
		        "test.office.ni3.net:9090/Ni3Web");
	}

}
