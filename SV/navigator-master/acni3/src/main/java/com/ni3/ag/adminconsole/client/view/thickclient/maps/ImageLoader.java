/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.maps;

import java.net.URL;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.applet.ACMain;


public class ImageLoader{

	private final static Logger log = Logger.getLogger(ImageLoader.class);

	public static ImageIcon loadIcon(String iconName){
		try{
			URL imageURL = ACMain.class.getResource(iconName);
			ImageIcon icon = new ImageIcon(imageURL);
			return icon;
		} catch (Exception e){
			log.error("Fatal Error, can not load icon: " + iconName, e);
			return null;
		}
	}
}
