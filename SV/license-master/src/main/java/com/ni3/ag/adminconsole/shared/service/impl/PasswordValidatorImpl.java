/** * Copyright (c) 2009-2011 Ni3 AG. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.shared.service.def.PasswordValidator;

public class PasswordValidatorImpl implements PasswordValidator{

	private final static Logger log = Logger.getLogger(PasswordValidatorImpl.class);
	private final static String SPLITTER = "##";
	private final static String REG_START_SYMBOL = "[";
	private final static String REG_END_SYMBOL = "]";
	private final static String MIN_COUNT_START_SYMBOL = "{";
	private final static String MIN_COUNT_END_SYMBOL = "}";
	private final static String MIN_PASS_SIZE_SYMBOL = "[.]";
	private List<Object[]> rules;

	public PasswordValidatorImpl(){
	}

	private List<Object[]> parse(String passwordFormat){
		if (passwordFormat == null || passwordFormat.isEmpty()){
			return null;
		}
		List<Object[]> result = new ArrayList<Object[]>();

		log.debug("Parsing password format " + passwordFormat);
		String[] expressions = passwordFormat.split(SPLITTER);
		for (String exp : expressions){
			int rFrom = exp.indexOf(REG_START_SYMBOL);
			int rTo = exp.indexOf(REG_END_SYMBOL, rFrom);

			int minFrom = exp.indexOf(MIN_COUNT_START_SYMBOL, rTo);
			int minTo = exp.indexOf(MIN_COUNT_END_SYMBOL, minFrom);

			if (rFrom < 0 || rTo < 0 || minFrom < 0 || minTo < 0){
				log.error("Cannot parse password format: " + passwordFormat + ", expression: " + exp);
				return null;
			}

			String regExpr = exp.substring(rFrom, rTo + 1);
			if (regExpr.isEmpty()){
				log.error("Cannot parse password format: " + passwordFormat + ", expression: " + exp);
				return null;
			}
			String count = exp.substring(minFrom + 1, minTo);
			Integer intCount = 0;
			try{
				intCount = Integer.parseInt(count);
			} catch (NumberFormatException e){
				log.error("Cannot parse password format: " + passwordFormat + ", expression: " + exp);
				return null;
			}
			result.add(new Object[] { regExpr, intCount });
		}
		return result;
	}

	@Override
	public boolean parseFormat(String passwordFormat){
		if (passwordFormat == null)
			return false;
		rules = parse(passwordFormat);
		if (rules == null || rules.isEmpty())
			return false;

		for (Object[] rule : rules){
			try{
				if (MIN_PASS_SIZE_SYMBOL.equals(rule[0]))
					continue;
				Pattern.compile((String) rule[0]);
			} catch (PatternSyntaxException ex){
				log.error("Cannot compile pattern: " + rule[0]);
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isPasswordValid(String password){
		log.debug("Validating password");
		for (Object[] rule : rules){
			String expression = (String) rule[0];
			Integer count = (Integer) rule[1];
			if (MIN_PASS_SIZE_SYMBOL.equals(rule[0])){
				if (password.length() < count){
					log.warn("Password is too short, expected: " + count + ", current:" + password.length());
					return false;
				}
				continue;
			}
			Pattern pattern = Pattern.compile(expression);
			Matcher matcher = pattern.matcher(password);

			for (int i = 0; i < count; i++){
				if (!matcher.find()){
					log.warn("Password doesn't match pattern: " + rule[0]);
					return false;
				}
			}
		}
		return true;
	}

}
