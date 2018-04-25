/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import com.ni3.ag.adminconsole.shared.service.def.DeploymentVersionService;

public class DeploymentVersionServiceImpl implements DeploymentVersionService{
	private String deploymentVersion;

	public void setDeploymentVersion(String deploymentVersion){
		this.deploymentVersion = deploymentVersion;
	}

	public String getDeploymentVersion(){
		return deploymentVersion;
	}
}
