/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;

import org.apache.log4j.Logger;

public class SystemIdentificationTest{
	public void testIdentificatorTest(){
		Logger.getLogger(getClass()).info(SystemIdentification.getSystemId());
	}
}
