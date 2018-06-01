/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.session;

import java.util.Arrays;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class ObjectHolder{
	private ObjectHolder(){
	}

	private static ObjectHolder instance;
	static{
		instance = new ObjectHolder();
	}

	private ObjectDefinition currentObject;
	private Object[] currentPath;

	public static synchronized ObjectHolder getInstance(){
		return instance;
	}

	public ObjectDefinition getCurrentObject(){
		return currentObject;
	}

	public void setCurrentObject(ObjectDefinition currentObject){
		this.currentObject = currentObject;
	}

	public Object[] getCurrentPath(){
		if (currentPath == null){
			DatabaseInstance instance = SessionData.getInstance().getCurrentDatabaseInstance();
			if (instance != null){
				return new Object[] { new ACRootNode(), instance };
			}
		}
		return currentPath;
	}

	public void setCurrentPath(Object[] currentPath){
		if (currentPath == null){
			this.currentPath = null;
			return;
		}
		this.currentPath = new Object[currentPath.length];
		System.arraycopy(currentPath, 0, this.currentPath, 0, currentPath.length);
	}

	public Object[] getMaxPath(Class<?>[] classes){
		Object[] currentPath = getCurrentPath();
		int gap = 2; // constant gap (root, databaseinstance)
		if (currentPath == null || currentPath.length <= gap){
			return currentPath;
		}
		Object[] maxPath = currentPath;
		for (int i = 0; i < classes.length; i++){
			int pathIndex = i + gap;
			Object object = currentPath[i + gap];
			if (!object.getClass().equals(classes[i])){
				maxPath = Arrays.copyOf(currentPath, pathIndex);
				break;
			} else if (i >= classes.length - 1 || pathIndex >= currentPath.length - 1){
				maxPath = Arrays.copyOf(currentPath, pathIndex + 1);
				break;
			}
		}
		return maxPath;
	}

}
