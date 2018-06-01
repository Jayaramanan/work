/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import com.ni3.ag.navigator.client.model.SystemGlobals;
import org.apache.log4j.Logger;

public class ThickClientServiceDisposer extends WindowAdapter{
	private static final long SLEEP_TIME = 100;
	private static final String SHUTDOWN_SCRIPTS_FILE = "shutdown_scripts.txt";

	public void windowClosing(WindowEvent e){
		if (SystemGlobals.isThickClient)
			disposeAll();
	}

	public void disposeAll(){
		try{
			URL url = getClass().getClassLoader().getResource(SHUTDOWN_SCRIPTS_FILE);
			InputStream is = url.openStream();
			Scanner scanner = new Scanner(is);
			while (scanner.hasNextLine()){
				final String nextLine = scanner.nextLine();
				final String cmd = "cmd ";
				Runtime.getRuntime().exec(cmd + nextLine);
				Thread.sleep(SLEEP_TIME);
			}
		} catch (FileNotFoundException e){
			Logger.getLogger(getClass()).error("Error closing thick client", e);
		} catch (IOException e){
			Logger.getLogger(getClass()).error("Error closing thick client", e);
		} catch (InterruptedException e){
			Logger.getLogger(getClass()).error("Error closing thick client", e);
		} catch (Throwable e){
			Logger.getLogger(getClass()).error("Error closing thick client", e);
		}
	}
}
