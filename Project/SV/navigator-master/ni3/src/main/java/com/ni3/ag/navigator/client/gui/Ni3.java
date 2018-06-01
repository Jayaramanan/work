/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Properties;
import java.awt.*;
import java.awt.event.WindowListener;
import javax.swing.*;

import com.ni3.ag.navigator.client.controller.ThickClientServiceDisposer;
import com.ni3.ag.navigator.client.controller.login.LoginController;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.client.util.Ni3UncaughtExceptionHandler;
import com.ni3.ag.navigator.client.util.Utility;
import org.apache.log4j.Logger;

public class Ni3 extends JApplet{

	private final static Logger log = Logger.getLogger(Ni3.class);

	public static Container mainF = null;

	public static final String version;

	static{
		final Properties properties = new Properties();
		String _version = "unknown";
		try{
			properties.load(Ni3.class.getResourceAsStream("/packaging.properties"));
			_version = properties.getProperty("build.version");
		} catch (IOException e){
			// ignore
		}
		version = _version;
	}

	static final long serialVersionUID = 0;

	public static boolean AppletMode = true;

	static MainPanel mainPanel;
	private static FileOutputStream lockFile;
	private static FileChannel lockFileChannel;
	private static String lockFilePath;

	public static boolean relogin(String username){
		LoginController loginController = new LoginController();
		return loginController.login(username, null, null, null, true);
	}

	public static void reloadSchema(){
		mainPanel.reloadSchema();
		JOptionPane.showMessageDialog(mainF, UserSettings.getWord("Schema_just_invalidated_last_result_can_be_invalid"));
	}

	public static void main(String[] args){
		Thread.setDefaultUncaughtExceptionHandler(new Ni3UncaughtExceptionHandler());

		AppletMode = false;
		SystemGlobals.isThickClient = getArgument("IsThickClient", args) != null;
		SystemGlobals.IconURL = getArgument("ImagesURL", args);
		SystemGlobals.MetaphorURL = getArgument("MetaphorURL", args);
		String ServerURL = getArgument("ServerURL", args);
		String ServerContextRoot = getArgument("ServerContextRoot", args);
		if (ServerContextRoot == null)
			ServerContextRoot = "Ni3Web";
		ServerContextRoot = "/" + ServerContextRoot;
		SystemGlobals.PureServerURL = ServerURL;
		SystemGlobals.ServerURL = ServerURL + ServerContextRoot;
		log.info("Server " + ServerURL);
		String Username = getArgument("Username", args);
		String Password = getArgument("Password", args);
		String SID = getArgument("SID", args);
		String SSO = getArgument("SSO", args);
		SystemGlobals.isSiebelIntegrationModeEnabled = getArgument("siebelIntegrationJSFunctionName", args) != null;

		if (!checkMemory())
			return;

		if (!checkJRE())
			return;

		if (!createLockFile()){
			JOptionPane.showMessageDialog(null, UserSettings.getWord("Another_navigator_copy_is_already_running"));
			return;
		}

		mainF = new JFrame("Ni3 Navigator");
		mainF.setMinimumSize(new Dimension(400, 300));

		((JFrame) (mainF)).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final URL resource = Ni3.class.getResource("/molecule.png");
		final ImageIcon frameIcon = new ImageIcon(resource);
		((JFrame) (mainF)).setIconImage(frameIcon.getImage());
		mainF.setVisible(true);
		((JFrame) mainF).setExtendedState(Frame.MAXIMIZED_BOTH);

		if (!SystemGlobals.isSiebelIntegrationModeEnabled){
			SplashWindow.splash(Ni3.class.getResource("/NI3-Logo.gif"));
		}

		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(mainF);
		} catch (Exception e){
			// No exception message
			log.error(e.getMessage(), e);
		}
		log.info("Ni3  " + version + " Copyright (C) 2006-2016");

		String FocusNodeID = "";
		if (getArgument("ID", args) != null){
			FocusNodeID = getArgument("ID", args);
		}

		if (SystemGlobals.isThickClient)
			((JFrame) (mainF)).addWindowListener(new ThickClientServiceDisposer());

		String Debug = getArgument("Debug", args);
		if (Debug != null){
			log.debug("****************");
			log.debug("*  DEBUG MODE  *");
			log.debug("****************");
			Utility.DEBUG = true;
		}

		mainPanel = new MainPanel();

		JMenuBar menuBar = new JMenuBar();
		((JFrame) mainF).setJMenuBar(menuBar);

		mainF.setLayout(new BorderLayout());

		mainPanel.init(null, Username, Password, SID, SSO);
		if (SystemGlobals.getUser() != null){
			EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
			while (eventQueue.peekEvent() != null);
			if (UserSettings.getBooleanAppletProperty("ShowToolbarPanel", true))
				mainF.add(mainPanel.getToolbarPanel(), BorderLayout.NORTH);
			mainF.add(mainPanel, BorderLayout.CENTER);
			((JFrame) mainF).setExtendedState(Frame.MAXIMIZED_BOTH);
			mainF.setVisible(true);
			mainPanel.paintImmediately(mainPanel.getBounds());

			if ("".equals(FocusNodeID) || (FocusNodeID == null) || FocusNodeID.isEmpty())
				mainPanel.loadDefaultFavorite();
			mainPanel.setInitialFocusNode(FocusNodeID);

			while (eventQueue.peekEvent() != null);

			mainPanel.start();

			mainPanel.checkActivityStreamStartup();
		}

		SplashWindow.disposeSplash();
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
		mainF = this;
		AppletMode = true;

		if (!checkMemory())
			return;

		if (!checkJRE())
			return;

		log.info("Ni3  " + version + " Copyright (C) 2006-2010");
		//
		SystemGlobals.IconURL = getParameter("ImagesURL");
		SystemGlobals.MetaphorURL = getParameter("MetaphorURL");
		String ServerURL = getParameter("ServerURL");
		String ServerContextRoot = getParameter("ServerContextRoot");
		if (ServerContextRoot == null)
			ServerContextRoot = "Ni3Web";
		ServerContextRoot = "/" + ServerContextRoot;

		SystemGlobals.ServerURL = ServerURL + ServerContextRoot;
		log.info("Server " + ServerURL);

		String Username = getParameter("Username");
		String Password = getParameter("Password");
		String SID = getParameter("SID");
		String SSO = getParameter("SSO");

		boolean isSiebelIntegrationMode = getParameter("siebelIntegrationJSFunctionName") != null;
		log.info("Siebel integration mode = " + isSiebelIntegrationMode);
		SystemGlobals.isSiebelIntegrationModeEnabled = isSiebelIntegrationMode;

		String FocusNodeID = "";
		if (getParameter("ID") != null){
			FocusNodeID = getParameter("ID");
		}

		if (!SystemGlobals.isSiebelIntegrationModeEnabled){
			SplashWindow.splash(Ni3.class.getResource("/NI3-Logo.gif"));
		}

		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e){
			// No exception message
			log.error(e.getMessage(), e);
		}

		// Debug
		String Debug = getParameter("Debug");
		if (Debug != null){
			log.debug("****************");
			log.debug("*  DEBUG MODE  *");
			log.debug("****************");
			Utility.DEBUG = true;
		}

		mainPanel = new MainPanel();

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		getContentPane().setLayout(new BorderLayout());

		mainPanel.init(this, Username, Password, SID, SSO);

		getContentPane().add(mainPanel, BorderLayout.CENTER);
		if (UserSettings.getBooleanAppletProperty("ShowToolbarPanel", true))
			getContentPane().add(mainPanel.getToolbarPanel(), BorderLayout.NORTH);

		setVisible(true);

		if (SystemGlobals.getUser() != null){
			getContentPane().repaint();

			EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
			// FIXME bad style, find another solution
			while (eventQueue.peekEvent() != null);

			mainPanel.loadDefaultFavorite();

			mainPanel.start();

			mainPanel.setInitialFocusNode(FocusNodeID);

			mainPanel.checkActivityStreamStartup();
		}

		SplashWindow.disposeSplash();
	}

	public static void callOnClosing(){
		if(!(mainF instanceof JFrame))
			return;
		WindowListener[] listeners = ((JFrame) (mainF)).getWindowListeners();
		for (int i = 0; i < listeners.length; i++)
			listeners[i].windowClosing(null);
	}

	public void stop(){
		if (SystemGlobals.getUser() != null){
			mainPanel.windowClosing(null);
			mainPanel.windowClosed(null);
		}
	}

	public static boolean checkMemory(){
//		if (Runtime.getRuntime().maxMemory() < 220 * 1024 * 1024){
//			JOptionPane.showMessageDialog(null,
//					"Insuficcient memory, you should set at least -Xmx256m in Java control panel JRE settings");
//
//			return false;
//		}

		return true;
	}

	public static boolean checkJRE(){
		String ver = System.getProperty("java.version");

		String target = "1.6.0";

		if (ver.compareTo(target) < 0){
			JOptionPane.showMessageDialog(null, "Version of Java VM on your computer is " + ver
					+ ", and should be at least " + target + ".\n Go to www.java.com and download latest version.");

			return false;
		}

		return true;
	}

	private static boolean createLockFile(){
		if (!SystemGlobals.isThickClient)
			return true;
		lockFilePath = System.getProperty("user.home");
		if (lockFilePath == null){
			log.error("cannot resolve user.home direcotry");
			return false;
		}

		if (!lockFilePath.endsWith(File.separator))
			lockFilePath += File.separator;
		lockFilePath += ".ni3nav.lock";

		try{
			lockFile = new FileOutputStream(lockFilePath);
			lockFileChannel = lockFile.getChannel();
			FileLock fl = lockFileChannel.tryLock();
			if (fl == null || !fl.isValid())
				return false;
			return true;
		} catch (IOException e){
			log.error("Error create lock file", e);
			return false;
		}
	}

	public static void destroyLockFile(){
		if (!SystemGlobals.isThickClient)
			return;
		try{
			lockFileChannel.close();
			lockFile.close();
			if (!new File(lockFilePath).delete())
				log.error("Error delete file " + lockFilePath);
		} catch (IOException e){
			log.error("Error closing lock file", e);
		}

	}

	public static void showServerCommunicationError(Throwable throwable, String serverError){
		String title = "Unexpected error occurred on server side/in communication with server\n";
		if (serverError != null)
			title += serverError + "\n\nLocal details: ";
		else
			title += throwable.getMessage();
		// TODO return back to true then version will be more stable
		showGenericError(title, throwable, false);
	}

	public static void showClientError(Thread thread, Throwable throwable){
		String title = "Unexpected error occurred in thread(" + thread.getId() + "): " + thread.getName() + "\n"
				+ throwable.getMessage();
		// TODO return back to true then version will be more stable
		showGenericError(title, throwable, false);
	}

	private static void showGenericError(String title, Throwable throwable, boolean showUI){
		SplashWindow.disposeSplash();
		StringBuilder titleSB = new StringBuilder();
		titleSB.append("<html>").append(title.replace("\n", "<br/>")).append("</html>");
		StringBuilder sb = new StringBuilder();
		sb.append(title).append("\n");
		fillStackTrace(throwable, sb);
		log.error(sb.toString());
		if (showUI)
			new ErrorDialog(mainF, titleSB.toString(), sb.toString()).setVisible(true);
	}

	private static void fillStackTrace(Throwable throwable, StringBuilder sb){
		if (throwable == null)
			return;
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		throwable.printStackTrace(printWriter);
		sb.append(result.toString());
		sb.append("\n------------------------------------------------------------------\n");
		if (throwable.getCause() != throwable)
			fillStackTrace(throwable.getCause(), sb);
	}
}
