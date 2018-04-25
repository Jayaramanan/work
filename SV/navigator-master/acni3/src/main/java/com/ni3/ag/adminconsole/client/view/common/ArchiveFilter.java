/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ArchiveFilter extends FileFilter{
	public final static String ZIP = ".zip";
	public final static String BZIP = ".tar.bz2";

	public boolean accept(File f){
		if (f.isDirectory()){
			return true;
		}
		String name = f.getName().toLowerCase();
		return name.endsWith(ZIP) || name.endsWith(BZIP);
	}

	public String getDescription(){
		return Translation.get(TextID.ZipArchives);
	}
}
