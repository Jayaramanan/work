/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserActivity;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class UserActivityServiceImplTest extends TestCase{
	private List<UserActivity> activities;
	private UserActivityServiceImpl service;

	private UserActivityType[] aTypes = { UserActivityType.PasswordLogin, UserActivityType.AdvancedSearch,
	        UserActivityType.CreateEdge, UserActivityType.Logout };

	protected void setUpV1(){
		service = new UserActivityServiceImpl();
		activities = new ArrayList<UserActivity>();
		long currentTime = System.currentTimeMillis();
		UserActivityType[] aTypes = { UserActivityType.PasswordLogin, UserActivityType.AdvancedSearch,
		        UserActivityType.CreateEdge, UserActivityType.Logout };
		String sessions[] = { "SessionId=111;", "SessionId=222;", "SessionId=333;" };
		for (int i = 0; i < 10; i++){
			UserActivity ua = new UserActivity();
			ua.setActivityType(aTypes[i % 4].getValue().toString());
			ua.setDateTime(new Date(currentTime + i * 10000));
			ua.setRequest(sessions[i / 4]);
			activities.add(ua);
		}
	}

	protected void setUpV2(){
		service = new UserActivityServiceImpl();
		activities = new ArrayList<UserActivity>();

		for (int i = 0; i < 10; i++){
			UserActivity ua = new UserActivity();
			ua.setActivityType(aTypes[i % 4].getValue().toString());
			if (ua.isLoginActivity())
				ua.setSessionDuration(new Long(i * 10000));
			activities.add(ua);
		}

	}

	public void testFillSessionDurations(){
		setUpV1();
		service.fillSessionDurations(activities);

		for (int i = 0; i < 10; i++){
			if (i == 0 || i == 4)
				assertEquals(30000, activities.get(i).getSessionDuration().intValue());
			else if (i == 8)
				assertEquals(10000, activities.get(i).getSessionDuration().intValue());
			else
				assertNull(activities.get(i).getSessionDuration());
		}
	}

	public void testGetActivityCount(){
		setUpV2();
		assertEquals(3, service.getActivityCount(activities, UserActivityType.PasswordLogin));
		assertEquals(3, service.getActivityCount(activities, UserActivityType.AdvancedSearch));
		assertEquals(2, service.getActivityCount(activities, UserActivityType.CreateEdge));
		assertEquals(2, service.getActivityCount(activities, UserActivityType.Logout));
		assertEquals(0, service.getActivityCount(activities, UserActivityType.SimpleSearch));
	}

	public void testGetAvgDurationUserMode(){
		setUpV2();
		List<User> users = fillUserList();
		assertEquals(40000, service.getAvgDuration(users));
	}

	public void testGetAvgDurationActivityMode(){
		setUpV2();
		Map<UserActivityType, List<UserActivity>> activityMap = fillActivityMap();
		assertEquals(40000, service.getAvgDuration(activityMap));
	}

	public void testGetTotalDuration(){
		setUpV2();
		assertEquals(120000, service.getTotalDuration(activities));
	}

	public void testGetTotalLoginCount(){
		setUpV2();
		assertEquals(3, service.getTotalLoginCount(activities));
	}

	public void testGetActivityCountsActivityMode(){
		setUpV2();
		Map<UserActivityType, List<UserActivity>> activityMap = fillActivityMap();
		Map<TextID, Integer> activityCounts = service.getActivityCounts(activityMap);

		assertEquals(new Integer(3), activityCounts.get(TextID.Login));
		assertEquals(new Integer(3), activityCounts.get(TextID.ActivityAdvancedSearch));
		assertEquals(new Integer(2), activityCounts.get(TextID.ActivityCreateEdge));
		assertEquals(new Integer(2), activityCounts.get(TextID.ActivityLogout));
		assertEquals(new Integer(0), activityCounts.get(TextID.ActivitySimpleSearch));
		assertEquals(new Integer(0), activityCounts.get(TextID.ActivityCreateNode));
		assertEquals(new Integer(0), activityCounts.get(TextID.ActivityInvokeFavorite));
		assertNull(activityCounts.get(TextID.ActivityPasswordLogin));
		assertNull(activityCounts.get(TextID.ActivitySIDLogin));
	}

	public void testGetActivityCountsUserMode(){
		setUpV2();
		List<User> users = fillUserList();
		Map<TextID, Integer> activityCounts = service.getActivityCounts(users);

		assertEquals(new Integer(3), activityCounts.get(TextID.Login));
		assertEquals(new Integer(3), activityCounts.get(TextID.ActivityAdvancedSearch));
		assertEquals(new Integer(2), activityCounts.get(TextID.ActivityCreateEdge));
		assertEquals(new Integer(2), activityCounts.get(TextID.ActivityLogout));
		assertEquals(new Integer(0), activityCounts.get(TextID.ActivitySimpleSearch));
		assertEquals(new Integer(0), activityCounts.get(TextID.ActivityCreateNode));
		assertEquals(new Integer(0), activityCounts.get(TextID.ActivityInvokeFavorite));
		assertNull(activityCounts.get(TextID.ActivityPasswordLogin));
		assertNull(activityCounts.get(TextID.ActivitySIDLogin));
	}

	public void testGetActivityCountsUserModeSIDLogin(){
		setUpV2();
		List<User> users = fillUserList();
		UserActivity ua = new UserActivity();
		ua.setActivityType(UserActivityType.SIDLogin.getValue().toString());
		ua.setSessionDuration(new Long(300000));
		ua.setUser(users.get(0));
		activities.add(ua);

		Map<TextID, Integer> activityCounts = service.getActivityCounts(users);

		assertEquals(new Integer(4), activityCounts.get(TextID.Login));
		assertNull(activityCounts.get(TextID.ActivityPasswordLogin));
		assertNull(activityCounts.get(TextID.ActivitySIDLogin));
	}

	private List<User> fillUserList(){
		List<User> users = new ArrayList<User>();
		User user = new User();
		user.setActivities(activities);
		users.add(user);
		return users;
	}

	private Map<UserActivityType, List<UserActivity>> fillActivityMap(){
		Map<UserActivityType, List<UserActivity>> activityMap = new HashMap<UserActivityType, List<UserActivity>>();
		for (UserActivity activity : activities){
			UserActivityType type = activity.getUserActivityType();
			if (!activityMap.containsKey(type)){
				activityMap.put(type, new ArrayList<UserActivity>());
			}
			activityMap.get(type).add(activity);
		}
		return activityMap;
	}

}
