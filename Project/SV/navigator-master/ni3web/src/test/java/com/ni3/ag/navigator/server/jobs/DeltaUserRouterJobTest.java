package com.ni3.ag.navigator.server.jobs;

import junit.framework.TestCase;

import org.quartz.JobExecutionException;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.DeltaHeaderUserDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.User;

public class DeltaUserRouterJobTest extends TestCase{

	public void testShouldBeRouted(){
		final User user = new User();
		user.setId(1);
		final User other = new User();
		other.setId(2);

		final DeltaHeader myLocalNew = new DeltaHeader(DeltaType.EDGE_CREATE, user, null);
		myLocalNew.setSync(false);
		final DeltaHeader myLocalEdit = new DeltaHeader(DeltaType.EDGE_UPDATE, user, null);
		myLocalEdit.setSync(false);
		final DeltaHeader myLocalDelete = new DeltaHeader(DeltaType.OBJECT_DELETE, user, null);
		myLocalDelete.setSync(false);

		final DeltaHeader myOfflineNew = new DeltaHeader(DeltaType.EDGE_CREATE, user, null);
		myOfflineNew.setSync(true);
		final DeltaHeader myOfflineEdit = new DeltaHeader(DeltaType.EDGE_UPDATE, user, null);
		myOfflineEdit.setSync(true);
		final DeltaHeader myOfflineDelete = new DeltaHeader(DeltaType.OBJECT_DELETE, user, null);
		myOfflineDelete.setSync(true);

		final DeltaHeader otherLocalNew = new DeltaHeader(DeltaType.EDGE_CREATE, other, null);
		otherLocalNew.setSync(false);
		final DeltaHeader otherLocalEdit = new DeltaHeader(DeltaType.EDGE_UPDATE, other, null);
		otherLocalEdit.setSync(false);
		final DeltaHeader otherLocalDelete = new DeltaHeader(DeltaType.OBJECT_DELETE, other, null);
		otherLocalDelete.setSync(false);

		final DeltaHeader otherOfflineNew = new DeltaHeader(DeltaType.EDGE_CREATE, other, null);
		otherOfflineNew.setSync(true);
		final DeltaHeader otherOfflineEdit = new DeltaHeader(DeltaType.EDGE_UPDATE, other, null);
		otherOfflineEdit.setSync(true);
		final DeltaHeader otherOfflineDelete = new DeltaHeader(DeltaType.OBJECT_DELETE, other, null);
		otherOfflineDelete.setSync(true);

		final DeltaUserRouterJob job = new DeltaUserRouterJob();
		assertTrue(job.shouldBeRouted(myLocalNew, user.getId()));
		assertTrue(job.shouldBeRouted(myLocalEdit, user.getId()));
		assertTrue(job.shouldBeRouted(myLocalDelete, user.getId()));
		assertFalse(job.shouldBeRouted(myOfflineNew, user.getId()));
		assertTrue(job.shouldBeRouted(myOfflineEdit, user.getId()));
		assertFalse(job.shouldBeRouted(myOfflineDelete, user.getId()));
		assertTrue(job.shouldBeRouted(otherLocalNew, user.getId()));
		assertTrue(job.shouldBeRouted(otherLocalEdit, user.getId()));
		assertTrue(job.shouldBeRouted(otherLocalDelete, user.getId()));
		assertTrue(job.shouldBeRouted(otherOfflineNew, user.getId()));
		assertTrue(job.shouldBeRouted(otherOfflineEdit, user.getId()));
		assertTrue(job.shouldBeRouted(otherOfflineDelete, user.getId()));

		assertTrue(job.shouldBeRouted(myLocalNew, other.getId()));
		assertTrue(job.shouldBeRouted(myLocalEdit, other.getId()));
		assertTrue(job.shouldBeRouted(myLocalDelete, other.getId()));
		assertTrue(job.shouldBeRouted(myOfflineNew, other.getId()));
		assertTrue(job.shouldBeRouted(myOfflineEdit, other.getId()));
		assertTrue(job.shouldBeRouted(myOfflineDelete, other.getId()));
		assertTrue(job.shouldBeRouted(otherLocalNew, other.getId()));
		assertTrue(job.shouldBeRouted(otherLocalEdit, other.getId()));
		assertTrue(job.shouldBeRouted(otherLocalDelete, other.getId()));
		assertFalse(job.shouldBeRouted(otherOfflineNew, other.getId()));
		assertTrue(job.shouldBeRouted(otherOfflineEdit, other.getId()));
		assertFalse(job.shouldBeRouted(otherOfflineDelete, other.getId()));
	}

	public void testExecute() throws JobExecutionException{
		final DeltaUserRouterJob job = new DeltaUserRouterJob();
		job.execute(null);

		// Hack: mocked getUnprocessedCountForUser() returns number of create() calls.
		final DeltaHeaderUserDAO dao = NSpringFactory.getInstance().getDeltaHeaderUserDAO();
		final Long count = dao.getUnprocessedCountForUser(0);
		assertEquals(Long.valueOf(100), count);
	}
}
