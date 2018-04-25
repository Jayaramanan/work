/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useractivity;

import static com.ni3.ag.adminconsole.client.view.Translation.get;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.useractivity.UserActivityTreeModel;
import com.ni3.ag.adminconsole.client.view.useractivity.UserActivityView;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserActivity;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.UserActivityModel;
import com.ni3.ag.adminconsole.shared.service.def.UserActivityService;
import com.ni3.ag.adminconsole.util.TimeUtil;

public class UserActivityController extends AbstractController{
	private static final Logger log = Logger.getLogger(UserActivityController.class);
	private UserActivityModel model;
	private UserActivityView view;

	private UserActivityController(){
	}

	@Override
	public void initializeController(){
		super.initializeController();
		ACTree tree = (ACTree) view.getTree();
		tree.setCurrentController(this);
	}

	@Override
	public UserActivityModel getModel(){
		return model;
	}

	@Override
	public UserActivityView getView(){
		return view;
	}

	public void setModel(UserActivityModel m){
		model = m;
	}

	public void setView(UserActivityView c){
		view = c;
	}

	@Override
	public void setModel(AbstractModel m){
		model = (UserActivityModel) m;
	}

	@Override
	public void setView(Component c){
		view = (UserActivityView) c;
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		UserActivityView aView = (UserActivityView) view;
		aView.addTreeSelectionListener(new UserActivityTreeSelectionListener(this));
		aView.addSearchButtonListener(new SearchButtonListener(this));
		aView.addLinkButtonListener(new LinkButtonListener(this));
		aView.setFilterModeComboListener(new FilterModeComboListener(this));
		aView.addReportsButtonListener(new MonitoringReportButtonListener(this));
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		updateTreeModel();
	}

	private void updateTreeModel(){
		UserActivityTreeModel treeModel = new UserActivityTreeModel(SessionData.getInstance()
		        .getConnectedDatabaseInstances());
		getView().setTreeModel(treeModel);
		updateServerTime();
	}

	private void updateServerTime(){
		UserActivityService service = ACSpringFactory.getInstance().getUserActivityService();
		String pattern = new SimpleDateFormat().toPattern();
		if (!pattern.contains("yyyy") && pattern.contains("yy"))
			pattern = pattern.replace("yy", "yyyy");
		view.setCurrentServerTime(service.getCurrentServerTime(pattern));
	}

	@Override
	public void clearData(){
		clearAll();
	}

	@Override
	public void reloadCurrent(){
		reloadCurrentInstanceData();
		updateTreeModel();
		clearAll();
		refreshFilterCombo();
	}

	@Override
	public void reloadData(){
		reloadCurrentInstanceData();
		updateTreeModel();
		clearAll();
		refreshFilterCombo();
	}

	@Override
	public boolean save(){
		return true;
	}

	public void clearAll(){
		view.setDateFrom(null);
		view.setDateTo(null);
		view.setCurrentFilterMode(TextID.UserBased);
		view.setTableModelUser(null);
	}

	public void refreshFilterCombo(){
		Object mode = view.getCurrentFilterMode();
		view.clearFilterItems();
		if (mode == TextID.ActionBased){
			view.setFilterItems(model.getActivityTypes());
		} else if (mode == TextID.UserBased){
			view.setFilterItems(model.getUsers());
		}
		view.updateLabels(mode);
	}

	public void reloadCurrentInstanceData(){
		DatabaseInstance inst = model.getCurrentDatabaseInstance();
		if (inst == null)
			return;
		if (!inst.isConnected())
			return;
		UserActivityService service = ACSpringFactory.getInstance().getUserActivityService();
		model.setUsers(service.getUsers());
		model.setActivityTypes(getAllTypes());
	}

	private List<UserActivityType> getAllTypes(){
		UserActivityType[] types = UserActivityType.values();
		List<UserActivityType> result = new ArrayList<UserActivityType>();
		for (UserActivityType ut : types)
			if (!ut.equals(UserActivityType.NotALog))
				result.add(ut);
		return result;
	}

	@SuppressWarnings("unchecked")
	public void launchSearch(){
		UserActivityService service = ACSpringFactory.getInstance().getUserActivityService();
		Date from = view.getDateFrom();
		Date to = view.getDateTo();
		Object mode = view.getCurrentFilterMode();
		Object filter = view.getCurrentFilter();

		Object[] data = service.getDataWithSummary(from, to, mode, filter);
		if (mode == TextID.UserBased){
			view.setTableModelUser((List<User>) data[0]);
			log.debug("User based mode");
		} else if (mode == TextID.ActionBased){
			view.setTableModelActivities((Map<UserActivityType, List<UserActivity>>) data[0]);
			log.debug("Action based mode");
		} else{
			view.setTableModelUser(null);
		}
		String summary = generateSummary((Long) data[1], (Map<TextID, Integer>) data[2]);
		view.setSummary(summary);
	}

	private String generateSummary(Long avgTime, Map<TextID, Integer> activityCounts){
		StringBuffer summary = new StringBuffer();
		summary.append("<html><body>");
		summary.append("<h2>").append(get(TextID.Summary)).append("</h2>");
		summary.append("<table WIDTH=300>");
		summary.append("<tr><td>").append(get(TextID.AvgSessionDuration));

		String avgDuration = avgTime != null && avgTime > 0 ? TimeUtil.getFormattedTime(avgTime) : "--";
		summary.append("</td><td>").append(avgDuration).append("</td></tr>");
		summary.append("<tr><h3>").append(get(TextID.TotalsByAction)).append("</h3></tr>");
		for (TextID ac : activityCounts.keySet()){
			summary.append("<tr><td>" + get(ac) + ":</td><td>" + activityCounts.get(ac) + "</td></tr>");
		}
		summary.append("</table>");
		summary.append("</body></html>");
		return summary.toString();
	}

}