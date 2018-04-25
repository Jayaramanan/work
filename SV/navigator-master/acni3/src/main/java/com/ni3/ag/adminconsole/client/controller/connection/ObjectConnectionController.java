/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.connection;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.connection.ObjectConnectionTableModel;
import com.ni3.ag.adminconsole.client.view.connection.ObjectConnectionTreeModel;
import com.ni3.ag.adminconsole.client.view.connection.ObjectConnectionView;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.LineStyle;
import com.ni3.ag.adminconsole.domain.LineWeight;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ObjectConnectionModel;
import com.ni3.ag.adminconsole.shared.service.def.ObjectsConnectionsService;
import com.ni3.ag.adminconsole.shared.service.def.SettingsService;

public class ObjectConnectionController extends AbstractController{
	private ObjectConnectionModel model;
	private ObjectConnectionView view;

	private Logger log = Logger.getLogger(ObjectConnectionController.class);

	private UpdateConnectionActionListener updateListener;

	private ObjectConnectionController(){
	}

	@Override
	public void initializeController(){
		loadObjectConnectionModel();
		super.initializeController();
		ACTree tree = (ACTree) view.getSchemaTree();
		tree.setCurrentController(this);
	}

	@Override
	public ObjectConnectionModel getModel(){
		return model;
	}

	@Override
	public ObjectConnectionView getView(){
		return view;
	}

	@Override
	public void setModel(AbstractModel m){
		model = (ObjectConnectionModel) m;
	}

	@Override
	public void setView(Component c){
		view = (ObjectConnectionView) c;
	}

	public void setModel(ObjectConnectionModel m){
		model = m;
	}

	public void setView(ObjectConnectionView v){
		view = v;
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		getView().addTreeSelectionListener(new ConnectionTreeSelectionListener(this));
		getView().addDeleteButtonActionListener(new DeleteConnectionActionListener(this));
		getView().addCancelButtonActionListener(new CancelConnectionActionListener(this));
		getView().addAddButtonActionListener(new AddConnectionActionListener(this));
		getView().addGenerateConnectionTypesActionListener(new GenerateConnectionTypesActionListener(this));
		updateListener = new UpdateConnectionActionListener(this);
		getView().addUpdateButtonActionListener(updateListener);
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){

	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		reloadTreeModel();
		ObjectConnectionTableModel tableModel = new ObjectConnectionTableModel();
		getView().setTableModel(tableModel);
		refreshTableModel();
	}

	private void reloadTreeModel(){
		ObjectConnectionTreeModel treeModel = new ObjectConnectionTreeModel(getModel().getSchemaMap(), SessionData
		        .getInstance().getConnectedDatabaseInstances());
		getView().setTreeModel(treeModel);
	}

	public void deleteConnection(ObjectConnection connToDelete){
		List<ObjectConnection> connections = getModel().getCurrentObject().getObjectConnections();
		int index = -1;
		if (connToDelete != null){
			index = connections.indexOf(connToDelete);
			connections.remove(connToDelete);
			log.debug("Removed object connection: " + connToDelete);
		}
		view.getTableModel().fireTableRowsDeleted(index, index);
		if (index >= 0 && connections.size() > 0){
			ObjectConnection next = (index < connections.size()) ? connections.get(index) : connections.get(index - 1);
			view.setActiveTableRow(next);
		}
	}

	public void addNewConnection(){
		if (model.getCurrentObject() == null){
			return;
		}
		ObjectConnection newConnection = new ObjectConnection();
		newConnection.setObject(model.getCurrentObject());
		newConnection.setRgb("#000000");
		List<ObjectConnection> connections = model.getCurrentObject().getObjectConnections();
		connections.add(newConnection);
		log.debug("Added new connection");
		int index = connections.size() - 1;
		view.getTableModel().fireTableRowsInserted(index, index);
		view.setActiveTableRow(newConnection);
	}

	private void loadObjectConnectionModel(){
		DatabaseInstance instance = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(instance);
		if (instance != null && instance.isConnected()){
			ObjectsConnectionsService objectsConnectionsService = ACSpringFactory.getInstance()
			        .getObjectsConnectionService();
			List<ObjectDefinition> objectDefinitions = objectsConnectionsService.getNodeLikeObjectDefinitions();
			List<LineWeight> lineWeights = objectsConnectionsService.getLineWeights();
			List<Schema> schemas = objectsConnectionsService.getSchemas();

			model.setSchemas(schemas);
			model.setNodeObjects(objectDefinitions);
			model.setLineWeights(lineWeights);

			loadHierarchicalEdges();
		}
	}

	private void loadHierarchicalEdges(){
		SettingsService settingsService = ACSpringFactory.getInstance().getSettingsService();
		final Setting hierarchies = settingsService.getApplicationSetting(Setting.APPLET_SECTION,
		        Setting.HIERARCHIES_PROPERTY);
		parseHierarchicalEdges(hierarchies);
	}

	@Override
	public void reloadData(){
		loadObjectConnectionModel();
		reloadTreeModel();
		refreshTableModel();
	}

	public void reloadConnections(){
		ObjectDefinition currentObject = model.getCurrentObject();
		if (currentObject == null){
			return;
		}
		ObjectsConnectionsService service = ACSpringFactory.getInstance().getObjectsConnectionService();
		ObjectDefinition newObject = null;
		newObject = service.reloadObject(currentObject.getId());
		currentObject.setObjectConnections(newObject.getObjectConnections());
	}

	public void refreshTableModel(){
		ObjectDefinition currentObject = model.getCurrentObject();
		List<ObjectConnection> connections = currentObject != null ? currentObject.getObjectConnections()
		        : new ArrayList<ObjectConnection>();
		refreshHierarchicalConnections(connections);
		ObjectConnectionTableModel tableModel = view.getTableModel();
		tableModel.setData(connections);
		tableModel.fireTableDataChanged();
	}

	private List<ObjectDefinition> filterObjectsBySchema(List<ObjectDefinition> objects){
		ObjectDefinition current = model.getCurrentObject();
		if (current == null)
			return objects;
		Schema currentSchema = current.getSchema();
		if (currentSchema == null)
			return objects;
		ArrayList<ObjectDefinition> ar = new ArrayList<ObjectDefinition>();
		for (ObjectDefinition od : objects){
			if (od.getSchema().getId().equals(currentSchema.getId()))
				ar.add(od);
		}
		return ar;
	}

	public List<PredefinedAttribute> getConnectionTypeReferenceData(){
		ObjectDefinition od = model.getCurrentObject();
		if (od != null){
			List<ObjectAttribute> attributes = od.getObjectAttributes();
			if (attributes != null){
				for (ObjectAttribute attr : attributes){
					if (attr.getName().equals(ObjectAttribute.CONNECTION_TYPE_ATTRIBUTE_NAME)){
						return attr.getPredefinedAttributes();
					}
				}
			}
		}
		return new ArrayList<PredefinedAttribute>();
	}

	@Override
	public void clearData(){
		model.getSchemaMap().clear();
		model.getLineWeightMap().clear();
		model.getNodeObjectMap().clear();
		model.setCurrentObject(null);
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance.isConnected() && !model.isInstanceLoaded(dbInstance)){
			reloadData();
			AbstractTreeModel treeModel = view.getTreeModel();
			TreePath found = new TreeModelSupport().findPathForEqualObject(dbInstance, treeModel);
			view.getSchemaTree().setSelectionPath(found);
			log.debug("Loaded data for instance: " + dbInstance);
			return false;
		}
		return true;
	}

	public void setReferenceData(){
		view.setLineWeightReferenceData(model.getLineWeights());
		view.setConnectionTypeReferenceData(getConnectionTypeReferenceData());
		List<ObjectDefinition> nodeObjects = filterObjectsBySchema(model.getNodeObjects());
		view.setObjectDefinitionReferenceData(nodeObjects);
	}

	@Override
	public boolean save(){
		return updateListener.save();
	}

	@Override
	public void reloadCurrent(){
		reloadConnections();
	}

	void refreshHierarchicalConnections(List<ObjectConnection> connections){
		for (ObjectConnection c : connections){
			if (model.isHierarchicalEdge(c.getConnectionType().getId())){
				c.setHierarchical(true);
			} else{
				c.setHierarchical(false);
			}
		}
	}

	void parseHierarchicalEdges(Setting hierarchies){
		model.getHierarchicalEdges().clear();
		if (hierarchies != null && hierarchies.getValue() != null && !hierarchies.getValue().isEmpty()){
			String[] ids = hierarchies.getValue().split(";");
			for (String id : ids){
				try{
					model.setHierarchicalEdge(Integer.valueOf(id));
				} catch (NumberFormatException e){
					log.debug("Cannot parse hierarchical edge id: " + id);
					// ignore
				}
			}
		}
	}

	public void updateHierarchicalEdges(){
		removeExcessiveHierarchicalEdges();
		addMissingHierarchicalEdges();

		final Set<Integer> hierarchicalEdges = model.getHierarchicalEdges();
		List<Integer> list = new ArrayList<Integer>();
		list.addAll(hierarchicalEdges);
		Collections.sort(list);
		String edgesStr = listToString(list);

		SettingsService service = ACSpringFactory.getInstance().getSettingsService();
		service.updateApplicationSetting(Setting.APPLET_SECTION, Setting.HIERARCHIES_PROPERTY, edgesStr);
	}

	String listToString(List<Integer> list){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++){
			if (i > 0){
				sb.append(";");
			}
			sb.append(list.get(i));
		}
		return sb.toString();
	}

	/**
	 * add missing hierarchical edge ids
	 */
	void addMissingHierarchicalEdges(){
		final List<Schema> schemas = model.getSchemas();
		final ObjectDefinition currentObject = model.getCurrentObject();

		for (Schema schema : schemas){
			for (ObjectDefinition od : schema.getObjectDefinitions()){
				if (od.isEdge() && od.equals(currentObject)){
					for (ObjectConnection conn : od.getObjectConnections()){
						Integer connTypeId = conn.getConnectionType().getId();
						if (conn.isHierarchical() && !model.isHierarchicalEdge(connTypeId)){
							model.setHierarchicalEdge(connTypeId);
						}
					}
				}
			}
		}
	}

	/**
	 * remove excessive hierarchical edge ids (predefined attribute ids)
	 */
	void removeExcessiveHierarchicalEdges(){
		final List<Schema> schemas = model.getSchemas();
		final ObjectDefinition currentObject = model.getCurrentObject();
		final Set<Integer> hierarchicalEdges = model.getHierarchicalEdges();
		final Set<Integer> toRemove = new HashSet<Integer>();

		for (int eId : hierarchicalEdges){
			boolean found = false;
			for (Schema schema : schemas){
				for (ObjectDefinition od : schema.getObjectDefinitions()){
					if (!od.isEdge() || found){
						continue;
					}
					for (ObjectConnection conn : od.getObjectConnections()){
						int connTypeId = conn.getConnectionType().getId();
						if (eId == connTypeId && (!od.equals(currentObject) || conn.isHierarchical())){
							found = true;
							break;
						}
					}
				}
			}
			if (!found){
				toRemove.add(eId);
			}
		}

		hierarchicalEdges.removeAll(toRemove);
	}

	public void generateConnectionTypes(List<ObjectDefinition> fromNodes, List<ObjectDefinition> toNodes,
	        List<PredefinedAttribute> connectionTypes){
		List<ObjectConnection> connections = model.getCurrentObject().getObjectConnections();
		for (PredefinedAttribute type : connectionTypes)
			for (ObjectDefinition from : fromNodes)
				for (ObjectDefinition to : toNodes)
					if (!connectionExists(connections, from, to, type))
						addNewConnection(from, to, type);
	}

	private void addNewConnection(ObjectDefinition from, ObjectDefinition to, PredefinedAttribute type){
		ObjectConnection newConnection = new ObjectConnection();
		List<ObjectConnection> connections = model.getCurrentObject().getObjectConnections();
		newConnection.setObject(model.getCurrentObject());
		newConnection.setFromObject(from);
		newConnection.setToObject(to);
		newConnection.setConnectionType(type);
		newConnection.setRgb("#000000");
		newConnection.setLineStyle(LineStyle.FULL);
		connections.add(newConnection);
		log.debug("Added new connection");
		int index = connections.size() - 1;
		view.getTableModel().fireTableRowsInserted(index, index);
		view.setActiveTableRow(newConnection);
	}

	private boolean connectionExists(List<ObjectConnection> connections, ObjectDefinition from, ObjectDefinition to,
	        PredefinedAttribute type){
		for (ObjectConnection oc : connections){
			if (oc.getFromObject().equals(from) && oc.getToObject().equals(to) && oc.getConnectionType().equals(type))
				return true;
		}
		return false;
	}
}
