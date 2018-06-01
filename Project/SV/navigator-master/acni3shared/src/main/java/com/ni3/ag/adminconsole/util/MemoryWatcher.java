/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;

import org.apache.log4j.Logger;

public class MemoryWatcher{
	private Logger log;

	public MemoryWatcher(String name){
		log = Logger.getLogger(name);
	}

	public void dump(){
		dump("");
	}

	public void dump(String s){
		log.debug("+++++++++++++" + s + "+++++++++++++++++++++++");
		log.debug("TOTAL: " + (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " mb");
		log.debug("FREE : " + (Runtime.getRuntime().freeMemory() / (1024 * 1024)) + " mb");
		log.debug("USED : " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024))
		        + " mb");
		log.debug("_____________________________________");
	}
}
