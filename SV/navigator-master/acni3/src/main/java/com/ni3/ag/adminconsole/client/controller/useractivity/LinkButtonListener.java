/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useractivity;

import java.awt.event.ActionEvent;
import java.util.Date;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.useractivity.UserActivityView;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.util.TimeUtil;

public class LinkButtonListener extends ProgressActionListener{

	public LinkButtonListener(UserActivityController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		UserActivityView view = (UserActivityView) getController().getView();

		String command = e.getActionCommand();
		Date startDate = null;
		Date endDate = null;
		if (TextID.Today.toString().equals(command)){
			startDate = TimeUtil.getToday();
			endDate = TimeUtil.getTodayEnd();
		} else if (TextID.ThisWeek.toString().equals(command)){
			startDate = TimeUtil.getWeekStart();
			endDate = TimeUtil.getTodayEnd();
		} else if (TextID.ThisMonth.toString().equals(command)){
			startDate = TimeUtil.getMonthStart();
			endDate = TimeUtil.getTodayEnd();
		}
		view.setDateFrom(startDate);
		view.setDateTo(endDate);
	}

}
