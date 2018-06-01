/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.remoting;

public class ThreadLocalStorage{
	private ThreadLocal<String> currentDatabaseInstanceId = new ThreadLocal<String>();

	private static ThreadLocalStorage instance = null;
	private static Object lock = new Object();

	public static ThreadLocalStorage getInstance(){
		synchronized (lock){
			if (instance == null){
				instance = new ThreadLocalStorage();
			}
			return instance;
		}
	}

	public void setCurrentDatabaseInstanceId(String s){
		currentDatabaseInstanceId.set(s);
	}

	public String getCurrentDatabaseInstanceId(){
		return currentDatabaseInstanceId.get();
	}

	public void removeCurrentDatabaseInstanceId(){
		currentDatabaseInstanceId.remove();
	}
}
