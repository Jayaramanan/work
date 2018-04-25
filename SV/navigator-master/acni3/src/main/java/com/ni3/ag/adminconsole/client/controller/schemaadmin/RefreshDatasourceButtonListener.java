/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.ActionEvent;
import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;

public class RefreshDatasourceButtonListener extends ProgressActionListener{

	private SchemaAdminController controller;

	public RefreshDatasourceButtonListener(SchemaAdminController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		controller.reloadData();
		controller.updateInfoView();
		controller.getView().restoreSelection();
	}

}
