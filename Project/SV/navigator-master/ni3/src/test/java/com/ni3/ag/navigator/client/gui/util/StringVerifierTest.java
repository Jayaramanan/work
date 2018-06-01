/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.util;

import junit.framework.TestCase;

public class StringVerifierTest extends TestCase{

	public void testCheckValidCharactersAllValid(){
		String validChars = "abcDEF123~!@#$%^&*()_+";
		StringVerifier verifier = new StringVerifier(validChars, "");
		assertTrue(verifier.checkValidCharacters(validChars));
		assertTrue(verifier.checkValidCharacters("321cba"));
		assertTrue(verifier.checkValidCharacters("abcabcabc"));
		assertTrue(verifier.checkValidCharacters("D+E"));
	}

	public void testCheckValidCharactersNotValid(){
		String validChars = "abcDEF123~!@#$%^&*()_+";
		StringVerifier verifier = new StringVerifier(validChars, "");
		assertFalse(verifier.checkValidCharacters("abcdef"));
		assertFalse(verifier.checkValidCharacters("1+2=3"));
		assertFalse(verifier.checkValidCharacters(" a b c"));
	}

	public void testCheckInvalidCharactersAllValid(){
		String invalidChars = "abcDEF123~!@#$%^&*()_+";
		StringVerifier verifier = new StringVerifier("", invalidChars);
		assertTrue(verifier.checkInvalidCharacters("ABCdef456="));
		assertTrue(verifier.checkInvalidCharacters("test"));
		assertTrue(verifier.checkInvalidCharacters("=-/\\][ "));
	}

	public void testCheckInvalidCharactersNotValid(){
		String invalidChars = "abcDEF123~!@#$%^&*()_+";
		StringVerifier verifier = new StringVerifier("", invalidChars);
		assertFalse(verifier.checkInvalidCharacters("abcdef"));
		assertFalse(verifier.checkInvalidCharacters("1+2=3"));
		assertFalse(verifier.checkInvalidCharacters(" 000001"));
	}
}
