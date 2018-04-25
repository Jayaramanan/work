/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.connection;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.connection.ObjectConnectionView;
import com.ni3.ag.adminconsole.domain.LineWeight;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.ObjectConnectionModel;
import com.ni3.ag.adminconsole.shared.service.def.ObjectsConnectionsService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UpdateConnectionActionListener extends ProgressActionListener{

	private ObjectConnectionController controller;

	private Logger log = Logger.getLogger(UpdateConnectionActionListener.class);

	public UpdateConnectionActionListener(ObjectConnectionController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		ObjectConnectionView view = controller.getView();
		ObjectConnection selectedConnection = view.getSelectedConnection();
		TreePath treeSelection = view.getSchemaTree().getSelectionPath();
		if (!save()){
			return;
		}

		view.resetEditedFields();

		controller.reloadData();

		if (treeSelection != null){
			TreePath found = view.getTreeModel().findPathByNodes(treeSelection.getPath(), view.getTreeModel());
			if (found != null){
				view.getSchemaTree().setSelectionPath(found);
				view.setActiveTableRow(selectedConnection);
			}
		}
	}

	public boolean save(){
		log.debug("action performed");

		ObjectConnectionView view = controller.getView();
		ObjectConnectionModel model = controller.getModel();

		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return true;
		}

		view.stopCellEditing();
		view.clearErrors();

		ObjectDefinition currentObject = model.getCurrentObject();
		if (currentObject == null){
			return true;
		}

		List<ObjectConnection> connToUpdate = currentObject.getObjectConnections();
		if (connToUpdate != null){
			ObjectsConnectionsService objectsConnectionsService = ACSpringFactory.getInstance()
			        .getObjectsConnectionService();
			LineWeight lineWeight = objectsConnectionsService.getDefaultLineWeight();
			for (ObjectConnection conn : connToUpdate)
				if (conn.getLineWeight() == null)
					conn.setLineWeight(lineWeight);
			ACValidationRule rule = (ACValidationRule) ACSpringFactory.getInstance().getBean(
			        "objectConnectionValidationRule");
			if (!rule.performCheck(model)){
				controller.getView().renderErrors(rule.getErrorEntries());
				return false;
			}
			rule = (ACValidationRule) ACSpringFactory.getInstance().getBean("connectionUniqueValidationRule");
			if (!rule.performCheck(model)){
				view.renderErrors(rule.getErrorEntries());
				return false;
			}

			controller.updateHierarchicalEdges();

			objectsConnectionsService.save(currentObject);
		}
		return true;
	}

}
