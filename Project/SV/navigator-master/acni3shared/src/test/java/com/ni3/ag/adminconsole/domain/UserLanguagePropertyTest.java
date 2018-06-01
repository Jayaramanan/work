/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class UserLanguagePropertyTest extends TestCase{

	public void testEqualsObject(){
		Language l = new Language();
		l.setId(1);
		UserLanguageProperty ulp1 = new UserLanguageProperty(l);
		UserLanguageProperty ulp2 = new UserLanguageProperty(l);
		assertTrue(ulp1.equals(ulp1));
		assertTrue(ulp2.equals(ulp2));
		assertFalse(ulp1.equals(ulp2));
		assertFalse(ulp2.equals(ulp1));
		ulp1.setProperty("prop");
		assertFalse(ulp1.equals(ulp2));
		assertFalse(ulp2.equals(ulp1));
		ulp2.setProperty("prop");
		assertTrue(ulp1.equals(ulp2));
		assertTrue(ulp2.equals(ulp1));
	}

}
