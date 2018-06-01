/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.datasource;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class DeltaThreshold{
	private int okMaxRecords;
	private int warnMaxRecords;

	public DeltaThreshold(String thresholdProperty) throws ACException{
		parseThresholdProperty(thresholdProperty);
	}

	private void parseThresholdProperty(String property) throws ACException{
		try{
			StringTokenizer strt = new StringTokenizer(property, "/");
			String okProp = strt.nextToken();
			String warnProp = strt.nextToken();
			this.okMaxRecords = Integer.parseInt(okProp);
			this.warnMaxRecords = Integer.parseInt(warnProp);
		} catch (NoSuchElementException nse){
			throw new ACException(TextID.MsgInvalidDeltaThresholdProperty);
		} catch (NumberFormatException nfe){
			throw new ACException(TextID.MsgInvalidDeltaThresholdProperty);
		}
	}

	public int getOkMaxRecords(){
		return okMaxRecords;
	}

	public int getWarningMaxRecords(){
		return warnMaxRecords;
	}

}
