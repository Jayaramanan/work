/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.schemaadmin.NumberDocumentFilter;

public class NumberDocumentFilterTest extends ACTestCase{
	private NumberDocumentFilter filter;
	private Pattern digitPattern;

	public void setUp(){
		filter = new NumberDocumentFilter();
		digitPattern = Pattern.compile("[0-9]*");
	}

	public void testFilterLetters(){
		String res = filter.filterLetters("fh98@#cas8");
		Matcher m = digitPattern.matcher(res);
		assertTrue(m.matches());

		res = filter.filterLetters("f");
		m = digitPattern.matcher(res);
		assertTrue(m.matches());

		res = filter.filterLetters("");
		m = digitPattern.matcher(res);
		assertTrue(m.matches());
	}
}
