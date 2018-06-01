/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.importers.CSVDataImporter;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.importers.Importer;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.importers.SalesforceSchemaImporter;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.importers.XLSDataImporter;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.importers.XLSSchemaImporter;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.importers.XMLSchemaImporter;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ImportButtonListener extends ProgressActionListener{

	public ImportButtonListener(SchemaAdminController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		if (SessionData.getInstance().getCurrentDatabaseInstance() == null)
			return;
		if (!SessionData.getInstance().getCurrentDatabaseInstance().isConnected())
			return;
		SchemaAdminController controller = (SchemaAdminController) getController();
		SchemaAdminView view = controller.getView();
		String action = e.getActionCommand();
		Object[] paths = null;
		if (TextID.XMLImport.toString().equals(action) || TextID.XLSSchemaImport.toString().equals(action)
				|| TextID.SalesforceSchemaImport.toString().equals(action)){
			paths = view.getSchemaTreePaths();
		} else{
			TreePath tp = view.getSchemaTreeSelectedPath();
			paths = tp.getPath();
		}
		ObjectHolder.getInstance().setCurrentPath(paths);

		boolean ok = true;
		Importer importer = null;
		if (TextID.XMLImport.toString().equals(action)){
			importer = new XMLSchemaImporter(controller);
		} else if (TextID.XLSDataImport.toString().equals(action)){
			importer = new XLSDataImporter(controller);
		} else if (TextID.XLSSchemaImport.toString().equals(action)){
			importer = new XLSSchemaImporter(controller);
		} else if (TextID.CSVDataImport.toString().equals(action)){
			importer = new CSVDataImporter(controller);
		} else if (TextID.SalesforceSchemaImport.toString().equals(action)){
			importer = new SalesforceSchemaImporter(controller);
		}

		ok = importer != null && importer.doImport();

		if (ok){
			view.clearErrors();
			controller.reloadData();
			view.resetEditedFields();
			restoreSelection(action);
		}

	}

	private void restoreSelection(String action){
		SchemaAdminController controller = (SchemaAdminController) getController();
		SchemaAdminView view = controller.getView();
		Object[] paths = ObjectHolder.getInstance().getCurrentPath();
		if (TextID.XMLImport.toString().equals(action) || TextID.XLSSchemaImport.toString().equals(action)){
			TreePath[] newPaths = view.getSchemaTreePaths();
			TreePath found = getPathDelta(paths, newPaths);
			if (found != null)
				view.setSchemaTreeSelectionPath(found);
		} else{
			view.setSchemaTreeSelectionPath(new TreePath(paths));
		}
	}

	private TreePath getPathDelta(Object[] oldPaths, TreePath[] newPaths){
		for (TreePath ntp : newPaths){
			boolean found = false;
			for (Object otp : oldPaths)
				if (otp.equals(ntp)){
					found = true;
					break;
				}
			if (!found)
				return ntp;
		}
		return null;
	}

}
