/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorRightPanel;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorTableModel;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorTreeModel;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorView;
import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;
import com.ni3.ag.adminconsole.shared.service.def.NodeMetaphorService;
import org.apache.log4j.Logger;

public class NodeMetaphorController extends AbstractController{

	static final String DEFAULT_METAPHOR_SET = "Default";

	private NodeMetaphorModel model;
	private NodeMetaphorView view;

	private Logger log = Logger.getLogger(NodeMetaphorController.class);

	private UpdateNodeMetaphorActionListener updateListener;

	private NodeMetaphorController(){
	}

	@Override
	public void initializeController(){
		loadSchemas();
		super.initializeController();
		ACTree tree = (ACTree) view.getLeftPanel().getSchemaTree();
		tree.setCurrentController(this);
	}

	@Override
	public NodeMetaphorModel getModel(){
		return model;
	}

	@Override
	public NodeMetaphorView getView(){
		return view;
	}

	@Override
	public void setModel(AbstractModel m){
		model = (NodeMetaphorModel) m;
	}

	@Override
	public void setView(Component c){
		view = (NodeMetaphorView) c;

	}

	public void setModel(NodeMetaphorModel m){
		model = m;
	}

	public void setView(NodeMetaphorView v){
		view = v;
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		getView().getLeftPanel().addTreeSelectionListener(new NodeMetaphorTreeSelectionListener(this));
		getView().getRightPanel().addDeleteButtonActionListener(new DeleteNodeMetaphorActionListener(this));
		getView().getRightPanel().addAddButtonActionListener(new AddNodeMetaphorActionListener(this));
		updateListener = new UpdateNodeMetaphorActionListener(this);
		getView().getRightPanel().addUpdateButtonActionListener(updateListener);
		getView().getRightPanel().addCancelButtonActionListener(new CancelNodeMetaphorActionListener(this));
		getView().getRightPanel().addMetaphorSetComboListener(new MetaphorSetComboListener(this));
		getView().getRightPanel().addAddSetButtonActionListener(new AddSetButtonListener(this));
		getView().getRightPanel().addCopySetButtonActionListener(new CopySetButtonListener(this));
		getView().getRightPanel().addDeleteSetButtonActionListener(new DeleteSetButtonListener(this));
		getView().getRightPanel().addAddIconButtonActionListener(new AddIconButtonListener(this));
		getView().getRightPanel().addDeleteIconButtonActionListener(new DeleteIconButtonListener(this));
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){

	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		NodeMetaphorTreeModel treeModel = new NodeMetaphorTreeModel(getModel().getSchemaMap(), SessionData.getInstance()
		        .getConnectedDatabaseInstances());
		getView().getLeftPanel().setTreeModel(treeModel);
		NodeMetaphorTableModel tableModel = new NodeMetaphorTableModel();
		tableModel.addTableModelListener(new TableModelListener(){
			@Override
			public void tableChanged(TableModelEvent e){
				getView().resizeNodeMetaphorTableColumns();
			}
		});
		NodeMetaphorRightPanel rightPanel = getView().getRightPanel();
		rightPanel.setTableModel(tableModel);
		refreshTableModel(true);
	}

	private void refreshTreeModel(){
		getView().getLeftPanel().getTreeModel()
		        .setData(getModel().getSchemaMap(), SessionData.getInstance().getConnectedDatabaseInstances());
		view.updateTree();
	}

	public void refreshTableModel(boolean structureChanged){
		ObjectDefinition od = model.getCurrentObjectDefinition();
		List<Metaphor> metaphors = new ArrayList<Metaphor>();
		List<ObjectAttribute> attributes = new ArrayList<ObjectAttribute>();
		if (od != null){
			metaphors = od.getMetaphors();
			attributes = od.getObjectAttributes();
		}
		model.setMetaphorSets(getMetaphorSets(metaphors));
		NodeMetaphorRightPanel rightPanel = getView().getRightPanel();
		NodeMetaphorTableModel tableModel = rightPanel.getTableModel();
		tableModel.setData(metaphors, attributes);

		if (structureChanged){
			TableColumnModel tcm = new DefaultTableColumnModel();
			for (int i = 0; i < tableModel.getColumnCount(); i++){
				TableColumn tc = new TableColumn(i);
				tc.setHeaderValue(tableModel.getColumnName(i));
				tcm.addColumn(tc);
			}
			rightPanel.setTableColumnModel(tcm);
			tableModel.fireTableStructureChanged();
		} else
			tableModel.fireTableDataChanged();

		view.resetEditedFields();
		rightPanel.setCellRenderersAndEditors(attributes);
		rightPanel.setMetaphorSetReferenceData(model.getMetaphorSets());
		rightPanel.setIconReferenceData(model.getIcons());
		rightPanel.getMetaphorSetCombo().setSelectedItem(DEFAULT_METAPHOR_SET);

	}

	private void loadSchemas(){
		DatabaseInstance instance = SessionData.getInstance().getCurrentDatabaseInstance();
		for(DatabaseInstance di : SessionData.getInstance().getDatabaseInstances()){
			SessionData.getInstance().setCurrentDatabaseInstance(di);
			model.setCurrentDatabaseInstance(di);
			if (di.isConnected()){
				NodeMetaphorService nodeMetaphorService = ACSpringFactory.getInstance().getNodeMetaphorService();
				List<Schema> schemas = nodeMetaphorService.getSchemasWithObjects();
				log.debug("loaded schemas, size: " + (schemas != null ? schemas.size() : 0));
				model.setSchemas(schemas);

				if (!model.isIconLoaded()){
					loadIcons();
				}
			}
		}
		SessionData.getInstance().setCurrentDatabaseInstance(instance);
		model.setCurrentDatabaseInstance(instance);
	}

	private void loadIcons(){
		NodeMetaphorService service = ACSpringFactory.getInstance().getNodeMetaphorService();
		List<Icon> icons = service.getAllIcons();
		log.debug("loaded " + icons.size() + " icons");
		model.setIcons(icons);
	}

	@Override
	public void reloadData(){
		loadSchemas();
		refreshTreeModel();
		refreshTableModel(false);
		view.setMetaphorSetCopied(false);
		view.setMetaphorSetDeleted(false);
	}

	public void deleteNodeMetaphor(Metaphor nmToDelete){
		List<Metaphor> currentMetaphors = model.getCurrentMetaphors();
		int index = -1;
		if (nmToDelete == null){
			return;
		}

		index = currentMetaphors.indexOf(nmToDelete);

		List<Metaphor> allMetaphors = model.getCurrentObjectDefinition().getMetaphors();

		currentMetaphors.remove(nmToDelete);
		allMetaphors.remove(nmToDelete);
		log.debug("Removed node metaphor: " + nmToDelete);

		view.getRightPanel().getTableModel().fireTableRowsDeleted(index, index);

		if (index >= 0 && currentMetaphors.size() > 0){
			Metaphor next = (index < currentMetaphors.size()) ? currentMetaphors.get(index) : currentMetaphors
			        .get(index - 1);
			view.getRightPanel().setActiveTableRow(next);
		}
	}

	public void addNewNodeMetaphor(){
		Metaphor row = new Metaphor();
		row.setObjectDefinition(model.getCurrentObjectDefinition());
		row.setSchema(model.getCurrentObjectDefinition().getSchema());
		row.setMetaphorSet(model.getCurrentMetaphorSet() != null ? model.getCurrentMetaphorSet() : DEFAULT_METAPHOR_SET);
		row.setMetaphorData(new ArrayList<MetaphorData>());
		row.setPriority(getNextPriority());
		model.getCurrentMetaphors().add(row);
		if (model.getCurrentMetaphorSet() != null)
			model.getCurrentObjectDefinition().getMetaphors().add(row);
		log.debug("Added new metaphor");
		NodeMetaphorTableModel tableModel = view.getRightPanel().getTableModel();
		int index = tableModel.getRowCount() - 1;
		tableModel.fireTableRowsInserted(index, index);
		view.getRightPanel().setActiveTableRow(row);
	}

	private int getNextPriority(){
		int nextPriority = 1;
		if (model.getCurrentMetaphors() != null)
			for (Metaphor metaphor : model.getCurrentMetaphors())
				if (metaphor.getPriority() != null && metaphor.getPriority() >= nextPriority)
					nextPriority = metaphor.getPriority() + 1;
		return nextPriority;
	}

	protected List<String> getMetaphorSets(List<Metaphor> metaphors){
		List<String> metaphorSets = new ArrayList<String>();
		for (Metaphor metaphor : metaphors){
			String set = metaphor.getMetaphorSet();
			if (set != null && set.length() > 0 && !metaphorSets.contains(set)){
				metaphorSets.add(metaphor.getMetaphorSet());
			}
		}
		if (!metaphorSets.contains(DEFAULT_METAPHOR_SET)){
			metaphorSets.add(DEFAULT_METAPHOR_SET);
		}
		return metaphorSets;
	}

	@Override
	public void clearData(){
		model.getSchemaMap().clear();
		model.setCurrentMetaphorSet(null);
		model.setCurrentObjectDefinition(null);
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance.isConnected() && !model.isInstanceLoaded()){
			reloadData();
			TreePath found = view.getLeftPanel().getTreeModel().findPathForEqualObject(dbInstance);
			view.getLeftPanel().setSelectionTreePath(found);
			log.debug("Loaded data for instance: " + dbInstance);
			return false;
		}
		return true;
	}

	@Override
	public boolean save(){
		return updateListener.save();
	}

	@Override
	public void reloadCurrent(){
		view.setMetaphorSetCopied(false);
		view.setMetaphorSetDeleted(false);
		reloadMetaphors();
	}

	public void reloadMetaphors(){
		ObjectDefinition currentObject = model.getCurrentObjectDefinition();
		if (currentObject == null){
			return;
		}
		NodeMetaphorService service = ACSpringFactory.getInstance().getNodeMetaphorService();
		ObjectDefinition newObject = null;
		newObject = service.reloadObject(currentObject.getId());
		currentObject.setMetaphors(newObject.getMetaphors());
		for (Metaphor m : newObject.getMetaphors()){
			m.setObjectDefinition(currentObject);
		}
	}
}
