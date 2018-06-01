/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.applet;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.shared.service.def.DeploymentVersionService;

public class ACMain extends JApplet{
	static final long serialVersionUID = 0;
	private static Double ScreenHeight;
	private static Double ScreenWidth;
	private static Container mainF = null;
	private static String version = "1.0.020";
	private static String ServerURL;
	private static String MetaphorURL;
	private static String ImagesURL;
	private static ImageIcon frameIcon;

	private static int minWidth = 1000;
	private static int minHeight = 500;

	private static String ServerContextRoot;
	private static Logger log = Logger.getLogger(ACMain.class);

	public static void main(String[] args){
		ScreenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		ScreenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		ServerURL = getArgument("ServerURL", args);
		MetaphorURL = getArgument("MetaphorURL", args);
		ImagesURL = getArgument("ImagesURL", args);
		ServerContextRoot = getArgument("ServerContextRoot", args);
		if (ServerContextRoot == null)
			ServerContextRoot = "ACNi3Web";

		//this should be done as early as possible, ACSpringFactory.getInstance().get* will not work otherwise
		Properties jnlpProperties = new Properties();
		jnlpProperties.setProperty("com.ni3.ag.adminconsole.server.serverUrl", ServerURL);

		//for compatibility reasons, to be removed at some point
		jnlpProperties.setProperty("com.ni3.ag.adminconsole.server.offlineclient.moduleTransferServlet", "ModuleTransfer");

		ACSpringFactory.init(jnlpProperties);

		DeploymentVersionService deploymentVersionService = ACSpringFactory.getInstance().getDeploymentVersionService();
		String deploymentVersion = deploymentVersionService.getDeploymentVersion();
		mainF = new JFrame("Admin Console, build " + deploymentVersion);
		mainF.setName("ACMainFrame");

		((JFrame) (mainF)).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainF.setVisible(true);
		((JFrame) mainF).setExtendedState(Frame.MAXIMIZED_BOTH);
		frameIcon = new ImageIcon(ACMain.class.getResource("/images/Ni3.png"));
		((JFrame) (mainF)).setIconImage(frameIcon.getImage());

		String ver = System.getProperties().getProperty("java.version");
		log.debug(ver);

		log.debug("Admin Console, version: " + version + " Copyright (C) 2006-2008");

		MainPanel2 mainPanel2 = new MainPanel2();
		mainPanel2.setSize(new Dimension(500, 300));
		mainF.add(mainPanel2);
		mainF.setVisible(true);
		mainF.setMinimumSize(calculateMinimumSize());
		((JFrame) mainF).setExtendedState(Frame.MAXIMIZED_BOTH);
	}

	static String getArgument(String name, String[] args){
		int n, l;
		l = args.length;
		name += "=";
		for (n = 0; n < l; n++){
			if (args[n].startsWith(name)){
				return args[n].substring(name.length());
			}
		}

		return null;
	}

	public void init(){

		try{
			String windowsLAF = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
			UIManager.setLookAndFeel(windowsLAF);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e){
			log.error("Error while setting up UIManager");
		}
		log.debug("Admin  Console " + version + " Copyright (C) 2006-2008");

		String ver = System.getProperties().getProperty("java.version");
		log.debug(ver);

		ScreenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		ScreenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		ServerURL = getParameter("ServerURL");
		MetaphorURL = getParameter("MetaphorURL");
		ImagesURL = getParameter("ImagesURL");
		ServerContextRoot = getParameter("ServerContextRoot");
		if (ServerContextRoot == null)
			ServerContextRoot = "ACNi3Web";
		MainPanel2 mainPanel2 = new MainPanel2();

		getContentPane().add(mainPanel2);
	}

	private static Dimension calculateMinimumSize(){
		minWidth = Math.min((int) (ScreenWidth - 10), minWidth);
		minHeight = Math.min((int) (ScreenHeight - 10), minHeight);
		return new Dimension(minWidth, minHeight);
	}

	public static Double getScreenWidth(){
	    return ScreenWidth;
    }

	public static Double getScreenHeight(){
	    return ScreenHeight;
    }

	public static Container getMainFrame(){
	    return mainF;
    }
}