/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.OfflineJob;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.jobs.OfflineJobStatus;

public class OfflineJobTableModelTest extends TestCase{
	private OfflineJobTableModel model;
	private List<OfflineJob> jobs;
	private List<User> allUsers;
	private List<Group> groups;

	public void setUp(){
		jobs = generateJobs();
		allUsers = generateUsers();
		groups = new ArrayList<Group>();
		Group group = new Group();
		group.setUsers(allUsers);
		groups.add(group);
		model = new OfflineJobTableModel(jobs, groups);
	}

	private List<User> generateUsers(){
		List<User> users = new ArrayList<User>();
		for (int i = 1; i < 6; i++){
			User user = new User();
			user.setUserName("user" + i);
			user.setId(i + i * 10);
			users.add(user);
		}
		return users;
	}

	private List<OfflineJob> generateJobs(){
		List<OfflineJob> jobs = new ArrayList<OfflineJob>();
		for (int i = 0; i < 5; i++){
			OfflineJob job = new OfflineJob();
			job.setUserIds("" + (i + 1) + (i + 1));
			job.setWithFirstDegreeObjects(true);
			job.setTimeStart(new Date());
			job.setTimeEnd(new Date());
			job.setStatus(OfflineJobStatus.InProgress.getValue());
			User trigger = new User();
			trigger.setId((i + 1) * 2);
			job.setTriggeredBy(trigger);
			jobs.add(job);
		}
		return jobs;
	}

	public void testColumnCount(){
		assertEquals(6, model.getColumnCount());
	}

	public void testGetRowCount(){
		assertEquals(model.getRowCount(), jobs.size());
	}

	public void testGetValueAt(){
		for (int i = 0; i < jobs.size(); i++){
			OfflineJob job = jobs.get(i);
			assertEquals(allUsers.get(i).getUserName(), model.getValueAt(i, 0));
			assertEquals(job.getWithFirstDegreeObjects(), model.getValueAt(i, 1));
			assertEquals(job.getTimeStart(), model.getValueAt(i, 2));
			assertEquals(job.getTimeEnd(), model.getValueAt(i, 3));
			assertEquals(OfflineJobStatus.getStatus(job.getStatus()), model.getValueAt(i, 4));
			assertEquals(job.getTriggeredBy(), model.getValueAt(i, 5));
		}
	}

	public void testColumnEditable(){
		for (int i = 0; i < model.getRowCount(); i++){
			OfflineJob job = jobs.get(i);
			boolean isScheduled = OfflineJobStatus.Scheduled.equals(OfflineJobStatus.getStatus(job.getStatus()));
			assertEquals(isScheduled, model.isCellEditable(i, 0));
			assertEquals(isScheduled, model.isCellEditable(i, 1));
			assertEquals(isScheduled, model.isCellEditable(i, 2));
			assertFalse(model.isCellEditable(i, 3));
			assertFalse(model.isCellEditable(i, 4));
			assertFalse(model.isCellEditable(i, 5));
		}
	}

	public void testSetValueAt(){
		long millis = System.currentTimeMillis();
		for (int i = 0; i < jobs.size(); i++){
			model.setValueAt("user" + (i + 1), i, 0);
			model.setValueAt(false, i, 1);
			model.setValueAt(new Date(millis + 1000), i, 2);

			OfflineJob job = jobs.get(i);
			assertEquals("" + (i + 1) + (i + 1), job.getUserIds());
			assertEquals(Boolean.FALSE, job.getWithFirstDegreeObjects());
			assertEquals(new Date(millis + 1000), job.getTimeStart());

		}
	}

	public void testUserIdsToUserNames(){
		assertNull(model.userIdsToUserNames(""));
		assertNull(model.userIdsToUserNames(null));
		assertNull(model.userIdsToUserNames("500"));
		assertEquals("user1", model.userIdsToUserNames("11"));
		assertEquals("user2,user3", model.userIdsToUserNames("22,33"));
		assertEquals("user4,user3,user1", model.userIdsToUserNames("44,33,11"));
	}

	public void testUserNamesToUserIds(){
		assertNull(model.userNamesToUserIds(""));
		assertNull(model.userNamesToUserIds(null));
		assertNull(model.userNamesToUserIds("user500"));
		assertEquals("11", model.userNamesToUserIds("user1"));
		assertEquals("22,33", model.userNamesToUserIds("user2,user3"));
		assertEquals("44,33,11", model.userNamesToUserIds("user4,user3,user1"));
	}

	public void testCorrectString(){
		assertNull(model.correctString(""));
		assertNull(model.correctString(null));
		assertEquals("abc,def", model.correctString("abc,def,"));
	}
}
