/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.util.Date;

import com.ni3.ag.adminconsole.shared.jobs.OfflineJobStatus;

import junit.framework.TestCase;

public class OfflineJobTest extends TestCase{
	public void testEquals(){
		OfflineJob c1 = new OfflineJob();
		OfflineJob c2 = new OfflineJob();
		assertFalse(c1.equals(c2));
		assertFalse(c2.equals(c1));
		c1.setId(1);
		assertFalse(c1.equals(c2));
		assertFalse(c2.equals(c1));
		c2.setId(2);
		assertFalse(c1.equals(c2));
		assertFalse(c2.equals(c1));
		c2.setId(1);
		assertTrue(c1.equals(c2));
		assertTrue(c2.equals(c1));
	}

	public void testCompareTo(){
		OfflineJob c1 = new OfflineJob();
		OfflineJob c2 = new OfflineJob();
		OfflineJobStatus status1 = OfflineJobStatus.Ok; // 2
		OfflineJobStatus status2 = OfflineJobStatus.ScheduledWaiting;// 10

		assertEquals(-1, c1.compareTo(null));
		assertEquals(-1, c1.compareTo(c2));
		assertEquals(-1, c2.compareTo(null));
		assertEquals(-1, c2.compareTo(c1));

		c1.setStatus(status1.getValue());
		assertEquals(1, c1.compareTo(null));
		assertEquals(1, c1.compareTo(c2));
		assertEquals(-1, c2.compareTo(c1));

		c2.setStatus(status2.getValue());
		assertTrue(c1.compareTo(c2) > 0);
		assertTrue(c2.compareTo(c1) < 0);

		c2.setStatus(status1.getValue());
		long now = System.currentTimeMillis();
		c1.setTimeStart(new Date(now));
		assertTrue(c1.compareTo(c2) > 0);
		assertTrue(c2.compareTo(c1) < 0);

		c2.setTimeStart(new Date(now + 10));
		assertTrue(c1.compareTo(c2) > 0);
		assertTrue(c2.compareTo(c1) < 0);

		c2.setTimeStart(new Date(now - 10));
		assertTrue(c1.compareTo(c2) < 0);
		assertTrue(c2.compareTo(c1) > 0);
	}
}
