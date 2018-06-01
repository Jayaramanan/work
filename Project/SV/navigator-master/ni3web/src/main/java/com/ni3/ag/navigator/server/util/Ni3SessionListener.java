package com.ni3.ag.navigator.server.util;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

public class Ni3SessionListener implements HttpSessionListener{
	private static final Logger log = Logger.getLogger(Ni3SessionListener.class);

	public Ni3SessionListener(){
	}

	@Override
	public void sessionCreated(HttpSessionEvent httpSessionEvent){
		HttpSession session = httpSessionEvent.getSession();
		log.debug("JUST CREATED SESSION: " + session.getId());
		dumpStack(Thread.currentThread().getStackTrace());
	}

	private void dumpStack(StackTraceElement[] stackTrace){
		final StringBuilder result = new StringBuilder("SESSION CREATION STACK: ");
		final String NEW_LINE = System.getProperty("line.separator");
		result.append(NEW_LINE);

		//add each element of the stack trace
		for (StackTraceElement element : stackTrace){
			result.append("\t\t@ ").append(element);
			result.append(NEW_LINE);
		}
		log.debug(result.toString());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent httpSessionEvent){
		HttpSession session = httpSessionEvent.getSession();
		log.debug("SESSION TO BE DESTROYED: " + session.getId());
	}
}
