/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.reports;

import java.awt.Component;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.reports.ReportsTreeModel;
import com.ni3.ag.adminconsole.client.view.reports.ReportsView;
import com.ni3.ag.adminconsole.domain.ReportTemplate;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ReportsModel;
import com.ni3.ag.adminconsole.shared.service.def.ReportsService;

public class ReportsController extends AbstractController{

	private ReportsView view;
	private ReportsModel model;
	private UpdateReportTemplateButtonListener updateListener;

	@Override
	public void initializeController(){
		loadData();
		super.initializeController();
		view.setTreeController(this);
	}

	void loadData(){
		DatabaseInstance instance = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(instance);
		if (instance != null && instance.isConnected()){
			ReportsService service = ACSpringFactory.getInstance().getReportsService();
			model.setReports(service.getReportTemplates());
		}
	}

	@Override
	public void clearData(){
		model.getReportMap().clear();
	}

	@Override
	public ReportsModel getModel(){
		return model;
	}

	@Override
	public ReportsView getView(){
		return view;
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		updateListener = new UpdateReportTemplateButtonListener(this);
		getView().addUpdateButtonListener(updateListener);
		getView().addTreeEditorReportTemplateNameListener(updateListener);
		getView().addAddReportTemplateButtonListener(new AddReportTemplateButtonListener(this));
		getView().addTreeSelectionListener(new SchemaTreeSelectionListener(this));
		getView().addUploadThumbnailButtonListener(new UploadThumbnailButtonListener(this));
		getView().addDeleteReportButtonListener(new DeleteReportButtonListener(this));
		getView().addRefreshReportButtonListener(new RefreshReportButtonListener(this));
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){

	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		ReportsModel rModel = (ReportsModel) model;
		ReportsTreeModel treeModel = new ReportsTreeModel(rModel.getReportMap(), SessionData.getInstance()
		        .getConnectedDatabaseInstances());
		getView().setTreeModel(treeModel);
		populateXMLAndPreviewToModel();
	}

	public boolean canSwitch(boolean reloadCurrent){
		if (model.getCurrentReport() == null)
			return true;
		return super.canSwitch(reloadCurrent);
	}

	@Override
	public void reloadCurrent(){
		if (model.getCurrentReport() == null)
			return;
		ReportTemplate current = (ReportTemplate) model.getCurrentReport();
		if (current == null){
			return;
		}
		ReportsService service = (ReportsService) ACSpringFactory.getInstance().getReportsService();
		ReportTemplate newRT = service.getReportTemplate(current.getId());
		model.setCurrentReport(newRT);
		populateXMLAndPreviewToView();
	}

	@Override
	public void reloadData(){
		loadData();
		populateDataToView(model, view);
	}

	@Override
	public boolean save(){
		return updateListener.save();
	}

	@Override
	public void setModel(AbstractModel m){
		this.model = (ReportsModel) m;
	}

	public void setModel(ReportsModel m){
		this.model = m;
	}

	@Override
	public void setView(Component c){
		view = (ReportsView) c;
	}

	public void setView(ReportsView c){
		view = c;
	}

	public ReportTemplate addReportTemplate(ReportTemplate reportTemplate){
		ReportsService service = (ReportsService) ACSpringFactory.getInstance().getBean("reportsService");
		return service.saveReportTemplate(reportTemplate);
	}

	public void updateTree(boolean delete){
		view.updateTree(model.getReportMap());
		if (delete){
			model.setCurrentReport(null);
		}
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance.isConnected() && !model.isInstanceLoaded()){
			reloadData();
			TreePath found = view.getTreeModel().findPathForEqualObject(dbInstance);
			view.setTreeSelectionPath(found);
			return false;
		}
		return true;
	}

	public void saveCurrentReportTemplate(){
		ReportTemplate rt = model.getCurrentReport();
		ReportsService service = (ReportsService) ACSpringFactory.getInstance().getBean("reportsService");
		service.saveReportTemplate(rt);
		reloadData();
		reloadCurrent();
		view.resetEditedFields();
	}

	public void populateXMLAndPreviewToView(){
		ReportTemplate rt = model.getCurrentReport();
		if (rt == null){
			view.setXML("");
			view.setStartIcon(null);
		} else{
			view.setXML(rt.getXml());
			view.setReportType(rt.getReportType());
			view.setStartIcon(rt.getPreview());
		}
		view.setXMLEnabled(rt != null);
	}

	public void populateXMLAndPreviewToModel(){
		ReportTemplate rt = model.getCurrentReport();
		if (rt == null){
			return;
		}
		rt.setXml(view.getXML());
		rt.setPreview(view.getPreview());
		rt.setReportType(view.getReportType());
	}

	public void deleteReportTemplate(ReportTemplate rt){
		ReportsService service = (ReportsService) ACSpringFactory.getInstance().getBean("reportsService");
		service.deleteReportTemplate(rt);
	}

}
