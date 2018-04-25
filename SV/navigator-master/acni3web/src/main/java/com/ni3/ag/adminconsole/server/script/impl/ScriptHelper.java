/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.script.impl;

import java.util.ArrayList;
import java.util.List;

public class ScriptHelper{
	public static List<String> getParams(String formula){
		int from = formula.indexOf("=begin") + 6;
		int to = formula.indexOf("=end");
		if (from < 0 || to < 0){
			if (formula.indexOf("@") < 0){
				return null;
			} else{
				return getParamsFromCode(formula);
			}
		}
		String dataBlock = formula.substring(from, to);
		List<String> params = new ArrayList<String>();
		int fromIndex = -1;
		while ((fromIndex = dataBlock.indexOf("{")) >= 0){
			int toIndex = dataBlock.indexOf("}");
			String param = dataBlock.substring(fromIndex + 1, toIndex);
			params.add(param);
			dataBlock = dataBlock.substring(toIndex + 1);
		}
		return params;
	}

	public static List<String> getParamsFromCode(String formula){
		List<String> params = new ArrayList<String>();
		String[] tokens = formula.split("@");
		char underscore = '_';
		for (int t = 1; t < tokens.length; t++){
			String token = tokens[t];
			if (token == null || token.isEmpty()){
				continue;
			}
			String param = null;
			for (int i = 0; i < token.length(); i++){
				char c = token.charAt(i);
				if (!(Character.isLetterOrDigit(c) || underscore == c)){
					param = token.substring(0, i);
					break;
				}
				if (i == token.length() - 1){
					param = token.substring(0);
				}
			}
			if (param != null && !params.contains(param)){
				params.add(param);
			}
		}
		return params;
	}
}
