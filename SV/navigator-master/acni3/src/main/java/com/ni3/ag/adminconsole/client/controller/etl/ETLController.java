/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.etl;

import java.awt.Component;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.etl.ETLTreeModel;
import com.ni3.ag.adminconsole.client.view.etl.ETLView;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ETLModel;

public class ETLController extends AbstractController{
	private ETLView view;
	private ETLModel model;

	@Override
	public ETLModel getModel(){
		return model;
	}

	@Override
	public ETLView getView(){
		return view;
	}

	@Override
	public void setModel(AbstractModel m){
		this.model = (ETLModel) m;
	}

	@Override
	public void setView(Component c){
		this.view = (ETLView) c;
	}

	public void setModel(ETLModel m){
		this.model = m;
	}

	public void setView(ETLView c){
		this.view = c;
	}

	@Override
	public void initializeController(){
		super.initializeController();
		ACTree tree = (ACTree) view.getTree();
		tree.setCurrentController(this);
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		this.view.addETLLinkButtonListener(new ETLLinkButtonListener(this));
		this.view.getTree().addTreeSelectionListener(new ETLTreeSelectionListener(this));
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){

	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		ETLTreeModel treeModel = new ETLTreeModel(SessionData.getInstance().getConnectedDatabaseInstances());
		getView().setTreeModel(treeModel);
	}

	@Override
	public void reloadCurrent(){
	}

	@Override
	public void reloadData(){
		populateDataToView(model, view);
	}

	@Override
	public boolean save(){
		return false;
	}

	@Override
	public void clearData(){
	}
}
