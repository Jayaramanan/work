/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useractivity;

import java.text.ParseException;
import java.util.Date;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.useractivity.UserActivityTableModelUser;
import com.ni3.ag.adminconsole.domain.UserActivity;

public class UserActivityTableModelUserTest extends ACTestCase{

	public void testGetActivityTime() throws ParseException{
		UserActivityTableModelUser model = new UserActivityTableModelUser();

		assertNull(model.getActivityTime(""));

		UserActivity activity = new UserActivity();
		assertNull(model.getActivityTime(activity));

		Date now = new Date(0);
		activity.setDateTime(now);
		String time = model.getActivityTime(activity);
		assertNotNull(time);
		Date parsed = UserActivityTableModelUser.DATE_FORMAT.parse(time);

		assertEquals(now, parsed);
	}

}
