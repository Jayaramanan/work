/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class Ni3HttpServletRequest extends HttpServletRequestWrapper{
	private Map<String, String> additionalParameters;

	public Ni3HttpServletRequest(HttpServletRequest request){
		super(request);
		additionalParameters = new HashMap<String, String>();
	}

	@Override
	public String getParameter(String name){
		String rez = super.getParameter(name);
		if (rez != null)
			return rez;
		if (!additionalParameters.containsKey(name))
			return null;
		return additionalParameters.get(name);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public Map getParameterMap(){
		Map m = new HashMap();
		m.putAll(super.getParameterMap());
		m.putAll(additionalParameters);
		return m;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public Enumeration getParameterNames(){
		Vector v = new Vector();
		Enumeration e = super.getParameterNames();
		while (e.hasMoreElements())
			v.add(e.nextElement());
		v.addAll(additionalParameters.keySet());
		return v.elements();
	}

	@Override
	public String[] getParameterValues(String name){
		String[] rez = super.getParameterValues(name);
		if (rez != null)
			return rez;
		if (!additionalParameters.containsKey(name))
			return null;
		String val = additionalParameters.get(name);
		return new String[] { val };
	}

	public void putParameter(String key, String value){
		additionalParameters.put(key, value);
	}
}
