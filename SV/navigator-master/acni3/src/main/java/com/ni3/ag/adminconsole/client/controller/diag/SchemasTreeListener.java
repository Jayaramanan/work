/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.diag;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.diag.DiagnosticsView;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.DiagnosticsModel;

public class SchemasTreeListener extends SchemaTreeSelectionListener{
	private static final Logger log = Logger.getLogger(SchemasTreeListener.class);
	private DiagnosticsController controller;

	public SchemasTreeListener(DiagnosticsController diagnosticsController){
		controller = diagnosticsController;
	}

	@Override
	public void changeValue(TreeSelectionEvent e){
		DiagnosticsView view = controller.getView();
		view.renderErrors(null);
		TreePath path = e.getNewLeadSelectionPath();
		Object o = path.getLastPathComponent();
		DiagnosticsModel model = (DiagnosticsModel) controller.getModel();
		log.debug("current schema: " + o);
		if (o instanceof Schema){
			model.setCurrentSchema((Schema) o);
			TreePath pPath = path.getParentPath();
			model.setCurrentDatabaseInstance((DatabaseInstance) pPath.getLastPathComponent());
		} else
			model.setCurrentSchema(null);
		if (path != null)
			ObjectHolder.getInstance().setCurrentPath(path.getPath());
		controller.updateTableData();
	}

}
