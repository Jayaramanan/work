/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model;

import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public abstract class AbstractModel{

	protected DatabaseInstance currentDatabaseInstance;

	public void setCurrentDatabaseInstance(DatabaseInstance currentDatabaseInstance){
		this.currentDatabaseInstance = currentDatabaseInstance;
	}

	public DatabaseInstance getCurrentDatabaseInstance(){
		return currentDatabaseInstance;
	}
}
