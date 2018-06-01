/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;

import junit.framework.TestCase;

public class ValidationUtilsTest extends TestCase {
    public void testIsIconNameValid() {
        assertTrue(ValidationUtils.isIconNameValid("name_1.ext"));
        assertFalse(ValidationUtils.isIconNameValid("name-1.ext"));
        assertFalse(ValidationUtils.isIconNameValid("name^1.ext"));
        assertFalse(ValidationUtils.isIconNameValid("name^1"));
    }
}
