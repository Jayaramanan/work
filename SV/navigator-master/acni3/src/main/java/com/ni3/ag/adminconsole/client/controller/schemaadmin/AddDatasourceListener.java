/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class AddDatasourceListener extends ProgressActionListener{
	private SchemaAdminController controller;

	public AddDatasourceListener(SchemaAdminController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		List<DatabaseInstance> dbNames = controller.getView().getTreeModel().getDatabaseInstances();
		List<DatabaseInstance> dbNamesInTree = new ArrayList<DatabaseInstance>();
		String newDBIName = "New instance";
		DatabaseInstance newDBI = new DatabaseInstance(newDBIName);
		newDBI.setInited(false);
		dbNamesInTree.addAll(dbNames);
		if (!dbNamesInTree.contains(newDBI)){
			dbNamesInTree.add(newDBI);
			controller.updateTreeModel(dbNamesInTree);
		} else{
			List<ErrorEntry> errors = new ArrayList<ErrorEntry>();
			errors.add(new ErrorEntry(TextID.MsgDuplicateDataSourceName, new String[] { newDBIName }));
			controller.getView().renderErrors(errors);
		}
	}

}
