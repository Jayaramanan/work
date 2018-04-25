/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.connection;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.connection.ObjectConnectionView;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.ObjectConnectionModel;

public class CancelConnectionActionListener extends ProgressActionListener{

	Logger log = Logger.getLogger(CancelConnectionActionListener.class);

	public CancelConnectionActionListener(ObjectConnectionController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		log.debug("action performed");
		ObjectConnectionController controller = (ObjectConnectionController) getController();
		ObjectConnectionView view = controller.getView();
		ObjectConnectionModel model = controller.getModel();

		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		view.stopCellEditing();
		view.clearErrors();

		ObjectConnection selectedConnection = view.getSelectedConnection();
		TreePath treeSelection = view.getSchemaTree().getSelectionPath();

		controller.reloadData();

		if (treeSelection != null){
			TreePath found = view.getTreeModel().findPathByNodes(treeSelection.getPath(), view.getTreeModel());
			if (found != null){
				view.getSchemaTree().setSelectionPath(found);
				view.setActiveTableRow(selectedConnection);
			}
		}
		view.resetEditedFields();
	}
}
