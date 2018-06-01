/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class LicenseAdminModel extends AbstractModel{
	private Map<DatabaseInstance, List<LicenseData>> licenseMap = new HashMap<DatabaseInstance, List<LicenseData>>();
	private String updateLicenseText;
	private Object currentObject;

	public void setUpdateLicenseText(String updateLicenseText){
		this.updateLicenseText = updateLicenseText;
	}

	public String getUpdateLicenseText(){
		return updateLicenseText;
	}

	public void setLicenses(List<LicenseData> licenses){
		licenseMap.put(currentDatabaseInstance, licenses);
	}

	public Map<DatabaseInstance, List<LicenseData>> getLicenseMap(){
		return licenseMap;
	}

	public List<LicenseData> getLicenses(){
		return licenseMap.get(currentDatabaseInstance);
	}

	public void setCurrentObject(Object o){
		this.currentObject = o;
	}

	public Object getCurrentObject(){
		return currentObject;
	}

	public boolean isLicenseSelected(){
		return currentObject != null && currentObject instanceof LicenseData;
	}

	public boolean isInstanceLoaded(){
		return licenseMap.containsKey(currentDatabaseInstance);
	}

	public boolean isInstanceLoaded(DatabaseInstance dbInstance){
		return licenseMap.containsKey(dbInstance);
	}

}
