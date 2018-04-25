/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ImageFilter extends FileFilter{
	public final static String JPEG = "jpeg";
	public final static String JPG = "jpg";
	public final static String GIF = "gif";
	public final static String PNG = "png";

	public boolean accept(File f){
		if (f.isDirectory()){
			return true;
		}
		String extension = getExtension(f);
		if (extension != null){
			if (extension.equalsIgnoreCase(JPEG) || extension.equalsIgnoreCase(JPG) || extension.equalsIgnoreCase(GIF)
			        || extension.equalsIgnoreCase(PNG)){
				return true;
			} else{
				return false;
			}
		}

		return false;
	}

	public String getDescription(){
		return Translation.get(TextID.OnlyImages);
	}

	public static String getExtension(File f){
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1){
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
}
