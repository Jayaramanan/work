/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.impl;

import com.ni3.ag.adminconsole.shared.service.impl.PasswordValidatorImpl;

import junit.framework.TestCase;

public class PasswordValidatorImplTest extends TestCase{

	public void testIsFormatCorrectAllCorrect(){
		String format = "[a-z]{1}##[A-Z]{1}##[0-9]{1}##[@#$%]{1}##[.]{8}";
		PasswordValidatorImpl validator = new PasswordValidatorImpl();
		assertTrue(validator.parseFormat(format));

		format = "[a-z]{1}";
		assertTrue(validator.parseFormat(format));

		format = "[.]{25}";
		assertTrue(validator.parseFormat(format));
	}

	public void testIsFormatCorrectAllInCorrect(){
		String format = "[a-z]##[A-Z]{1}##[0-9]{1}##[@#$%]{1}##[.]{8}";
		PasswordValidatorImpl validator = new PasswordValidatorImpl();
		assertFalse(validator.parseFormat(format));

		format = "[a-z]##[A-Z]";
		assertFalse(validator.parseFormat(format));

		format = "{25}";
		assertFalse(validator.parseFormat(format));

		format = "";
		assertFalse(validator.parseFormat(format));

		format = null;
		assertFalse(validator.parseFormat(format));
	}

	public void testIsPasswordValidMin1(){
		String format = "[a-z]{1}##[A-Z]{1}##[0-9]{1}##[@#$%]{1}##[.]{8}";
		PasswordValidatorImpl validator = new PasswordValidatorImpl();
		assertTrue(validator.parseFormat(format));
		String[] validPasswords = { "aB$d12ef", "Abcd1e2#$", "@aBCD123$4567", "aBcd3ef$" };
		String[] invalidPasswords = { "abcdefgh", "ABCDEFGH", "aBcdefgh", "aBcDeFgH", "abc1defgh", "aBCdefgh", "aB1c2e3f4",
		        "ab1c2e3f4", "aB12cdef", "aBCd12ef", "12345678", "abc$d12ef", "aBC$d1", "aBCdef$gsdf" };
		for (String invalid : invalidPasswords){
			assertFalse(invalid, validator.isPasswordValid(invalid));
		}

		for (String valid : validPasswords){
			assertTrue(valid, validator.isPasswordValid(valid));
		}
	}

	public void testIsPasswordValidMin2(){
		String format = "[a-z]{2}##[A-Z]{2}##[0-9]{2}##[@#$%]{2}##[.]{10}";
		PasswordValidatorImpl validator = new PasswordValidatorImpl();
		assertTrue(validator.parseFormat(format));
		String[] validPasswords = { "AB$@cd12ax", "AbcD1e2#$e", "@aBCd123$4567", "aBcd3efG$%4" };
		String[] invalidPasswords = { "abcdefghij", "ABCDEFGHIJKL", "aBcdefgh12345", "aBcDeFgH", "abc1defgh", "aBCdefgh",
		        "aB1c2e3f4", "ab1c2e3f4adb", "aeCaB12cdef", "aBCd12ef", "12345678", "aBC$d12ef", "aBC$@d12", "aBCdef$#g1sdf" };
		for (String invalid : invalidPasswords){
			assertFalse(invalid, validator.isPasswordValid(invalid));
		}

		for (String valid : validPasswords){
			assertTrue(valid, validator.isPasswordValid(valid));
		}
	}

	public void testIsPasswordValidOnlyLength(){
		String format = "[.]{5}";
		PasswordValidatorImpl validator = new PasswordValidatorImpl();
		assertTrue(validator.parseFormat(format));
		String[] validPasswords = { "AB$@c", "AbcD1e", "@aBCd123", "aBcd3efG$%4" };
		String[] invalidPasswords = { "", "A", "aB", "aBc", "abc1" };
		for (String invalid : invalidPasswords){
			assertFalse(invalid, validator.isPasswordValid(invalid));
		}

		for (String valid : validPasswords){
			assertTrue(valid, validator.isPasswordValid(valid));
		}
	}
}
