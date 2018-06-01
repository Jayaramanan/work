/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class LanguageTest extends TestCase{
	Language language;

	@Override
	protected void setUp() throws Exception{
		language = new Language();
	}

	public void testEquals(){
		Language l1 = new Language();
		Language l2 = new Language();
		assertTrue(l1.equals(l1));
		assertTrue(l2.equals(l2));
		assertFalse(l1.equals(l2));
		assertFalse(l2.equals(l1));
		l1.setId(1);
		assertFalse(l1.equals(l2));
		assertFalse(l2.equals(l1));
		l2.setId(2);
		assertFalse(l1.equals(l2));
		assertFalse(l2.equals(l1));
		l2.setId(1);
		assertTrue(l1.equals(l2));
		assertTrue(l2.equals(l1));
	}

	public void testCompareToWithNulls(){
		language.setLanguage(null);
		assertEquals(0, language.compareTo(null));
	}

	public void testCompareToWithNullArg(){
		language.setLanguage("Language");
		assertTrue(language.compareTo(null) > 0);
	}

	public void testCompareToWithNullLanguage(){
		language.setLanguage(null);
		Language arg = new Language();
		arg.setLanguage("ArgLanguage");
		assertTrue(language.compareTo(arg) < 0);
	}

	public void testCompareToWithNullArgLanguage(){
		language.setLanguage("Language");
		Language arg = new Language();
		arg.setLanguage(null);
		assertTrue(language.compareTo(arg) > 0);
	}

	public void testCompareToEquals(){
		language.setLanguage("Language");
		Language arg = new Language();
		arg.setLanguage("Language");
		assertEquals(0, language.compareTo(arg));
	}

	public void testCompareToArgIsLess(){
		language.setLanguage("ALanguage");
		Language arg = new Language();
		arg.setLanguage("BLanguage");
		assertTrue(language.compareTo(arg) < 0);
	}

	public void testCompareToArgIsGreater(){
		language.setLanguage("BLanguage");
		Language arg = new Language();
		arg.setLanguage("ALanguage");
		assertTrue(language.compareTo(arg) > 0);
	}

	public void testEqualsSameIds(){
		language.setId(1);
		Language arg = new Language();
		arg.setId(1);
		assertTrue(language.equals(arg));
	}

	public void testEqualsDiffernetIds(){
		language.setId(1);
		Language arg = new Language();
		arg.setId(2);
		assertFalse(language.equals(arg));
	}

	public void testEqualsSameObjects(){
		Language arg = language;
		assertTrue(language.equals(arg));
	}

	public void testEqualsNullArg(){
		language.setId(1);
		assertFalse(language.equals(null));
	}
}
