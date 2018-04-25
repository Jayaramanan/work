/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.license;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.ni3.ag.adminconsole.domain.License;

import junit.framework.TestCase;

public class LicenseDataTest extends TestCase{

	public void testDateFormat() throws ParseException{
		assertNotNull(LicenseData.LICENSE_DATE_FORMAT);
		Date dt = new Date();
		String sresult = LicenseData.LICENSE_DATE_FORMAT.format(dt);
		assertNotNull(sresult);
		Date dresult = LicenseData.LICENSE_DATE_FORMAT.parse(sresult);
		assertNotNull(dresult);
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.AM_PM, Calendar.AM);
		c.set(Calendar.MILLISECOND, 0);
		dt = c.getTime();
		assertEquals(dresult, dt);
	}

	public void testSetGetLicense(){
		License l = new License();
		l.setId(1);
		l.setLicense("license");
		l.setProduct("test");
		LicenseData ld = new LicenseData();
		assertNull(ld.getLicense());
		ld.setLicense(l);
		assertEquals(l, ld.getLicense());
	}

	public void testStatus(){
		LicenseData ld = new LicenseData();
		ld.setValid(false);
		assertFalse(ld.isValid());
		assertEquals(LicenseData.LicenseStatus.Invalid, ld.getStatus());

		ld.setValid(true);
		assertTrue(ld.isValid());
	}

	public void testStatus2(){
		for (LicenseData.LicenseStatus ls : LicenseData.LicenseStatus.values()){
			LicenseData ld = new LicenseData();
			ld.setStatus(ls);
			assertEquals(ld.getStatus(), ls);
		}
	}

	public void testEquals(){
		LicenseData ld = new LicenseData();
		LicenseData ld2 = new LicenseData();
		License l = new License();
		ld.setLicense(l);
		License l2 = new License();
		ld2.setLicense(l2);
		assertFalse(ld.equals(ld2));
		assertFalse(ld2.equals(ld));
		l.setId(1);
		assertFalse(ld.equals(ld2));
		assertFalse(ld2.equals(ld));
		l2.setId(2);
		assertFalse(ld.equals(ld2));
		assertFalse(ld2.equals(ld));
		l2.setId(1);
		assertTrue(ld.equals(ld2));
		assertTrue(ld2.equals(ld));
	}
}
