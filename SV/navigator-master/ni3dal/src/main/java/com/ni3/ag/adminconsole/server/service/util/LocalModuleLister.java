/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalModuleLister implements ModuleLister{

	private String modulePath;

	public LocalModuleLister(String path){
		modulePath = path;
	}

	@Override
	public List<String> list(){
		File dir = new File(modulePath);
		List<String> paths = new ArrayList<String>();
		if (dir.isDirectory()){
			File[] files = dir.listFiles();
			for (File f : files)
				paths.add(f.getName());
		}
		return paths;
	}

	@Override
	public boolean testPath(){
		File dir = new File(modulePath);
		return dir.exists();
	}
}
