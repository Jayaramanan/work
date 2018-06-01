package com.ni3.ag.navigator.server.servlets.mocks;

import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

@SuppressWarnings("deprecation")
public class HttpSessionImpl implements HttpSession{
	String id = UUID.randomUUID().toString();

	@Override
	public Object getAttribute(String arg0){
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<?> getAttributeNames(){
		throw new UnsupportedOperationException();
	}

	@Override
	public long getCreationTime(){
		throw new UnsupportedOperationException();
	}

	@Override
	public String getId(){
		return id;
	}

	@Override
	public long getLastAccessedTime(){
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMaxInactiveInterval(){
		throw new UnsupportedOperationException();
	}

	@Override
	public ServletContext getServletContext(){
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpSessionContext getSessionContext(){
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getValue(String arg0){
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getValueNames(){
		throw new UnsupportedOperationException();
	}

	@Override
	public void invalidate(){
	}

	@Override
	public boolean isNew(){
		throw new UnsupportedOperationException();
	}

	@Override
	public void putValue(String arg0, Object arg1){
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAttribute(String arg0){
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeValue(String arg0){
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttribute(String arg0, Object arg1){
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMaxInactiveInterval(int arg0){
		throw new UnsupportedOperationException();
	}

}
